package org.zerock.b01.service;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ProductListAllDTO;

public interface ProductPageService {
    PageResponseDTO<ProductListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);
}
