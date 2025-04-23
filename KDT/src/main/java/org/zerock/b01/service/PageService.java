package org.zerock.b01.service;

import org.zerock.b01.dto.*;

public interface PageService {
    PageResponseDTO<PlanListAllDTO> planListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<ProductListAllDTO> productListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<UserByAllDTO> userByWithAll(PageRequestDTO pageRequestDTO);

}
