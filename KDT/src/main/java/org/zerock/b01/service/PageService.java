package org.zerock.b01.service;

import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.PlanListAllDTO;
import org.zerock.b01.dto.allDTO.ProductListAllDTO;
import org.zerock.b01.dto.allDTO.SupplierAllDTO;
import org.zerock.b01.dto.allDTO.UserByAllDTO;

public interface PageService {
    PageResponseDTO<PlanListAllDTO> planListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<ProductListAllDTO> productListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<MaterialDTO> materialListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<UserByAllDTO> userByWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<SupplierAllDTO> supplierWithAll(PageRequestDTO pageRequestDTO, String pageType);
    PageResponseDTO<UserByAllDTO> userByWithAllList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BomDTO> bomListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<InventoryStockDTO> inventoryStockWithAll(PageRequestDTO pageRequestDTO);
}
