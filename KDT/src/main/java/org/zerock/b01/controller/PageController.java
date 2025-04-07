package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.service.UserByService;

@Log4j2
@Controller
@RequestMapping("/mainPage")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class PageController {

    private final UserByService userByService;

    @ModelAttribute
    public void Profile(UserBy userBy, Model model, Authentication auth, HttpServletRequest request) {

        log.info("profile@@ = " + userBy);
        log.info("profile@@ = " + auth.getName());

        if(auth == null) {
            log.info("aaaaaa 인증정보 없음");
            model.addAttribute("userBy", null);
        } else {
            UserByDTO userByDTO = userByService.readOne(auth.getName());
            model.addAttribute("userBy", userByDTO);
            log.info("^^^^" + userByDTO);
        }
    }

    @GetMapping("/main")
    public void main() {
        log.info ("layout page test...");
    }
}
