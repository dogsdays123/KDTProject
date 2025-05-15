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
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.*;
import org.zerock.b01.dto.formDTO.OrderByFormDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '구매부서'))")
@RequestMapping("/orderBy")
public class OrderByController {

    private final UserByService userByService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final ProductionPlanService productionPlanService;
    private final PageService pageService;
    private final MaterialRepository materialRepository;
    private final UserByRepository userByRepository;
    private final OrderByService orderByService;
    private final OrderByRepository orderByRepository;

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

    @GetMapping("orderByRegister")
    public void orderByRegister(PageRequestDTO pageRequestDTO, Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<ProductionPlan> ppList = productionPlanService.getPlans();
        model.addAttribute("ppList", ppList);

        List<UserBy> userList = userByRepository.findAll();
        model.addAttribute("userList", userList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<DppListAllDTO> responseDTO =
                pageService.dppListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @PostMapping("/register")
    public String PostRegister(@RequestParam("uId") String uId, @ModelAttribute OrderByFormDTO form,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) throws IOException{

        List<OrderByDTO> orderByDTOs = form.getOrders();
        int index = 0;

        for(OrderByDTO orderByDTO : orderByDTOs) {
            orderByDTO.setOCode(String.valueOf(index));
            orderByService.orderByRegister(orderByDTO, uId);
            index++;
        }

        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:orderByRegister";
    }

    @GetMapping("orderByList")
    public void orderByList(PageRequestDTO pageRequestDTO, Model model) {

        List<OrderBy> orderByList = orderByRepository.findAll();
        model.addAttribute("orderByList", orderByList);

        List<String> sNameList = orderByRepository.findSupplierNames();
        model.addAttribute("sNameList", sNameList);

        List<String> mNameList = orderByRepository.findMaterialNames();
        model.addAttribute("mNameList", mNameList);

        List<UserBy> userByList = userByRepository.findAll();
        model.addAttribute("userByList", userByList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OrderByListAllDTO> responseDTO =
                pageService.orderByWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }
}
