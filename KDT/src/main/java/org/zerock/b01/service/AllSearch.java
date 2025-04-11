package org.zerock.b01.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.dto.ProductListAllDTO;

import java.time.LocalDate;

public interface AllSearch {
    Page<ProductListAllDTO> searchWithAll(String[] types, String keyword, String pCode, String pName, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
