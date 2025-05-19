package org.zerock.b01.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.OrderByService;
import org.zerock.b01.service.ProductionPlanService;
import org.zerock.b01.service.UserByService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/mainPage")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class PageController {

    private final UserByService userByService;
    private final ProductionPlanService productionPlanService;
    private final OrderByService orderByService;

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
            log.info("##### 일반 로그인 사용자 정보: " + userByDTO);

            if(userByDTO.getStatus() == null){

            }

            model.addAttribute("userBy", userByDTO);
            String formattedPhone = formatPhoneNumber(userByDTO.getUPhone());
            model.addAttribute("formattedPhone", formattedPhone);
        }
    }

    @GetMapping("/main")
    public RedirectView mainView(Authentication auth, Model model, HttpServletRequest request) throws JsonProcessingException {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
        UserBySecurityDTO principal = (UserBySecurityDTO) token.getPrincipal();
        String status = principal.getStatus(); // MemberSecurityDTO에서 사용자 이름 가져오기
        String userJob = principal.getUserJob();

        if (!("승인".equals(status) || "관리자".equals(status))) {
            // '승인'이 아니면 /beforeApproval로 리다이렉트
            return new RedirectView("/mainPage/beforeApproval");
        }

        if ("승인".equals(status) || "생산부서".equals(userJob) || "구매부서".equals(userJob)
                || "자재부서".equals(userJob) || "관리자".equals(status)) {
            List<Map<String, Object>> eventList = getProductionPlanEvents();
            String eventJson = new ObjectMapper().writeValueAsString(eventList);
            model.addAttribute("events", eventJson);
        }

        if ("승인".equals(status) || "생산부서".equals(userJob) || "구매부서".equals(userJob)
                || "자재부서".equals(userJob) || "관리자".equals(status)) {
            Map<String, Double> eventMap = orderByService.getMonthlyOrderSummary();
            String eventJson = new ObjectMapper().writeValueAsString(eventMap);
            model.addAttribute("orderByEvents", eventJson);
        }

        if ("승인".equals(status) || "생산부서".equals(userJob) || "구매부서".equals(userJob)
                || "자재부서".equals(userJob) || "관리자".equals(status)) {
            // 월별 생산 계획 제품 수를 가져오는 서비스 메서드 호출
            Map<String, Map<String, Integer>> productCountMap = productionPlanService.getMonthlyProductionSummary();
            String productCountJson = new ObjectMapper().writeValueAsString(productCountMap);
            model.addAttribute("productCountEvents", productCountJson);
        }

        if ("승인".equals(status) && "협력회사".equals(userJob)) {
            return new RedirectView("/supplier/purchaseOrderList");
        }

        log.info("layout page test...");
        return null;
    }


    public List<Map<String, Object>> getProductionPlanEvents() {
        List<ProductionPlan> plans = productionPlanService.getPlans(); // pp 테이블 데이터
        List<Map<String, Object>> events = new ArrayList<>();

        for (ProductionPlan plan : plans) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", plan.getPName()); // 예: 제품명
            event.put("start", plan.getPpStart().toString()); // 날짜는 ISO 8601 형식으로
            event.put("end", plan.getPpEnd().toString());
            event.put("textColor", "black");
            if ("전기자전거A".equals(plan.getPName())) {
                event.put("color", "#FF8C9D"); // 전기자전거A 색상
            } else if ("전기자전거B".equals(plan.getPName())) {
                event.put("color", "#6EB4F7"); // 전기자전거B 색상
            } else {
                event.put("color", "#FFD97F"); // 기본 색상
            }
            events.add(event);
        }

        return events;
    }



    @GetMapping("/guide")
    public void guide() {
        log.info ("layout guide test...");
    }

    @GetMapping("/myPage")
    public void myPage() {
        log.info ("layout myPage test...");
    }

    @PostMapping("/myPage")
    public String myPagePost(@ModelAttribute UserByDTO userByDTO, Model model) {
        userByService.changeUser(userByDTO);
        // POST 처리 후 마이페이지 GET으로 리다이렉트
        return "redirect:myPage";
    }

    @PostMapping("/checkEmail")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("uEmail") String uEmail, @RequestParam("uId") String uId, Model model) {
        Map<String, Object> response = new HashMap<>();

        // 아이디 중복 여부 체크
        if (userByService.readOneForEmail(uEmail) != null && !userByService.readOneForEmail(uEmail).getUId().equals(uId)) {
            response.put("isAvailable", false); // 아이디가 이미 존재하는 경우
            model.addAttribute("checkEmail", false);
        } else {
            response.put("isAvailable", true);  // 아이디가 사용 가능한 경우
            model.addAttribute("checkEmail", true);
        }

        log.info("email체크" + uEmail);

        return response;
    }

    // 전화번호 포맷팅 메서드
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            // 01012341234 -> 010-1234-1234
            return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 7) + "-" + phoneNumber.substring(7);
        }
        return phoneNumber;  // 예외적인 경우 그대로 반환
    }

    // 관리자 가입 승인 전 메인 페이지
    @GetMapping("/beforeApproval")
    public void beforeApproval() {
    }
}
