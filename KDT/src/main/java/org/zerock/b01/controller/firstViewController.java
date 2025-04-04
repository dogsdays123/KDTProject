package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.service.UserByService;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/firstView")
public class firstViewController {

    private final UserByService userByService;

    @ModelAttribute
    public void Profile(UserBy userBy, Model model, Authentication auth, HttpServletRequest request) {
        if(auth == null) {
            log.info("aaaaaa 인증정보 없음");
            model.addAttribute("userBy", null);
        }
    }

    @GetMapping("/login")
    public void login() {
        log.info("login");
    }

    @PostMapping("/checkId")
    @ResponseBody
    public Map<String, Object> checkId(@RequestParam("uId") String uId, Model model) {
        Map<String, Object> response = new HashMap<>();

        // 아이디 중복 여부 체크
        if (userByService.readOne(uId) != null) {
            response.put("isAvailable", false); // 아이디가 이미 존재하는 경우
            model.addAttribute("checkId", false);
        } else {
            response.put("isAvailable", true);  // 아이디가 사용 가능한 경우
            model.addAttribute("checkId", true);
        }

        log.info("Id체크" + uId);

        return response; // JSON 형식으로 반환
    }

    @PostMapping("/checkEmail")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("uEmail") String uEmail, Model model) {
        Map<String, Object> response = new HashMap<>();

        // 아이디 중복 여부 체크
        if (userByService.readOneForEmail(uEmail) != null) {
            response.put("isAvailable", false); // 아이디가 이미 존재하는 경우
            model.addAttribute("checkEmail", false);
        } else {
            response.put("isAvailable", true);  // 아이디가 사용 가능한 경우
            model.addAttribute("checkEmail", true);
        }

        log.info("email체크" + uEmail);

        return response; // JSON 형식으로 반환
    }
}
