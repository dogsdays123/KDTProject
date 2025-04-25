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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.PageService;
import org.zerock.b01.service.ProductService;
import org.zerock.b01.service.ProductionPlanService;
import org.zerock.b01.service.UserByService;

import java.io.IOException;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/memberManagement")
public class MemberManagementController {

    private final PageService pageService;
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

    @GetMapping("/employeeList")
    public void employeeList() {
        log.info("##EMPLOYEE LIST PAGE GET....##");
    }


    @GetMapping("/supplierList")
    public void supplierList() {
        log.info("##supplierList LIST PAGE GET....##");
    }

    @GetMapping("/employeeApproval")
    public void eaList(PageRequestDTO pageRequestDTO, Model model) {

        log.info("테스트 ");

        pageRequestDTO.setSize(10);

        PageResponseDTO<UserByAllDTO> responseDTO =
                pageService.userByWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);

        log.info("^&^&" + responseDTO);
    }

    @PostMapping("/employeeApprovalAgree")
    public String employeeApprovalAgree(@RequestParam("uId") List<String> uId, @RequestParam("userRank") List<String> userRank,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws IOException {

        log.info("BBBBB " + uId);
        for (int i = 0; i < uId.size(); i++) {
            String id = uId.get(i);
            String ur = userRank.get(i);

            userByService.agreeEmployee(id, ur);
        }

        return "redirect:/memberManagement/employeeApproval";
    }


    @GetMapping("/supplierApproval")
    public void supplierApproval(PageRequestDTO pageRequestDTO, Model model) {
        pageRequestDTO.setSize(10);

        PageResponseDTO<SupplierAllDTO> responseDTO =
                pageService.supplierWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);

        log.info("^&^& " + responseDTO);
    }

    @PostMapping("/supplierApprovalAgree")
    public String supplierApprovalAgree(@RequestParam("uId") List<String> uId,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws IOException {

        for (String uid : uId){
            userByService.agreeSupplier(uid);
        }

        return "redirect:/memberManagement/supplierApproval";
    }

    @GetMapping("/roleSet")
    public void roleSet() {
        log.info("##roleSet PAGE GET....##");
    }
}
