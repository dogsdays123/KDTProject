package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.OrderBy;
import org.zerock.b01.domain.ProgressInspection;
import org.zerock.b01.domain.SupplierStock;
import org.zerock.b01.dto.ProgressInspectionDTO;
import org.zerock.b01.repository.OrderByRepository;
import org.zerock.b01.repository.ProgressInspectionRepository;
import org.zerock.b01.repository.SupplierStockRepository;
import org.zerock.b01.service.ProgressInspectionService;

@Log4j2
@Service
public class ProgressInspectionServiceImpl implements ProgressInspectionService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    OrderByRepository orderByRepository;
    @Autowired
    SupplierStockRepository ssRepository;
    @Autowired
    ProgressInspectionRepository piRepository;

    @Override
    public Boolean register(ProgressInspectionDTO psDTO){
        OrderBy orderBy = orderByRepository.findById(psDTO.getOCode()).orElseThrow();
        SupplierStock supplierStock = (ssRepository.findBySupplierId
                (orderBy.getDeliveryProcurementPlan().getSupplier().getSId(), orderBy.getDeliveryProcurementPlan().getMaterial().getMCode()));

        ProgressInspection progressInspection = modelMapper.map(psDTO, ProgressInspection.class);
        progressInspection.setOrderBy(orderBy);
        progressInspection.setSupplierStock(supplierStock);

        if(piRepository.findByOCode(psDTO.getOCode()) == null){
            piRepository.save(progressInspection);

            if(orderBy.getOState() == CurrentStatus.ON_HOLD){
                orderBy.setOState(CurrentStatus.UNDER_INSPECTION);
                orderByRepository.save(orderBy);
            }
            return true;
        } else {
            return false;
        }

    }
}
