package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.MaterialFormDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("authentication.principal.status == '승인' && (authentication.principal.userJob == '생산부서' || authentication.principal.userJob == '구매부서' || authentication.principal.userJob == '관리자')")
@RequestMapping("/supply")
public class SupplyController {

    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final ProductService productService;
    private final UserByService userByService;
    private final MaterialService materialService;
    private final BomService bomService;

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

    @GetMapping("/materialList")
    public void materialList() {
        log.info("##MATERIAL LIST PAGE GET....##");
    }

    @GetMapping("/materialRegister")
    public String materialRegister(Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<Bom> bomList = bomService.getBoms();
        model.addAttribute("bomList", bomList);

        // 반환할 뷰 이름을 명시합니다.
        return "/supply/materialRegister";
    }

    @GetMapping("/procureRegister")
    public void procureRegister() {
        log.info("##PROCURE REGISTER PAGE GET....##");
    }

    @GetMapping("/procureList")
    public void procureList() {
        log.info("##PROCURE LIST PAGE GET....##");
    }

    @GetMapping("/purchaseOrder")
    public void purchaseOrder() {
        log.info("##PURCHASE ORDER PAGE GET....##");
    }

    @GetMapping("/purchaseOrderList")
    public void purchaseOrderList() {
        log.info("##PURCHASE OREDER LIST PAGE GET....##");
    }

    @GetMapping("/progressInspection")
    public void progressInspection() {
        log.info("##PROGRESS INSPECTION PAGE GET....##");
    }

    @GetMapping("/requestDelivery")
    public void requestDelivery() {
        log.info("##REQUEST DELIVERY PAGE GET....##");
    }

    @PostMapping("/addMaterialSelf")
    public String addMaterialSelf(@ModelAttribute MaterialFormDTO form, Model model,
                                RedirectAttributes redirectAttributes, HttpServletRequest request) throws IOException {

        List<MaterialDTO> materialDTOs = form.getMaterials();

        for(MaterialDTO materialDTO : materialDTOs) {
            materialService.registerMaterial(materialDTO, materialDTO.getUId());
        }

        return "redirect:/supply/materialRegister";
    }

    @GetMapping("/purchaseOrderStatus")
    public void purchaseOrderStatus() {
        log.info("##PURCHASE ORDER STATUS PAGE GET....##");
    }

    @GetMapping("/transactionStatement")
    public void transactionStatement() {
        log.info("##TRANSACTION STATEMENT PAGE GET....##");
    }
}