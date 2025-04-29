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
import org.zerock.b01.dto.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//자재관리 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '생산부서')")
@RequestMapping("/material")
public class MaterialController {

    private final ProductService productService;
    private final MaterialService materialService;
    private final UserByService userByService;
    private final BomService bomService;
    private final PageService pageService;

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

        // 반환할 뷰 이름을 명시합니다.
        return "/material/materialRegister";
    }

    @PostMapping("/addMaterialSelf")
    public String addMaterialSelf(@ModelAttribute MaterialFormDTO form, Model model,
                                  @RequestParam("mNames[]") List<String> mNames,
                                  @RequestParam("mCodes[]") List<String> mCodes,
                                  @RequestParam("mTypes[]") List<String> mTypes,
                                  @RequestParam("mComponentTypes[]") List<String> mComponentTypes,
                                  @RequestParam("pNames[]") List<String> pNames,
                                  @RequestParam("mMinNums[]") List<String> mMinNums,
                                  @RequestParam("mDepths[]") List<Float> mDepths,
                                  @RequestParam("mHeights[]") List<Float> mHeights,
                                  @RequestParam("mWidths[]") List<Float> mWidths,
                                  @RequestParam("mWeights[]") List<Float> mWeights,
                                  @RequestParam("mUnitPrices[]") List<String> mUnitPrices,
                                  @RequestParam("supplier[]") List<String> supplier,
                                  @RequestParam("mLeadTime[]") List<String> mLeadTime,
                                  @RequestParam("uId[]") List<String> uId,
                                  RedirectAttributes redirectAttributes, HttpServletRequest request) throws IOException {


        List<MaterialDTO> materialDTOs = new ArrayList<>();

        for (int i = 0; i < mCodes.size(); i++) {
            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setMCode(mCodes.get(i)); // pCode를 설정
            materialDTO.setMName(mNames.get(i));
            materialDTO.setMType(mTypes.get(i));
            materialDTO.setMComponentType(mComponentTypes.get(i));
            materialDTO.setPName(pNames.get(i));
            materialDTO.setMMinNum(mMinNums.get(i));
            materialDTO.setMDepth(mDepths.get(i));
            materialDTO.setMHeight(mHeights.get(i));
            materialDTO.setMWidth(mWidths.get(i));
            materialDTO.setMWeight(mWeights.get(i));
            materialDTO.setMUnitPrice(mUnitPrices.get(i));
            materialDTO.setMLeadTime(mLeadTime.get(i));
            materialDTO.setUId(uId.get(i));
            materialDTOs.add(materialDTO);
        }
        for(MaterialDTO materialDTO : materialDTOs) {
            materialService.registerMaterial(materialDTO, materialDTO.getUId());
        }
        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:/material/materialRegister";
    }

    //제품 자동 등록
    @PostMapping("/addMaterial")
    @ResponseBody
    public Map<String, Object> uploadProduct(String uId, @RequestParam("file") MultipartFile[] files,
                                             @RequestParam("where") String where,
                                             @RequestParam("whereToGo") String whereToGo,
                                             Model model, RedirectAttributes redirectAttributes) throws IOException {

        Map<String, Object> response = new HashMap<>();
        List<String> allMessages = new ArrayList<>();

        // 엑셀 파일 처리
        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            String[] fileMessages = registerProduct(worksheet, uId);
            allMessages.addAll(Arrays.asList(fileMessages));
        }

        String messageString = String.join(", ", allMessages);
        response.put("message", messageString);
        response.put("isAvailable", allMessages.isEmpty());

        return response;
    }

    private String[] registerProduct(XSSFSheet worksheet, String uId) {

        List<MaterialDTO> materialDTOS = new ArrayList<>();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            MaterialDTO materialDTO = new MaterialDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            if (row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                String materialCode = formatter.formatCellValue(row.getCell(4));
                materialDTO.setMCode(materialCode);
            }

            String pName = formatter.formatCellValue(row.getCell(0));
            materialDTO.setPName(pName);
            String mComponentType = formatter.formatCellValue(row.getCell(1));
            materialDTO.setMComponentType(mComponentType);
            String mType = formatter.formatCellValue(row.getCell(2));
            materialDTO.setMType(mType);
            String mName = formatter.formatCellValue(row.getCell(3));
            materialDTO.setMName(mName);
//            String sName = formatter.formatCellValue(row.getCell(5));
//            materialDTO.setSName(sName);
            String mLeadTime = formatter.formatCellValue(row.getCell(6));
            materialDTO.setMLeadTime(mLeadTime);
            String depthStr = formatter.formatCellValue(row.getCell(7));
            Float mDepth = (Float) Float.parseFloat(depthStr);
            materialDTO.setMDepth(mDepth);
            String heightStr = formatter.formatCellValue(row.getCell(8));
            Float mHeight = (Float) Float.parseFloat(heightStr);
            materialDTO.setMHeight(mHeight);
            String widthStr = formatter.formatCellValue(row.getCell(9));
            Float mWidth = (Float) Float.parseFloat(widthStr);
            materialDTO.setMWidth(mWidth);
            String weightStr = formatter.formatCellValue(row.getCell(10));
            Float mWeight = (Float) Float.parseFloat(weightStr);
            materialDTO.setMWeight(mWeight);
            String mUnitPrice = formatter.formatCellValue(row.getCell(11));
            materialDTO.setMUnitPrice(mUnitPrice);
            String mMinNum = formatter.formatCellValue(row.getCell(12));
            materialDTO.setMMinNum(mMinNum);

            log.info("^^^^&&&&&5" + materialDTO.getMCode());
            log.info("^^^^&&&&&6" + materialDTO.getPName());
            materialDTOS.add(materialDTO);
        }
//        String[] resultMessages = productService.registerProductsEasy(materialDTOS, uId);
//        log.info("Returned messages from registerProductsEasy: " + Arrays.toString(resultMessages));
//        return resultMessages;
        return materialService.registerMaterialEasy(materialDTOS, uId);
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
