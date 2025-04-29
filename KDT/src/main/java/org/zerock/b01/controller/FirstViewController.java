package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.SupplierDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.UserByService;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/firstView")
public class FirstViewController {

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
            log.info("##### 일반 로그인 사용자 정보: " + userByDTO);
        }
    }

    @GetMapping("/login")
    public void login() {
        log.info("login");
    }

    @PostMapping("/join")
    public String join(@ModelAttribute("userByDTO") UserByDTO userByDTO,
                       @ModelAttribute("supplierDTO") SupplierDTO supplierDTO,
                       Model model, RedirectAttributes redirectAttributes) {

        log.info("join");
        log.info("%%%%" + userByDTO);

        //html에서 수정하기 귀찮음
        if (userByDTO.getUserJob().contains("협력회사") && userByDTO.getUserType().equals("our")) {
            String userJob = userByDTO.getUserJob().replace(",협력회사", "");
            userByDTO.setUserJob(userJob);
        }

        if (userByDTO.getUserJob().contains("협력회사") && userByDTO.getUserType().equals("other")) {
            String userJob = "협력회사";
            userByDTO.setUserJob(userJob);
        }

        try {
            userByService.join(userByDTO, supplierDTO);
        } catch (UserByService.MidExistException e) {
            redirectAttributes.addFlashAttribute("error", "uId");
            return "redirect:/firstView/join";
        }
        redirectAttributes.addFlashAttribute("result", "success");
        model.addAttribute("userDTO", userByDTO);

        return "redirect:/firstView/login";
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

    @PostMapping("/checkType")
    @ResponseBody
    public Map<String, Object> checkType(@RequestParam("userType") String userType, Model model) {
        Map<String, Object> response = new HashMap<>();

        model.addAttribute("userType", userType);

        if (userType.equals("our")) {
            log.info("userType체크" + userType);
            response.put("isAvailable", true);
        } else {
            log.info("userType체크" + userType);
            response.put("isAvailable", false);
        }

        return response; // JSON 형식으로 반환
    }

    @PostMapping("/forgot")
    @ResponseBody
    public Map<String, String> forgotPassword(@RequestParam("checkEmail") String email) {
        Map<String, String> result = new HashMap<>();

        String msg = userByService.changeUserProfile(email); // ex: "임시 비밀번호를 전송했습니다."
        result.put("msg", msg);

        return result; // JSON 형태로 응답
    }

    @PostMapping("/find")
    public String find(@ModelAttribute("userByDTO") UserByDTO userByDTO, Model model, RedirectAttributes redirectAttributes) {

        return "redirect:/firstView/login";
    }
}
