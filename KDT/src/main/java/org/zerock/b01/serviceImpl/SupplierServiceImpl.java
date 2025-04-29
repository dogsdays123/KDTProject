package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.repository.SupplierRepository;
import org.zerock.b01.service.SupplierService;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    @Override
    public List<Supplier> getSupplier(){
        return supplierRepository.findSupWithOutDisAgree();
    }
}
