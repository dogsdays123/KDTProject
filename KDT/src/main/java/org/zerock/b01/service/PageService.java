package org.zerock.b01.service;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.PlanListAllDTO;
import org.zerock.b01.dto.ProductListAllDTO;

public interface PageService {
    PageResponseDTO<PlanListAllDTO> planListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<ProductListAllDTO> productListWithAll(PageRequestDTO pageRequestDTO);

}
