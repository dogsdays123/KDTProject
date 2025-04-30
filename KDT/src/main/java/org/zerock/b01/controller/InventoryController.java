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
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.util.*;
import java.util.stream.Collectors;

//자재관리 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '자재부서'))")
@RequestMapping("/inventory")
public class InventoryController {


    private final MaterialService materialService;
    private final PageService pageService;
    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final UserByService userByService;
    private final ProductService productService;
    private final InventoryStockService inventoryStockService;

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
    public String inventoryRegister(Model model){
        log.info("##MATERIAL INVENTORY REGISTER PAGE GET....##");

        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        List<Material> materialList = materialService.getMaterials();
        model.addAttribute("materialList", materialList);
        return "/inventory/inventoryRegister";

    }

    @PostMapping("/inventoryRegister")
    public String inventoryRegisterPost(String uId,
                                        @RequestParam("pNames[]") List<String> pNames,
                                        @RequestParam("pCodes[]") List<String> pCodes,
                                        @RequestParam("cTypes[]") List<String> cTypes,
                                        @RequestParam("mNames[]") List<String> mNames,
                                        @RequestParam("mCodes[]") List<String> mCodes,
                                        @RequestParam("isNums[]") List<String> isNums,
                                        @RequestParam("isLoca[]") List<String> isLoca,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request){

        log.info(" ^^^^ " + uId);

        List<InventoryStockDTO> inventoryStockDTOS = new ArrayList<>();

        for (int i = 0; i < mCodes.size(); i++) {
            InventoryStockDTO inventoryStockDTO = new InventoryStockDTO();
            inventoryStockDTO.setMCode(mCodes.get(i));
            inventoryStockDTO.setIsNum(isNums.get(i));
            inventoryStockDTO.setIsAvailable(isNums.get(i));
            inventoryStockDTO.setIsLocation(isLoca.get(i));
            inventoryStockDTOS.add(inventoryStockDTO);

        }

        for(InventoryStockDTO inventoryStockDTO : inventoryStockDTOS){
            inventoryStockService.registerIS(inventoryStockDTO);
        }
        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:/inventory/inventoryRegister";
    }



    @GetMapping("/inventoryList")
    public void inventoryList(PageRequestDTO pageRequestDTO, Model model){
        log.info("##MATERIAL INVENTORY LIST PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<InventoryStockDTO> responseDTO = pageService.inventoryStockWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<InventoryStockDTO> inventoryStockList = inventoryStockService.getInventoryStockList();
        model.addAttribute("inventoryStockList", inventoryStockList);
        model.addAttribute("responseDTO", responseDTO);

        log.info("IS List : " + inventoryStockList);
        log.info("IS ResponseDTO : " + responseDTO);

        Set<String> uniquePNames = inventoryStockList.stream()
                .map(InventoryStockDTO::getPName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        model.addAttribute("pNameList", uniquePNames);

        Set<String> uniqueIsLocation = inventoryStockList.stream()
                        .map(InventoryStockDTO::getIsLocation)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        model.addAttribute("isLocationList", uniqueIsLocation);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");

    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute InventoryStockDTO inventoryStockDTO, RedirectAttributes redirectAttributes, Long isId) {
        log.info("pp modify post.....#@" + inventoryStockDTO);
        inventoryStockService.modifyIS(inventoryStockDTO, isId);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/inventory/inventoryList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute InventoryStockDTO inventoryStockDTO, RedirectAttributes redirectAttributes, @RequestParam List<Long> isIds) {
        log.info("pp remove post.....#@" + inventoryStockDTO);
        inventoryStockService.removeIS(isIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/inventory/inventoryList";
    }


}
