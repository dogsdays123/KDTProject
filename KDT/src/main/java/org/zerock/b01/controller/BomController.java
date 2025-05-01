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
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        model.addAttribute("bomList", bomList);
        log.info("BOM bomList" + bomList);
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");
        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("responseDTO", responseDTO);
        log.info("BOM ResponseDTO" + responseDTO);
    }

    @GetMapping("/api/products/{pCode}/component-types")
    @ResponseBody
    public List<String> getComponentTypesByProductCode(@PathVariable String pCode) {
        List<String> componentTypes = materialRepository.findComponentTypesByProductCode(pCode);
        return componentTypes != null ? componentTypes : Collections.emptyList();
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

    @PostMapping("/bomRegister")
    public String bomRegisterPost(String uId,
                                  @RequestParam("pNames[]") List<String> pNames,
                                  @RequestParam("pCodes[]") List<String> pCodes,
                                  @RequestParam("cTypes[]") List<String> cTypes,
                                  @RequestParam("mNames[]") List<String> mNames,
                                  @RequestParam("mCodes[]") List<String> mCodes,
                                  @RequestParam("pQuant[]") List<String> pQuant,
                                  Model model, RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {
        log.info(" ^^^^ " + uId);

        List<BomDTO> bomDTOS = new ArrayList<>();

        for (int i = 0; i < pCodes.size(); i++) {
            BomDTO bomDTO = new BomDTO();
            bomDTO.setPCode(pCodes.get(i)); // pCode를 설정
            bomDTO.setMCode(mCodes.get(i));
            bomDTO.setBComponentType(cTypes.get(i));
            bomDTO.setBRequireNum(pQuant.get(i));
            bomDTOS.add(bomDTO);
        }

        for(BomDTO bomDTO : bomDTOS) {
            bomService.registerBOM(bomDTO);
        }
        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:/bom/bomRegister";
    }


    @PostMapping("/addBom")
    public String uploadProductPlan(String uId, @RequestParam("file") MultipartFile[] files, @RequestParam("where") String where, Model model, RedirectAttributes redirectAttributes) throws IOException {

        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            registerProductPlanOnController(uId, worksheet);
            log.info("%%%%" + worksheet.getSheetName());
        }

        if (where.equals("dataUpload")) {
            redirectAttributes.addFlashAttribute("successMessage", "(특정)데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:/productionPlan/ppRegister";
        } else {
            log.info("데이터넘겨주기");
            redirectAttributes.addFlashAttribute("successMessage", "데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:/productionPlan/ppRegister";
        }
    }


    private void registerProductPlanOnController(String uId, XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            BomDTO entity = new BomDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

//            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
//                String productionPlanCode = formatter.formatCellValue(row.getCell(0));
//                log.info("^^^^" + productionPlanCode);
//                entity.setPpCode(productionPlanCode);
//            }

            String productName = formatter.formatCellValue(row.getCell(0));
            String componentType = formatter.formatCellValue(row.getCell(1));
            String materialName = formatter.formatCellValue(row.getCell(2));
            String requireNum = formatter.formatCellValue(row.getCell(3));

            Optional<String> optionalPCode = productRepository.findPCodeByPName(productName);

            String mCode = materialRepository.findMCodeByMName(materialName)
                    .orElseThrow(() -> new IllegalArgumentException("해당 자재명을 가진 자재 코드가 없습니다: " + materialName));
            entity.setMCode(mCode);

            if (optionalPCode.isPresent()) {
                entity.setPCode(optionalPCode.get());
            } else {
                throw new IllegalArgumentException("해당 제품명을 가진 제품 코드가 없습니다: " + productName);
            }

            entity.setBComponentType(componentType);
            entity.setBRequireNum(requireNum);

            bomService.registerBOM(entity);
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
