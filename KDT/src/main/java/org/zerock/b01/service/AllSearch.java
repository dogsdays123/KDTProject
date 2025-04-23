package org.zerock.b01.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.dto.PlanListAllDTO;
import org.zerock.b01.dto.ProductListAllDTO;
import org.zerock.b01.dto.UserByAllDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AllSearch {
    Page<ProductListAllDTO> productSearchWithAll(String[] types, String keyword, String pCode, String pName, String uName, LocalDate regDate, Pageable pageable);
    Page<PlanListAllDTO> planSearchWithAll(String[] types, String keyword, String ppCode, String pCode, String ppNum, String pName, String uName, String ppState, LocalDate ppStart, LocalDate ppEnd, Pageable pageable);
    Page<UserByAllDTO> userBySearchWithAll(String[] types, String keyword, String uName,
                                           String userJob, String userRank, LocalDateTime modDate, String status, String uId, Pageable pageable);
}
