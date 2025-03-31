package org.zerock.b01.controller.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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

    private final ProductionPlanService productionPlanService;
    private final ProductionPerDayService productionPerDayService;

    @GetMapping("/basicPage")
    public void basicPage() {
        log.info("basic");
    }

    //생산계획 등록
    @PostMapping("/addProductPlan")
    public String uploadProductPlan(@RequestParam("file") MultipartFile file, String where) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerProductPlan(worksheet);
        log.info("%%%%" + worksheet.getSheetName());

        if (where.equals("dataUpload")) {
            return "redirect:/test/basicPage";
        } else {
            log.info("데이터넘겨주기");
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