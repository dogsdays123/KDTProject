package org.zerock.b01.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.dto.PlanListAllDTO;
import org.zerock.b01.dto.ProductListAllDTO;

import java.time.LocalDate;

public interface AllSearch {
    Page<ProductListAllDTO> productSearchWithAll(String[] types, String keyword, String pCode, String pName, String uName, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<PlanListAllDTO> planSearchWithAll(String[] types, String keyword, String ppCode, String ppNum, String pName, String uName, String ppState, LocalDate ppStart, LocalDate ppEnd, Pageable pageable);
}
