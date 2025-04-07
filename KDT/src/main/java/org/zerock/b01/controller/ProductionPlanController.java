package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.ProductionPerDayDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.ProductionPerDayService;
import org.zerock.b01.service.ProductionPlanService;
import org.zerock.b01.service.UserByService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/productionPlan")
public class ProductionPlanController {

    @Value("${org.zerock.upload.readyPath}")
    private String readyPath;

    private final ProductionPlanService productionPlanService;
    private final ProductionPerDayService productionPerDayService;
    private final UserByService userByService;

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

    @GetMapping("/ppRegister")
    public void register() {
        log.info("##REGISTER PAGE GET....##");
    }

    @GetMapping("/ppList")
    public void list() {
        log.info("##LIST PAGE GET....##");
    }

    //생산계획 등록
    @PostMapping("/addProductPlan")
    public String uploadProductPlan(@RequestParam("file") MultipartFile file, String where, Model model, RedirectAttributes redirectAttributes) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerProductPlan(worksheet);
        log.info("%%%%" + worksheet.getSheetName());

        if (where.equals("dataUpload")) {
            redirectAttributes.addFlashAttribute("successMessage", "(특정)데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:/productionPlan/ppRegister";
        } else {
            log.info("데이터넘겨주기");
            redirectAttributes.addFlashAttribute("successMessage", "데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:/productionPlan/ppRegister";
        }
    }


    private void registerProductPlan(XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            ProductionPlanDTO entity = new ProductionPlanDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                String productionPlanCode = formatter.formatCellValue(row.getCell(0));
                log.info("^^^^" + productionPlanCode);
                entity.setPpCode(productionPlanCode);
            }

            String productCode = formatter.formatCellValue(row.getCell(1));
            String productName = formatter.formatCellValue(row.getCell(2));

            //날짜 형식이라 포맷을 해줘야함
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate productionStartDate = LocalDate.parse(sdf.format(row.getCell(3).getDateCellValue()));
            LocalDate productionEndDate = LocalDate.parse(sdf.format(row.getCell(4).getDateCellValue()));

            Integer productionQuantity = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));

            entity.setPppCode(productCode);
            entity.setPpName(productName);
            entity.setPpStart(productionStartDate);
            entity.setPpEnd(productionEndDate);
            entity.setPpNum(productionQuantity);

            String productionPlanCode = productionPlanService.registerProductionPlan(entity);
            log.info("데이터 넘겨주기 2 = " + productionPlanCode);

            //6에서부터 10까지는 일별 생산량이다.
            for (int j = 0; j < 5; j++) {

                if (row.getCell(6 + j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {

                    int quantity = Integer.parseInt(formatter.formatCellValue(row.getCell(6 + j)));
                    ProductionPerDayDTO productionPerDayDTO = new ProductionPerDayDTO();
                    productionPerDayDTO.setPpdNum(quantity);
                    productionPerDayDTO.setPpdDate(productionStartDate.plusDays(j));
                    productionPerDayDTO.setPpCode(productionPlanCode);
                    productionPerDayService.register(productionPerDayDTO);
                }
            }
        }
    }
}
