package org.zerock.b01.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.PlanListAllDTO;
import org.zerock.b01.dto.allDTO.ProductListAllDTO;
import org.zerock.b01.dto.allDTO.SupplierAllDTO;
import org.zerock.b01.dto.allDTO.UserByAllDTO;

import java.time.LocalDate;

public interface AllSearch {
    Page<ProductListAllDTO> productSearchWithAll(String[] types, String keyword, String pCode, String pName, Pageable pageable);
    Page<PlanListAllDTO> planSearchWithAll(String[] types, String keyword, String ppCode, String pName, String ppState, LocalDate ppStart, LocalDate ppEnd, Pageable pageable);
    Page<UserByAllDTO> userBySearchWithAll(String[] types, String keyword, String uName,
                                           String userJob, String userRank, LocalDate modDate, String status, String uId, Pageable pageable);
    Page<SupplierAllDTO> supplierSearchWithAll(String[] types, String keyword, String sName, String sRegNum, String sBusinessType, LocalDate sRegDate, String sStatus, String pageType, Pageable pageable);
    Page<UserByAllDTO> userBySearchWithAllList(String[] types, String keyword, String uName,
                                           String userJob, String userRank, LocalDate modDate, String status, String uId, Pageable pageable);

    Page<MaterialDTO> materialSearchWithAll(String[] types, String keyword, String pName, String componentType, String mName,
                                            String mCode, String mType, Pageable pageable);

    Page<BomDTO> bomSearchWithAll(String[] types, String keyword, String componentType, String mName, String pName, String uId, Pageable pageable);

    Page<InventoryStockDTO> inventoryStockSearchWithAll(String[] types, String keyword,
                                                        String pName, String componentType, String mName, String isLocation, LocalDate isRegDate, String uId, Pageable pageable);

    Page<DeliveryRequestDTO> deliveryRequestSearchWithAll(String[] types, String keyword, String mName, String sName, String drState, Pageable pageable);

    Page<InputDTO> inputSearchWithAll(String[] types, String keyword, String mName, String ipState, Pageable pageable);
}
