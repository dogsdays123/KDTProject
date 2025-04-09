package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.ProductService;
import org.zerock.b01.service.ProductionPerDayService;
import org.zerock.b01.service.ProductionPlanService;
import org.zerock.b01.service.UserByService;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/product")
public class ProductController {

    private final ProductionPlanService productionPlanService;
    private final ProductionPerDayService productionPerDayService;
    private final UserByService userByService;
    private final ProductService productService;

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
        log.info("##BOM LIST PAGE GET....##");
    }

    @GetMapping("/bomRegister")
    public void bomRegister() {
        log.info("##BOM REGISTER PAGE GET....##");
    }

    @GetMapping("/goodsList")
    public void productList() {
        log.info("##PRODUCT LIST PAGE GET....##");
    }

    @GetMapping("/goodsRegister")
    public void productRegister() {
        log.info("##PRODUCT REGISTER PAGE GET....##");
    }

    @PostMapping("/goodsRegister")
    public String productRegisterPost(@RequestParam("pCodes[]") List<String> pCodes,
                                      @RequestParam("pNames[]") List<String> pNames,
                                      Model model, RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {

        log.info("##PRODUCT REGISTER PAGE GET....##");

        List<ProductDTO> products = new ArrayList<>();

        // pCodes와 pNames 배열을 순회하여 Product 객체를 만들어 products 리스트에 추가
        for (int i = 0; i < pCodes.size(); i++) {
            ProductDTO product = new ProductDTO();
            product.setPCode(pCodes.get(i)); // pCode를 설정
            product.setPName(pNames.get(i)); // pName을 설정
            products.add(product); // 제품 리스트에 추가
        }

        String[] message = productService.registerProducts(products);
        String messageString = String.join(",", message);

        // 리다이렉트 시에 message를 전달
        redirectAttributes.addFlashAttribute("message", messageString);

        return "redirect:/product/goodsRegister";
    }
}
