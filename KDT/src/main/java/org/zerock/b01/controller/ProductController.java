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
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.ProductListAllDTO;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '생산부서')")
@RequestMapping("/product")
public class ProductController {


    @Value("${org.zerock.upload.readyProductPath}")
    private String readyPath;

    private final UserByService userByService;
    private final ProductService productService;
    private final PageService pageService;
    private final MaterialService materialService;
    private final ProductionPlanRepository productionPlanRepository;
    private final MaterialRepository materialRepository;
//    private final ProductRepository productRepository;

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
    @GetMapping("/goodsList")
    public void productList(PageRequestDTO pageRequestDTO, Model model) {

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<ProductListAllDTO> responseDTO =
                pageService.productListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<Product> productList = productService.getProducts();

        model.addAttribute("productList", productList);
        model.addAttribute("selectedPCode", pageRequestDTO.getPCode() != null ? pageRequestDTO.getPCode() : "");
        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("responseDTO", responseDTO);
        log.info("^&^&" + responseDTO);
    }

    @GetMapping("/goodsRegister")
    public void productRegister() {
        log.info("##PRODUCT REGISTER PAGE GET....##");
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

    //제품 직접 등록
    @PostMapping("/goodsRegister")
    public String productRegisterPost(String uId,
                                      @RequestParam("pCodes[]") List<String> pCodes,
                                      @RequestParam("pNames[]") List<String> pNames,
                                      Model model, RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {

        log.info(" ^^^^ " + uId);

        List<ProductDTO> products = new ArrayList<>();

        // pCodes와 pNames 배열을 순회하여 Product 객체를 만들어 products 리스트에 추가
        for (int i = 0; i < pCodes.size(); i++) {
            ProductDTO product = new ProductDTO();
            product.setPCode(pCodes.get(i)); // pCode를 설정
            product.setPName(pNames.get(i)); // pName을 설정
            products.add(product); // 제품 리스트에 추가
        }

        log.info(" ^^^^ " + uId);
        String[] message = productService.registerProducts(products, uId);
        String messageString = String.join(",", message);

        redirectAttributes.addFlashAttribute("message", messageString);
        return "redirect:/product/goodsRegister";
    }


    //제품 자동 등록
    @PostMapping("/addProduct")
    @ResponseBody
    public Map<String, Object> uploadProduct(String uId, @RequestParam("file") MultipartFile[] files,
                                             @RequestParam("check") String check,
                                             Model model, RedirectAttributes redirectAttributes) throws IOException {
        Map<String, Object> response = new HashMap<>();
        Map<String, String[]> duplicationProducts = new HashMap<>();

        // 엑셀 파일 처리
        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            duplicationProducts = registerProduct(worksheet, uId, check);
        }

        response.put("isAvailable", duplicationProducts.isEmpty());
        response.put("pCodes", duplicationProducts.get("pCodes"));
        response.put("pNames", duplicationProducts.get("pNames"));
        log.info("$$$ " + Arrays.toString(duplicationProducts.get("pCodes")));

        return response;
    }

    private Map<String, String[]> registerProduct(XSSFSheet worksheet, String uId, String check) {

        List<ProductDTO> productDTOs = new ArrayList<>();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            ProductDTO productDTO = new ProductDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                String productCode = formatter.formatCellValue(row.getCell(0));
                productDTO.setPCode(productCode);
            }

            String productName = formatter.formatCellValue(row.getCell(1));
            productDTO.setPName(productName);

            log.info("^^^^&&&&&5" + productDTO.getPCode());
            log.info("^^^^&&&&&6" + productDTO.getPName());
            productDTOs.add(productDTO);
        }

        if(Objects.equals(check, "true")){
            return productService.ProductCheck(productDTOs);
        } else {
            return productService.registerProductsEasy(productDTOs, uId);
        }
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute ProductDTO productDTO, RedirectAttributes redirectAttributes, String uName) {
        log.info("pp modify post.....#@" + productDTO);
        productService.modifyProduct(productDTO, uName);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/product/goodsList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute ProductDTO productDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> pCodes) {
        log.info("pp remove post.....#@" + productDTO);
        for (String pCode : pCodes) {
            if (productionPlanRepository.existsByProduct_pCode(pCode)) {
                redirectAttributes.addFlashAttribute("message", "생산 계획이 등록된 상품이 있어 삭제할 수 없습니다.");
                return "redirect:/product/goodsList";
            }
            if (materialRepository.existsByProduct_pCode(pCode)) {
                redirectAttributes.addFlashAttribute("message", "부품 등록된 상품이 있어 삭제할 수 없습니다.");
                return "redirect:/product/goodsList";
            }
        }

        productService.removeProduct(pCodes);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/product/goodsList";
    }
}
