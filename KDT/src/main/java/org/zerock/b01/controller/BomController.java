package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.formDTO.BomFormDTO;
import org.zerock.b01.dto.formDTO.MaterialFormDTO;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '생산부서'))")
@RequestMapping("/bom")
public class BomController {

    private final UserByService userByService;
    private final ProductService productService;
    private final PageService pageService;
    private final MaterialService materialService;
    private final ProductionPlanRepository productionPlanRepository;
    private final MaterialRepository materialRepository;
    private final BomService bomService;
    private final ProductRepository productRepository;

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

    @GetMapping("/bomList")
    public void bomList(PageRequestDTO pageRequestDTO, Model model) {
        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<BomDTO> responseDTO = pageService.bomListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<BomDTO> bomList = bomService.getBoms();
        List<Product> productList = productRepository.findAll();
        List<Material> materialList = materialRepository.findAll();

        model.addAttribute("productList", productList);
        model.addAttribute("materialList", materialList);
        model.addAttribute("bomList", bomList);
        log.info("BOM bomList" + bomList);
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");
        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("responseDTO", responseDTO);
        log.info("BOM ResponseDTO" + responseDTO);
    }

    @GetMapping("/{pName}/forComponentType")
    @ResponseBody
    public List<String> getComponentTypesByProductCode(@PathVariable String pName) {
        List<String> componentTypes = materialRepository.findComponentTypesByProductName(pName);
        return componentTypes != null ? componentTypes : Collections.emptyList();
    }

    @GetMapping("/{mType}/forMName")
    @ResponseBody
    public List<String> getMNameProductCode(@PathVariable String mType) {
        List<String> mNames = materialRepository.findMNameByType(mType);
        return mNames != null ? mNames : Collections.emptyList();
    }

    @GetMapping("/{pName}/{mType}")
    @ResponseBody
    public List<String> getMNames(@PathVariable String pName, @PathVariable String mType) {
        List<String> mNames = materialRepository.findMNameByETC(pName, mType);
        return mNames != null ? mNames : Collections.emptyList();
    }

    // 부품명을 선택하면 자재 목록을 반환
    @GetMapping("/api/materials")
    @ResponseBody
    public List<MaterialDTO> getMaterialsByComponentType(String componentType) {
        List<Material> materials = materialService.getMaterialByComponentType(componentType);
        return materials.stream()
                .map(material -> new MaterialDTO(material.getMCode(), material.getMName()))
                .collect(Collectors.toList());
    }


    @GetMapping("/bomRegister")
    public String bomRegister(Model model) {
        log.info("##PP REGISTER PAGE GET....##");
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        log.info("$$$$" + productList);
        List<Material> materialList = materialService.getMaterials();
        model.addAttribute("materialList", materialList);
        // 반환할 뷰 이름을 명시합니다.
        return "/bom/bomRegister";
    }

    //붐 직접 등록
    @PostMapping("/bomRegister")
    @ResponseBody
    public String bomRegisterPost(@ModelAttribute BomFormDTO form,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) throws IOException {

        Map<String, String[]> bomObj = new HashMap<>();
        List<BomDTO> bomDTOs = form.getBoms();
        bomObj = bomService.registerBOM(bomDTOs, bomDTOs.get(0).getUId());

        redirectAttributes.addFlashAttribute("errorCheck", bomObj.get("errorCheck"));
        return "redirect:/material/materialRegister";
    }

    //붐 자동 등록
    @PostMapping("/addBom")
    @ResponseBody
    public Map<String, Object> BomRegisterAuto(String uId, String check, @RequestParam("file") MultipartFile[] files,  Model model, RedirectAttributes redirectAttributes) throws IOException {

        log.info("uuuu " + uId);

        Map<String, Object> response = new HashMap<>();
        Map<String, String[]> bomObj = new HashMap<>();

        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            bomObj = registerBomOnController(uId, worksheet, check);
        }

        //materialService 에서 가져온 mCodes
        if(check.equals("true")){
            response.put("pNames", bomObj.get("pNames"));
            response.put("mCodes", bomObj.get("mCodes"));
        } else {
            response.put("errorCheck", bomObj.get("errorCheck"));
        }

        return response;
    }


    private Map<String, String[]> registerBomOnController(String uId, XSSFSheet worksheet, String check) {
        List<BomDTO> bomDTOs = new ArrayList<>();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            BomDTO bomDTO = new BomDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            bomDTO.setPCode(formatter.formatCellValue(row.getCell(0)));
            bomDTO.setPName(formatter.formatCellValue(row.getCell(1)));
            bomDTO.setMCode(formatter.formatCellValue(row.getCell(2)));
            bomDTO.setMName(formatter.formatCellValue(row.getCell(3)));
            bomDTO.setMComponentType(formatter.formatCellValue(row.getCell(4)));
            bomDTO.setBRequireNum(formatter.formatCellValue(row.getCell(5)));

            bomDTOs.add(bomDTO);
        }

        if(check.equals("true")){
            return bomService.checkBOM(bomDTOs);
        } else{
            return bomService.registerBOM(bomDTOs, uId);
        }
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute BomDTO bomDTO, RedirectAttributes redirectAttributes, Long bId) {
        log.info("pp modify post.....#@" + bomDTO);
        bomService.modifyBOM(bomDTO, bId);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/bom/bomList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute BomDTO bomDTO, RedirectAttributes redirectAttributes, @RequestParam List<Long> bIds) {
        log.info("pp remove post.....#@" + bomDTO);
        bomService.removeBOM(bIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/bom/bomList";
    }
}
