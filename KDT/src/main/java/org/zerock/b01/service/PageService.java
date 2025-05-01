package org.zerock.b01.service;

import org.zerock.b01.dto.*;
import org.zerock.b01.dto.DppListAllDTO;
import org.zerock.b01.dto.allDTO.*;

public interface PageService {
    PageResponseDTO<PlanListAllDTO> planListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<ProductListAllDTO> productListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<MaterialDTO> materialListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<UserByAllDTO> userByWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<SupplierAllDTO> supplierWithAll(PageRequestDTO pageRequestDTO, String pageType);
    PageResponseDTO<UserByAllDTO> userByWithAllList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BomDTO> bomListWithAll(PageRequestDTO pageRequestDTO);

    PageResponseDTO<InventoryStockDTO> inventoryStockWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<DeliveryRequestDTO> deliveryRequestWithAll(PageRequestDTO pageRequestDTO);
//    PageResponseDTO<InputDTO> inputWithAll(PageRequestDTO pageRequestDTO);

    PageResponseDTO<DppListAllDTO> dppListWithAll(PageRequestDTO pageRequestDTO);

}
