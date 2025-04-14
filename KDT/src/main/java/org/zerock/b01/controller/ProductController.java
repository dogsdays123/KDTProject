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
import org.zerock.b01.dto.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/product")
public class ProductController {

    @Value("${org.zerock.upload.readyProductPath}")
    private String readyPath;

    private final UserByService userByService;
    private final ProductService productService;
    private final PageService pageService;

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
    public void bomList() {

    }

    @GetMapping("/bomRegister")
    public String bomRegister(Model model) {
        log.info("##PP REGISTER PAGE GET....##");
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        log.info("$$$$" + productList);

        // 반환할 뷰 이름을 명시합니다.
        return "/product/bomRegister";
    }

    @GetMapping("/goodsList")
    public void productList(PageRequestDTO pageRequestDTO, Model model) {

        pageRequestDTO.setSize(10);

        PageResponseDTO<ProductListAllDTO> responseDTO =
                pageService.productListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
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

    @PostMapping("/bomRegister")
    public String bomRegisterPost(){
        return null;
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

        // 리다이렉트 시에 message를 전달
        redirectAttributes.addFlashAttribute("message", messageString);

        return "redirect:/product/goodsRegister";
    }

    //제품 자동 등록
    @PostMapping("/addProduct")
    public String uploadProduct(String uName, @RequestParam("file") MultipartFile[] files, @RequestParam("where") String where,  Model model, RedirectAttributes redirectAttributes) throws IOException {

        log.info("&^&^" + uName);

        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            registerProduct(worksheet, uName);
            log.info("%%%%" + worksheet.getSheetName());
        }

        return "redirect:/product/goodsRegister";
    }

    private void registerProduct(XSSFSheet worksheet, String uName) {

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

        productService.registerProductsEasy(productDTOs, uName);
        log.info("^^^^&&&&&4");
    }
}
