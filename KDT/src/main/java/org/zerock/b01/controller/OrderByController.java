package org.zerock.b01.controller;

import org.springframework.web.bind.annotation.GetMapping;
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
import org.zerock.b01.domain.ProductionPlan;
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
@RequestMapping("/orderBy")
public class OrderByController {

    @GetMapping("/ppOrderPlan")
    public void orderPlan() {
        log.info("##ORDER PLAN PAGE GET....##");
    }
}
