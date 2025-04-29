package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '구매부서')")
@RequestMapping("/supply")
public class SupplyController {

    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final ProductService productService;
    private final UserByService userByService;
    private final MaterialService materialService;
    private final BomService bomService;
    private final PageService pageService;

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
    public void materialList(PageRequestDTO pageRequestDTO, Model model) {

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<MaterialDTO> responseDTO =
                pageService.materialListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<Material> materialList = materialService.getMaterials();

        Set<String> pNameSet = materialList.stream()
                .filter(m -> m.getProduct() != null && m.getProduct().getPName() != null)
                .map(m -> m.getProduct().getPName())
                .collect(Collectors.toSet());

        model.addAttribute("pNameList", pNameSet);


        Set<String> componentTypeSet = materialList.stream()
                .filter(m -> m.getMComponentType() != null)
                .map(Material::getMComponentType)
                .collect(Collectors.toSet());

        model.addAttribute("componentTypeList", componentTypeSet);

        Set<String> mNameSet = materialList.stream()
                .filter(m -> m.getMName() != null)
                .map(Material::getMName)
                .collect(Collectors.toSet());

        model.addAttribute("mNameList", mNameSet);


        model.addAttribute("materialList", materialList);
        model.addAttribute("responseDTO", responseDTO);

        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedMType", pageRequestDTO.getMType() != null ? pageRequestDTO.getMType() : "");

        log.info("###MATERIAL LIST: " + responseDTO);
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
                                  @RequestParam("mNames[]") List<String> mNames,
                                  @RequestParam("mCodes[]") List<String> mCodes,
                                  @RequestParam("mTypes[]") List<String> mTypes,
                                  @RequestParam("mComponentTypes[]") List<String> mComponentTypes,
                                  @RequestParam("pNames[]") List<String> pNames,
                                  @RequestParam("mMinNums[]") List<String> mMinNums,
                                  @RequestParam("mDepths[]") List<Float> mDepths,
                                  @RequestParam("mHeights[]") List<Float> mHeights,
                                  @RequestParam("mWidths[]") List<Float> mWidths,
                                  @RequestParam("mWeights[]") List<Float> mWeights,
                                  @RequestParam("mUnitPrices[]") List<String> mUnitPrices,
                                  @RequestParam("supplier[]") List<String> supplier,
                                  @RequestParam("mLeadTime[]") List<String> mLeadTime,
                                  @RequestParam("uId[]") List<String> uId,
                                RedirectAttributes redirectAttributes, HttpServletRequest request) throws IOException {


                List<MaterialDTO> materialDTOs = new ArrayList<>();

                for (int i = 0; i < mCodes.size(); i++) {
                    MaterialDTO materialDTO = new MaterialDTO();
                    materialDTO.setMCode(mCodes.get(i)); // pCode를 설정
                    materialDTO.setMName(mNames.get(i));
                    materialDTO.setMType(mTypes.get(i));
                    materialDTO.setMComponentType(mComponentTypes.get(i));
                    materialDTO.setPName(pNames.get(i));
                    materialDTO.setMMinNum(mMinNums.get(i));
                    materialDTO.setMDepth(mDepths.get(i));
                    materialDTO.setMHeight(mHeights.get(i));
                    materialDTO.setMWidth(mWidths.get(i));
                    materialDTO.setMWeight(mWeights.get(i));
                    materialDTO.setMUnitPrice(mUnitPrices.get(i));
                    materialDTO.setMLeadTime(mLeadTime.get(i));
                    materialDTO.setUId(uId.get(i));
                    materialDTOs.add(materialDTO);
                }
        for(MaterialDTO materialDTO : materialDTOs) {
            materialService.registerMaterial(materialDTO, materialDTO.getUId());
        }
        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:/supply/materialRegister";
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute MaterialDTO materialDTO, RedirectAttributes redirectAttributes, String uName) {
        log.info("pp modify post.....#@" + materialDTO);
        materialService.modifyMaterial(materialDTO, uName);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/supply/materialList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute MaterialDTO materialDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> pCodes) {
        log.info("pp remove post.....#@" + materialDTO);
        materialService.removeMaterial(pCodes);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/supply/materialList";
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