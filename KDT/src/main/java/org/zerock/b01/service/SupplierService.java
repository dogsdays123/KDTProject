package org.zerock.b01.service;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {

    SupplierDTO findByUserId(String uId);
    List<Supplier> getSupplier();
}
