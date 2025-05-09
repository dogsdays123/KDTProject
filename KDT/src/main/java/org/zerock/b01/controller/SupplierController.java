package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.zerock.b01.domain.QSupplier.supplier;

//협력사(공급업체) 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '협력회사'))")
@RequestMapping("/supplier")
public class SupplierController {

    private final MaterialRepository materialRepository;

    private final ProductService productService;
    private final SupplierStockService supplierStockService;
    private final PageService pageService;

    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final UserByService userByService;
    private final MaterialService materialService;
    private final SupplierService supplierService;

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
//            SupplierDTO supplierDTO = supplierService.findByUserId(userByDTO.getUId());
//            Long sId = supplierDTO.getSId();
//            log.info("#### sId: " + sId);
        }
    }

    @GetMapping("/purchaseOrderList")
    public void purchaseOrderList() { log.info("##SUPPLIER :: PURCHASE ORDER LIST PAGE GET....##");}

    @GetMapping("/transactionHistory")
    public void transactionHistory() { log.info("##SUPPLIER :: TRANSACTION HISTORY PAGE GET....##");}

    @GetMapping("/progressInspection")
    public void progressInspection() { log.info("##SUPPLIER :: PROGRESS INSPECTION PAGE GET....##");}

    @GetMapping("/requestDelivery")
    public void requestDelivery() { log.info("##SUPPLIER :: REQUEST DELIVERY PAGE GET....##");}



    @GetMapping("/sInventoryRegister")
    public String inventoryRegister(Model model) {

        log.info("##SUPPLIER :: REGISTER PAGE GET....##");

        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        List<Material> materialList = materialService.getMaterials();
        model.addAttribute("materialList", materialList);
        return "/supplier/sInventoryRegister";
    }

    @GetMapping("/api/products/{pCode}/component-types")
    @ResponseBody
    public List<String> getComponentTypesByProductCode(@PathVariable String pCode) {
        List<String> componentTypes = materialRepository.findComponentTypesByProductCode(pCode);
        return componentTypes != null ? componentTypes : Collections.emptyList();
    }

    // 부품명을 선택하면 자재 목록을 반환
    @GetMapping("/api/materials")
    @ResponseBody
    public List<MaterialDTO> getMaterialsByComponentType(String componentType) {
        List<Material> materials = materialService.getMaterialByComponentType(componentType);
        return materials.stream()
                .map(material -> new MaterialDTO(material.getMCode(), material.getMName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/sInventoryList")
    public void inventoryList(PageRequestDTO pageRequestDTO, Model model,  Authentication auth) {
        log.info("##SUPPLIER :: INVENTORY PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        UserBySecurityDTO principal = (UserBySecurityDTO) auth.getPrincipal();
        String uId = principal.getUId();
        String role = principal.getUserJob();

        PageResponseDTO<SupplierStockDTO> responseDTO;
        List<SupplierStockDTO> supplierStockList;

        if ("관리자".equals(role)) {
            responseDTO = pageService.adminSupplierStockWithAll(pageRequestDTO);
        } else {
            // 일반 공급업체
            SupplierDTO supplierDTO = supplierService.findByUserId(uId);
            Long sId = supplierDTO.getSId();
            log.info("#### sId: " + sId);

            responseDTO = pageService.supplierStockWithAll(pageRequestDTO, sId);

        }
        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
        log.info("SStock ResponseDTO : " + responseDTO);
    }

    //  직접 등록
    @PostMapping("/sInventoryRegister")
    public String sStocksRegisterPost(@RequestParam("mCodes[]") List<String> mCodes,
                                      @RequestParam("ssNums[]") List<String> ssNums,
                                      @RequestParam("ssMinOrderQty[]") List<String> ssMinOrderQty,
                                      @RequestParam("unitPrices[]") List<String> unitPrices,
                                      @RequestParam("leadTimes[]") List<String> leadTimes,
                                      Model model, RedirectAttributes redirectAttributes,
                                      HttpServletRequest request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uId = ((UserBySecurityDTO) auth.getPrincipal()).getUId();
        SupplierDTO supplierDTO;
        try {
            supplierDTO = supplierService.findByUserId(uId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "공급업체 회원만 재고 등록이 가능합니다.");
            return "redirect:/supplier/sInventoryRegister";
        }
        Long sId = supplierDTO.getSId();

        List<SupplierStockDTO> supplierStockDTOS = new ArrayList<>();

        for (int i = 0; i < mCodes.size(); i++) {
            SupplierStockDTO supplierStockDTO = new SupplierStockDTO();
            supplierStockDTO.setSId(sId);
            supplierStockDTO.setMCode(mCodes.get(i));
            supplierStockDTO.setSsNum(ssNums.get(i));
            supplierStockDTO.setSsMinOrderQty(ssMinOrderQty.get(i));
            supplierStockDTO.setUnitPrice(unitPrices.get(i));
            supplierStockDTO.setLeadTime(leadTimes.get(i));
            supplierStockDTOS.add(supplierStockDTO);
        }

        try {
            for (SupplierStockDTO supplierStockDTO : supplierStockDTOS) {
                supplierStockService.registerSStock(supplierStockDTO);
            }
            redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage()); // 중복 알림
        }

        return "redirect:/supplier/sInventoryRegister";
    }

    @PostMapping("/addSStock")
    @ResponseBody
    public Map<String, Object> SStockRegisterAuto(@RequestParam("check") boolean check, @RequestParam("file") MultipartFile[] files, Model model, RedirectAttributes redirectAttributes) throws IOException {

        Map<String, Object> response = new HashMap<>();
        Map<String, String[]> sStockObj = new HashMap<>();
        List<String> allMCodes = new ArrayList<>();

        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            Map<String, String[]> temp = registerSStockOnController(worksheet, redirectAttributes);

            if (check) {
                if (temp.containsKey("mCodes")) {
                    allMCodes.addAll(Arrays.asList(temp.get("mCodes")));
                }
            } else {
                response.put("errorCheck", temp.get("errorCheck"));
                return response;
            }
        }

        response.put("mCodes", allMCodes.toArray(new String[0]));
        return response;
    }


    private Map<String, String[]> registerSStockOnController(XSSFSheet worksheet,  RedirectAttributes redirectAttributes) {
        List<SupplierStockDTO> supplierStockDTOS = new ArrayList<>();
        List<String> mCodes = new ArrayList<>();
        Map<String, String[]> result = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uId = ((UserBySecurityDTO) auth.getPrincipal()).getUId();
        SupplierDTO supplierDTO;
        try {
            supplierDTO = supplierService.findByUserId(uId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "공급업체 회원만 재고 등록이 가능합니다.");
            return Collections.singletonMap("error", new String[]{"공급업체 회원 아님"});
        }

        Long sId = supplierDTO.getSId();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            if (row == null) continue;

            SupplierStockDTO supplierStockDTO = new SupplierStockDTO();
            DataFormatter formatter = new DataFormatter();

            supplierStockDTO.setMCode(formatter.formatCellValue(row.getCell(0)));
            supplierStockDTO.setMName(formatter.formatCellValue(row.getCell(1)));
            supplierStockDTO.setSsNum(formatter.formatCellValue(row.getCell(3)));
            supplierStockDTO.setSsMinOrderQty(formatter.formatCellValue(row.getCell(4)));
            supplierStockDTO.setUnitPrice(formatter.formatCellValue(row.getCell(5)));
            supplierStockDTO.setLeadTime(formatter.formatCellValue(row.getCell(6)));
            supplierStockDTO.setSId(sId);
            supplierStockDTOS.add(supplierStockDTO);
            mCodes.add(supplierStockDTO.getMCode());
        }

        for (SupplierStockDTO supplierStockDTO : supplierStockDTOS) {
            supplierStockService.registerSStock(supplierStockDTO);
        }

        result.put("mCodes", mCodes.toArray(new String[0]));
        return result;
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute SupplierStockDTO supplierStockDTO, RedirectAttributes redirectAttributes, Long ssId) {
        log.info("ss modify post.....#@" + supplierStockDTO);
        supplierStockService.modifySupplierStock(supplierStockDTO, ssId);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/supplier/sInventoryList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute SupplierStockDTO supplierStockDTO, RedirectAttributes redirectAttributes, @RequestParam List<Long> ssIds) {
        log.info("pp remove post.....#@" + supplierStockDTO);
        supplierStockService.removeSupplierStock(ssIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/supplier/sInventoryList";
    }
}
