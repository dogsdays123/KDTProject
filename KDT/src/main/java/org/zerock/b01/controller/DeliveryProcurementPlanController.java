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
import org.zerock.b01.dto.DppListAllDTO;
import org.zerock.b01.dto.allDTO.PlanListAllDTO;
import org.zerock.b01.dto.formDTO.DppFormDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '구매부서'))")
@RequestMapping("/dpp")
public class DeliveryProcurementPlanController {
    private final UserByService userByService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final DppService dppService;
    private final ProductionPlanService productionPlanService;
    private final PageService pageService;
    private final MaterialService materialService;
    private final MaterialRepository materialRepository;
    private final UserByRepository userByRepository;
    private final DeliveryProcurementPlanRepository deliveryProcurementPlanRepository;
    private final ProductRepository productRepository;
    private final ProductionPlanRepository productionPlanRepository;

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

    @GetMapping("/dppRegister")
    public void dppRegister(PageRequestDTO pageRequestDTO, Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<Supplier> supplierList = supplierService.getSupplier();
        model.addAttribute("supplierList", supplierList);

        List<ProductionPlan> ppList = productionPlanService.getPlans();
        model.addAttribute("ppList", ppList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        List<UserBy> userList = userByRepository.findAll();
        model.addAttribute("userList", userList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<PlanListAllDTO> responseDTO =
                pageService.planListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/dppList")
    public void dppList(PageRequestDTO pageRequestDTO, Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<Supplier> supplierList = supplierService.getSupplier();
        model.addAttribute("supplierList", supplierList);

        List<ProductionPlan> ppList = productionPlanService.getPlans();
        model.addAttribute("ppList", ppList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        List<UserBy> userList = userByRepository.findAll();
        model.addAttribute("userList", userList);

        List<DeliveryProcurementPlan> dppList = deliveryProcurementPlanRepository.findAll();
        model.addAttribute("dppList", dppList);

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

    @GetMapping("/{mName}/mName")
    @ResponseBody
    public List<String> getMCodesByMName(@PathVariable String mName) {
        List<String> mCodes = materialRepository.findMCodeByMNameDomain(mName);
        return mCodes != null ? mCodes : Collections.emptyList();
    }

    @GetMapping("/{ppCode}/ppCode")
    @ResponseBody
    public List<String> getDppCodeByPpCode(@PathVariable String ppCode) {
        log.info("****** " + ppCode);
        List<String> ppCodes = productionPlanRepository.findDppCodeByPpCode(
                productionPlanRepository.findByProductionPlanCodeObj(ppCode)
        );
        return ppCodes != null ? ppCodes : Collections.emptyList();
    }


    @PostMapping("/register")
    public String register(@RequestParam("uId") String uId, @ModelAttribute DppFormDTO form,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) throws IOException {

        List<DeliveryProcurementPlanDTO> dppDTOs = form.getDpps();

        for(DeliveryProcurementPlanDTO dppDTO : dppDTOs) {
            dppDTO.setUId(uId);
            dppService.registerDpp(dppDTO);
        }

        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:/dpp/dppRegister";
    }
}
