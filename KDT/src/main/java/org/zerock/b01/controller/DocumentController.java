package org.zerock.b01.controller;

import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.dto.allDTO.OrderByListAllDTO;
import org.zerock.b01.dto.allDTO.OrderByPdfDTO;
import org.zerock.b01.dto.formDTO.OrderByPdfFormDTO;
import org.zerock.b01.repository.DeliveryProcurementPlanRepository;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.OrderByRepository;
import org.zerock.b01.repository.SupplierStockRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.PageService;
import org.zerock.b01.service.PdfService;
import org.zerock.b01.service.UserByService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/document")
public class DocumentController {

    private final OrderByRepository orderByRepository;
    private final DeliveryProcurementPlanRepository deliveryProcurementPlanRepository;
    private final MaterialRepository materialRepository;
    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final UserByService userByService;
    private final PageService pageService;
    private final SupplierStockRepository supplierStockRepository;
    private final PdfService pdfService;

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

    @GetMapping("/orderDoc")
    public void orderDocList(PageRequestDTO pageRequestDTO, Model model) {

        List<OrderBy> orderByList = orderByRepository.findAll();
        model.addAttribute("orderByList", orderByList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OrderByListAllDTO> responseDTO =
                pageService.orderByWithAll(pageRequestDTO, "obd");

        for (OrderByListAllDTO dto : responseDTO.getDtoList()) {
            dto.setLeadTime(supplierStockRepository.findLeadTimeByETC(dto.getSName(), dto.getMCode()));
        }

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @PostMapping("/pdf/dg")
    public ResponseEntity<byte[]> downloadPurchaseOrderPdf(@RequestBody Map<String, List<String>> pdfs) {
        List<String> obCodes = pdfs.get("pdfs");

        if (obCodes == null || obCodes.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        OrderByPdfFormDTO orderByPdfFormDTO = new OrderByPdfFormDTO();
        List<OrderByPdfDTO> orderByPdfDTOS = new ArrayList<>();

        for (String obCode : obCodes) {
            OrderByPdfDTO orderByPdfDTO = new OrderByPdfDTO();
            OrderBy orderBy = orderByRepository.findByOrderByCode(obCode).orElseThrow();
            orderByPdfDTO.setDppCode(orderBy.getDeliveryProcurementPlan().getDppCode());
            orderByPdfDTO.setONum(orderBy.getONum());
            orderByPdfDTO.setOExpectDate(orderBy.getOExpectDate().toString());
            orderByPdfDTO.setSName(orderBy.getDeliveryProcurementPlan().getSupplier().getSName());
            orderByPdfDTO.setOrderAddress(orderBy.getOrderAddress());
            orderByPdfDTO.setORemarks(orderBy.getORemarks());
            orderByPdfDTO.setPayDate(orderBy.getPayDate());
            orderByPdfDTO.setPayMethod(orderBy.getPayMethod());
            orderByPdfDTO.setPayDocument(orderBy.getPayDocument());
            orderByPdfDTO.setUId(orderBy.getUserBy().getUId());
            orderByPdfDTOS.add(orderByPdfDTO);
        }

        orderByPdfFormDTO.setPdfs(orderByPdfDTOS);

        List<byte[]> pdfList = new ArrayList<>();

        for (OrderByPdfDTO order : orderByPdfFormDTO.getPdfs()) {
            OrderByPdfFormDTO orderByPdfFormDTO2 = new OrderByPdfFormDTO();
            List<OrderByPdfDTO> orderByPdfDTOS2 = new ArrayList<>();
            orderByPdfDTOS2.add(order);
            orderByPdfFormDTO2.setPdfs(orderByPdfDTOS2);
            byte[] pdfBytes = pdfService.createPdf(orderByPdfFormDTO2); // PDF 생성
            pdfList.add(pdfBytes);
        }

        // 여러 개의 PDF를 하나로 결합
        byte[] mergedPdf;
        try {
            mergedPdf = pdfService.mergePdfFiles(pdfList); // mergePdfFiles 메서드 호출
        } catch (IOException | DocumentException e) {
            // 예외가 발생한 경우 적절한 처리를 하세요
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error merging PDFs".getBytes());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(mergedPdf, headers, HttpStatus.OK);
    }

    @PostMapping("/pdf/preview")
    public ResponseEntity<byte[]> previewOrderPDF(@RequestBody Map<String, List<String>> pdfs, @RequestParam String type) {
        List<String> obCodes = pdfs.get("pdfs");

        if (obCodes == null || obCodes.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        OrderByPdfFormDTO orderByPdfFormDTO = new OrderByPdfFormDTO();
        List<OrderByPdfDTO> orderByPdfDTOS = new ArrayList<>();

        for (String obCode : obCodes) {
            OrderByPdfDTO orderByPdfDTO = new OrderByPdfDTO();
            OrderBy orderBy = orderByRepository.findByOrderByCode(obCode).orElseThrow();
            orderByPdfDTO.setDppCode(orderBy.getDeliveryProcurementPlan().getDppCode());
            orderByPdfDTO.setONum(orderBy.getONum());
            orderByPdfDTO.setOExpectDate(orderBy.getOExpectDate().toString());
            orderByPdfDTO.setSName(orderBy.getDeliveryProcurementPlan().getSupplier().getSName());
            orderByPdfDTO.setOrderAddress(orderBy.getOrderAddress());
            orderByPdfDTO.setORemarks(orderBy.getORemarks());
            orderByPdfDTO.setPayDate(orderBy.getPayDate());
            orderByPdfDTO.setPayMethod(orderBy.getPayMethod());
            orderByPdfDTO.setPayDocument(orderBy.getPayDocument());
            orderByPdfDTO.setUId(orderBy.getUserBy().getUId());
            orderByPdfDTOS.add(orderByPdfDTO);
        }

        orderByPdfFormDTO.setPdfs(orderByPdfDTOS);

        byte[] pdf = {};

        switch (type) {
            case "dg": pdf = pdfService.createPdf(orderByPdfFormDTO);
            break;
            case "s": pdf = pdfService.createPdf(orderByPdfFormDTO);
            break;
            default:
                break;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/{obCode}/mNameList")
    @ResponseBody
    public String getMNameByDppCode(@PathVariable String obCode) {
        return deliveryProcurementPlanRepository.findMNameByDppCodeOne(orderByRepository.findByOrderByCode(obCode).orElseThrow().getDeliveryProcurementPlan().getDppCode());
    }

    @GetMapping("/tStateDoc")
    public void tStateDocList(PageRequestDTO pageRequestDTO, Model model) {
        List<OrderBy> orderByList = orderByRepository.findAll();
        model.addAttribute("orderByList", orderByList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OrderByListAllDTO> responseDTO =
                pageService.orderByWithAll(pageRequestDTO, "obd");

        for (OrderByListAllDTO dto : responseDTO.getDtoList()) {
            dto.setLeadTime(supplierStockRepository.findLeadTimeByETC(dto.getSName(), dto.getMCode()));
        }

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }
}
