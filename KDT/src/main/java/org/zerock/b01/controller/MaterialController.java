package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.formDTO.MaterialFormDTO;
import org.zerock.b01.dto.formDTO.ProductionPlanFormDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//자재관리 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '생산부서'))")
@RequestMapping("/material")
public class MaterialController {

    private final ProductService productService;
    private final MaterialService materialService;
    private final UserByService userByService;
    private final BomService bomService;
    private final PageService pageService;
    private final SupplierService supplierService;

    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    @ModelAttribute
    public void Profile(UserByDTO userByDTO, Model model, Authentication auth, HttpServletRequest request) {
        if(auth == null) {
            log.info("aaaaaa 인증정보 없음");
            model.addAttribute("userBy", null);
        } else {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;

            // token.getPrincipal()이 MemberSecurityDTO 타입이라면, 이를 MemberSecurityDTO로 캐스팅
            UserBySecurityDTO principal = (UserBySecurityDTO) token.getPrincipal();
            String username = principal.getUId(); // MemberSecurityDTO에서 사용자 이름 가져오기

            // 일반 로그인 사용자 정보 가져오기
            userByDTO = userByService.readOne(username);
            log.info("#### 일반 로그인 사용자 정보: " + userByDTO);

            model.addAttribute("userBy", userByDTO);
        }
    }

    @GetMapping("/materialList")
    public void materialList(PageRequestDTO pageRequestDTO, Model model) {

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<MaterialDTO> responseDTO =
                pageService.materialListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<Material> materialList = materialService.getMaterials();

        Set<String> pNameSet = materialList.stream()
                .filter(m -> m.getProduct() != null && m.getProduct().getPName() != null)
                .map(m -> m.getProduct().getPName())
                .collect(Collectors.toSet());

        model.addAttribute("pNameList", pNameSet);


        Set<String> componentTypeSet = materialList.stream()
                .filter(m -> m.getMComponentType() != null)
                .map(Material::getMComponentType)
                .collect(Collectors.toSet());

        model.addAttribute("componentTypeList", componentTypeSet);

        Set<String> mNameSet = materialList.stream()
                .filter(m -> m.getMName() != null)
                .map(Material::getMName)
                .collect(Collectors.toSet());

        model.addAttribute("mNameList", mNameSet);


        model.addAttribute("materialList", materialList);
        model.addAttribute("responseDTO", responseDTO);

        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedMType", pageRequestDTO.getMType() != null ? pageRequestDTO.getMType() : "");

        log.info("###MATERIAL LIST: " + responseDTO);
    }

    @GetMapping("/materialRegister")
    public String materialRegister(Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<Bom> bomList = bomService.getBoms();
        model.addAttribute("bomList", bomList);

        List<Supplier> supplierList = supplierService.getSupplier();
        model.addAttribute("supplierList", supplierList);

        // 반환할 뷰 이름을 명시합니다.
        return "/material/materialRegister";
    }

    //부품 직접 등록
    @PostMapping("/addMaterialSelf")
    public String addMaterialSelf(@ModelAttribute MaterialFormDTO form,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) throws IOException {


        List<MaterialDTO> materialDTOs = form.getMaterials();

        for(MaterialDTO materialDTO : materialDTOs) {
            materialService.registerMaterial(materialDTO, materialDTO.getUId());
        }

        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:/material/materialRegister";
    }

    //부품 자동 등록
    @PostMapping("/addMaterial")
    @ResponseBody
    public Map<String, Object> addMaterial(String uId, @RequestParam("file") MultipartFile[] files,
                                             @RequestParam("check") String check,
                                              Model model, RedirectAttributes redirectAttributes) throws IOException {
        log.info("test ");

        Map<String, Object> response = new HashMap<>();
        Map<String, String[]> materialObj = new HashMap<>();

        // 엑셀 파일 처리
        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            materialObj = registerMaterialForExel(worksheet, uId, check);
        }

        //materialService 에서 가져온 mCodes
        response.put("mCodes", materialObj.get("mCodes"));
        response.put("errorCheck", materialObj.get("error"));

        return response;
    }

    private Map<String, String[]> registerMaterialForExel(XSSFSheet worksheet, String uId, String check) {

        List<MaterialDTO> materialDTOs = new ArrayList<>();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++){
            MaterialDTO materialDTO = new MaterialDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            materialDTO.setPName(formatter.formatCellValue(row.getCell(0)));
            materialDTO.setMComponentType(formatter.formatCellValue(row.getCell(1)));
            materialDTO.setMType(formatter.formatCellValue(row.getCell(2)));
            materialDTO.setMName(formatter.formatCellValue(row.getCell(3)));
            materialDTO.setMCode(formatter.formatCellValue(row.getCell(4)));
            materialDTO.setSName(formatter.formatCellValue(row.getCell(5)));
            materialDTO.setMLeadTime(formatter.formatCellValue(row.getCell(6)));
            materialDTO.setMDepth(Float.parseFloat(formatter.formatCellValue(row.getCell(7))));
            materialDTO.setMHeight(Float.parseFloat(formatter.formatCellValue(row.getCell(8))));
            materialDTO.setMWidth(Float.parseFloat(formatter.formatCellValue(row.getCell(9))));
            materialDTO.setMWeight(Float.parseFloat(formatter.formatCellValue(row.getCell(10))));
            materialDTO.setMUnitPrice(formatter.formatCellValue(row.getCell(11)));
            materialDTO.setMMinNum(formatter.formatCellValue(row.getCell(12)));

            materialDTOs.add(materialDTO);
        }

        if(check.equals("true")){
            return materialService.materialCheck(materialDTOs);
        } else {
            return materialService.registerMaterialEasy(materialDTOs, uId);
        }
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute MaterialDTO materialDTO, RedirectAttributes redirectAttributes, String uName) {
        log.info("pp modify post.....#@" + materialDTO);
        materialService.modifyMaterial(materialDTO, uName);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/material/materialList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute MaterialDTO materialDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> pCodes) {
        log.info("pp remove post.....#@" + materialDTO);
        materialService.removeMaterial(pCodes);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/material/materialList";
    }
}
