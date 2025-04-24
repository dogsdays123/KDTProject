package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.ProductService;
import org.zerock.b01.service.UserByService;

//자재관리 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/material")
public class MaterialController {
    private final ProductService productService;

    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final UserByService userByService;

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

    @GetMapping("/inventoryRegister")
    public void inventoryRegister(){ log.info("##MATERIAL INVENTORY REGISTER PAGE GET....##"); }

    @GetMapping("/inventoryList")
    public void inventoryList(){ log.info("##MATERIAL INVENTORY LIST PAGE GET....##"); }

    @GetMapping("/receipt")
    public void receipt(){ log.info("##MATERIAL RECEIPT PAGE GET....##"); }

    @GetMapping("/receiptList")
    public void receiptList(){ log.info("##MATERIAL RECEIPT LIST PAGE GET....##"); }

    @GetMapping("/delivery")
    public void delivery(){ log.info("##MATERIAL DELIVERY PAGE GET....##"); }

    @GetMapping("/deliveryList")
    public void deliveryList(){ log.info("##MATERIAL DELIVERY LIST PAGE GET....##"); }
}
