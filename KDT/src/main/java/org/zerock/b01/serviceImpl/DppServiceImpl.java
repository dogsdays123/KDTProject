package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.DeliveryProcurementPlan;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.DeliveryProcurementPlanDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.service.DppService;

@Log4j2
@Service
public class DppServiceImpl implements DppService {

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public void registerDpp(DeliveryProcurementPlanDTO dppDTO, String uId){
        DeliveryProcurementPlan dpp = modelMapper.map(dppDTO, DeliveryProcurementPlan.class);
//        Product product = materialRepository.findByProduct(materialDTO.getPName());
//
//        material.setUserBy(userByRepository.findByUId(uId));
//        log.info("&&&& " + uId);
//
//        if(materialDTO.getMCode() == null){
//            log.info("NewNoHave " + material.getMCode());
//            String productionPlanCode = generateProductionPlanCode(materialDTO);
//            material.set(productionPlanCode);
//        }
//        else if(materialRepository.findByMaterialCode(materialDTO.getMCode()).isEmpty()){
//            log.info("NewHave " + material.getMCode());
//        } else {
//            log.info("haveOld " + material.getMCode());
//        }
//
//        material.setProduct(product);
//        log.info("materialDTO = " + materialDTO.getPName());
//        materialRepository.save(material);
    }
}
