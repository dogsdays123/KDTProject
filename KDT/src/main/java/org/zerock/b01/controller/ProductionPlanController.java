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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.PageService;
import org.zerock.b01.service.ProductService;
import org.zerock.b01.service.ProductionPlanService;
import org.zerock.b01.service.UserByService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/productionPlan")
public class ProductionPlanController {

    private final ProductService productService;
    private final PageService pageService;

    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final ProductionPlanService productionPlanService;
    private final UserByService userByService;

    @ModelAttribute
    public void Profile(UserByDTO userByDTO, Model model, Authentication auth, HttpServletRequest request) {
        if (auth == null) {
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

    @GetMapping("/ppRegister")
    public String register(Model model) {
        log.info("##PP REGISTER PAGE GET....##");
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        log.info("$$$$" + productList);

        // 반환할 뷰 이름을 명시합니다.
        return "/productionPlan/ppRegister";
    }

    @PostMapping("ppRegister")
    public void registerPost(ProductionPlanDTO productionPlanDTO, Model model, HttpServletRequest request) {
        log.info("##PP REGISTER PAGE POST....##");
        log.info(productionPlanDTO);
    }

    @GetMapping("/ppOrderPlan")
    public void orderPlan() {
        log.info("##ORDER PLAN PAGE GET....##");
    }

    @GetMapping("/ppList")
    public void planList(PageRequestDTO pageRequestDTO, Model model) {

        pageRequestDTO.setSize(10);

        PageResponseDTO<PlanListAllDTO> responseDTO =
                pageService.planListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<ProductionPlan> planList = productionPlanService.getPlans();
        model.addAttribute("planList", planList);
        model.addAttribute("responseDTO", responseDTO);

        log.info("^&^&" + responseDTO);
    }

    @GetMapping("/downloadProductPlan/{isTemplate}")
    public ResponseEntity<Resource> downloadProductPlan(@PathVariable("isTemplate") boolean isTemplate) {
        // 요청에 따라 파일을 결정
        String filePath = isTemplate ? (readyPath + "/template.xlsx") : (readyPath + "/data.xlsx");

        // 파일 시스템에서 파일을 찾음
        Resource resource = new FileSystemResource(filePath);

        // 파일이 없으면 404 반환
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 응답 헤더 설정 (파일 다운로드를 위해 Content-Disposition 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

        // 파일 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    //생산계획 직접등록
    @PostMapping("/addProductPlanSelf")
    public String uploadProductPlanSelf(@ModelAttribute ProductionPlanFormDTO form,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws IOException {

        List<ProductionPlanDTO> productionPlanDTOs = form.getPlans();

        // pCodes와 pNames 배열을 순회하여 Product 객체를 만들어 products 리스트에 추가
        for (ProductionPlanDTO productionPlanDTO : productionPlanDTOs) {
            productionPlanService.registerProductionPlan(productionPlanDTO, productionPlanDTO.getUId());
        }

        return "redirect:/productionPlan/ppRegister";
    }

    //생산계획 자동 등록
    @PostMapping("/addProductPlan")
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

            ProductionPlanDTO entity = new ProductionPlanDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                String productionPlanCode = formatter.formatCellValue(row.getCell(0));
                log.info("^^^^" + productionPlanCode);
                entity.setPpCode(productionPlanCode);
            }

            String productCode = formatter.formatCellValue(row.getCell(1));
            String productName = formatter.formatCellValue(row.getCell(2));

            //날짜 형식이라 포맷을 해줘야함
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate productionStartDate = LocalDate.parse(sdf.format(row.getCell(3).getDateCellValue()));
            LocalDate productionEndDate = LocalDate.parse(sdf.format(row.getCell(4).getDateCellValue()));

            Integer productionQuantity = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));

            entity.setPppCode(productCode);
            entity.setPName(productName);
            entity.setPpStart(productionStartDate);
            entity.setPpEnd(productionEndDate);
            entity.setPpNum(productionQuantity);

            String productionPlanCode = productionPlanService.registerProductionPlan(entity, uId);
            log.info("데이터 넘겨주기 2 = " + productionPlanCode);

//            //6에서부터 10까지는 일별 생산량이다.
//            for (int j = 0; j < 30; j++) {
//
//                if (row.getCell(6 + j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
//
//                    int quantity = Integer.parseInt(formatter.formatCellValue(row.getCell(6 + j)));
//                    ProductionPerDayDTO productionPerDayDTO = new ProductionPerDayDTO();
//                    productionPerDayDTO.setPpdNum(quantity);
//                    productionPerDayDTO.setPpdDate(productionStartDate.plusDays(j));
//                    productionPerDayDTO.setPpCode(productionPlanCode);
//                    productionPerDayService.register(productionPerDayDTO);
//
//                } else {
//                    ProductionPerDayDTO productionPerDayDTO = new ProductionPerDayDTO();
//                    productionPerDayDTO.setPpdNum(0);
//                    productionPerDayDTO.setPpdDate(productionStartDate.plusDays(j));
//                    productionPerDayDTO.setPpCode(productionPlanCode);
//                    productionPerDayService.register(productionPerDayDTO);
//                }
//            }
        }
    }
}
