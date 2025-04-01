package org.zerock.b01.controller.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.test.ProductionPerDayDTO;
import org.zerock.b01.dto.test.ProductionPlanDTO;
import org.zerock.b01.service.test.ProductionPerDayService;
import org.zerock.b01.service.test.ProductionPlanService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class ExcelPageController {
    @Value("${org.zerock.upload.readyPath}")
    private String readyPath;

    private final ProductionPlanService productionPlanService;
    private final ProductionPerDayService productionPerDayService;

    @GetMapping("/basicPage")
    public void basicPage() {
        log.info("basic");
    }

    @GetMapping("/downloadProductPlan/{isTemplate}")
    public ResponseEntity<Resource> downloadProductPlan(@PathVariable("isTemplate") boolean isTemplate) {
        // 요청에 따라 파일을 결정
        String filePath = isTemplate ? (readyPath + "/template.xlsx") : (readyPath + "/data.xlsx");

        // 파일 시스템에서 파일을 찾음
        Resource resource = new FileSystemResource(filePath);

        // 파일이 없으면 404 반환
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 응답 헤더 설정 (파일 다운로드를 위해 Content-Disposition 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

        // 파일 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


    //생산계획 등록
    @PostMapping("/addProductPlan")
    public String uploadProductPlan(@RequestParam("file") MultipartFile file, String where, RedirectAttributes redirectAttributes) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerProductPlan(worksheet);
        log.info("%%%%" + worksheet.getSheetName());

        if (where.equals("dataUpload")) {
            redirectAttributes.addFlashAttribute("successMessage", "(특정)데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:/test/basicPage";
        } else {
            log.info("데이터넘겨주기");
            redirectAttributes.addFlashAttribute("successMessage", "데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:/test/basicPage";
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
                entity.setProductionPlanCode(productionPlanCode);
            }

            String productCode = formatter.formatCellValue(row.getCell(1));
            String productName = formatter.formatCellValue(row.getCell(2));

            //날짜 형식이라 포맷을 해줘야함
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate productionStartDate = LocalDate.parse(sdf.format(row.getCell(3).getDateCellValue()));
            LocalDate productionEndDate = LocalDate.parse(sdf.format(row.getCell(4).getDateCellValue()));

            Integer productionQuantity = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));

            entity.setProductCode(productCode);
            entity.setProductName(productName);
            entity.setProductionStartDate(productionStartDate);
            entity.setProductionEndDate(productionEndDate);
            entity.setProductionQuantity(productionQuantity);

            String productionPlanCode = productionPlanService.registerProductionPlan(entity);
            log.info("데이터 넘겨주기 2 = " + productionPlanCode);

            //6에서부터 10까지는 일별 생산량이다.
            for (int j = 0; j < 5; j++) {

                if (row.getCell(6 + j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {

                    int quantity = Integer.parseInt(formatter.formatCellValue(row.getCell(6 + j)));
                    ProductionPerDayDTO productionPerDayDTO = new ProductionPerDayDTO();
                    productionPerDayDTO.setProductionQuantity(quantity);
                    productionPerDayDTO.setProductionDate(productionStartDate.plusDays(j));
                    productionPerDayDTO.setProductionPlanCode(productionPlanCode);
                    productionPerDayService.register(productionPerDayDTO);
                }
            }
        }
    }
}