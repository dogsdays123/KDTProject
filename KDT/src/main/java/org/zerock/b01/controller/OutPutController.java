package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '자재부서'))")
@RequestMapping("/outPut")
public class OutPutController {

    private final UserByService userByService;

    private final InventoryStockService inventoryStockService;

    private final PageService pageService;
    private final OutputService outputService;

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

    @GetMapping("/outPutManage")
    public void outPutInventoryList(PageRequestDTO pageRequestDTO, Model model){
        log.info("##OUTPUT INVENTORY LIST PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10);
        }

        PageResponseDTO<InventoryStockDTO> responseDTO = pageService.inventoryStockWithAll(pageRequestDTO);

        List<InventoryStockDTO> filteredList = responseDTO.getDtoList().stream()
                .filter(dto -> dto.getIsNum() != null && dto.getIsNum() > 0)
                .collect(Collectors.toList());

        PageResponseDTO<InventoryStockDTO> filteredResponseDTO = PageResponseDTO.<InventoryStockDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(filteredList)
                .total(filteredList.size()) // 필터링된 수량 기준으로 total 조정
                .build();

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<InventoryStockDTO> inventoryStockList = inventoryStockService.getInventoryStockList();
        model.addAttribute("inventoryStockList", inventoryStockList);
        model.addAttribute("responseDTO", filteredResponseDTO);

        log.info("IS List : " + inventoryStockList);
        log.info("IS ResponseDTO : " + filteredResponseDTO);

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

        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");
    }

    @GetMapping("/outPutList")
    public void outPutList(PageRequestDTO pageRequestDTO, Model model){

        log.info("##MATERIAL DELIVERY LIST PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OutPutDTO> responseDTO = pageService.outputWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<OutPutDTO> outPutDTOList = outputService.getOutputs();
        Set<String> uniqueMNames = outPutDTOList.stream()
                .map(OutPutDTO::getMName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        model.addAttribute("uniqueMNameList", uniqueMNames);

        model.addAttribute("outPutDTOList", outPutDTOList);
        model.addAttribute("responseDTO", responseDTO);
        log.info("OP List : " + outPutDTOList);
        log.info("OP ResponseDTO : " + responseDTO);

        Set<CurrentStatus> opStateSet = outPutDTOList.stream()
                .map(OutPutDTO::getOpState)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        log.info("opStateSet : " + opStateSet);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedOPState", pageRequestDTO.getOpState() != null ? pageRequestDTO.getOpState() : "");
        model.addAttribute("opStateSet", opStateSet);
    }

    @PostMapping("/outputRegister")
    public String inventoryRegisterPost(String uId, OutPutDTO outPutDTO, Model model, RedirectAttributes redirectAttributes){

        log.info(" ^^^^ " + uId);

        outputService.registerOutput(outPutDTO);
        redirectAttributes.addFlashAttribute("message", "출고 처리가 완료되었습니다.");
        return "redirect:outPutManage";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute OutPutDTO outPutDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> opIds) {
        log.info("pp remove post.....#@" + outPutDTO);
        outputService.removeOutput(opIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:outPutList";
    }

    @PostMapping("/confirm")
    public String confirm(@ModelAttribute OutPutDTO outPutDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> opIds) {
        log.info("pp remove post.....#@" + outPutDTO);
        outputService.confirmOutput(opIds);
        redirectAttributes.addFlashAttribute("message", "출고 처리 확정이 완료되었습니다.");
        return "redirect:outPutList";
    }
}
