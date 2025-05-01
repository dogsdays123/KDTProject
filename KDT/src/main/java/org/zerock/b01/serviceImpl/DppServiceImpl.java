package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.DeliveryProcurementPlanDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.service.DppService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
@Service
public class DppServiceImpl implements DppService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    UserByRepository userByRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductionPlanRepository productionPlanRepository;

    @Autowired
    DeliveryProcurementPlanRepository deliveryProcurementPlanRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Override
    public void registerDpp(DeliveryProcurementPlanDTO dppDTO) {
        DeliveryProcurementPlan dpp = modelMapper.map(dppDTO, DeliveryProcurementPlan.class);

        log.info("test$$ " + dppDTO.getMCode());

        Material m = materialRepository.findByMaterialCode(dppDTO.getMCode()).orElseThrow();
        ProductionPlan pp = productionPlanRepository.findByProductionPlanCode(dppDTO.getPpCode()).orElseThrow();

        dpp.setUserBy(userByRepository.findByUId(dppDTO.getUId()));
        dpp.setMaterial(m);
        dpp.setProductionPlan(pp);
        dpp.setDppState(CurrentStatus.ON_HOLD);

        if (dppDTO.getMCode() == null) {
            String dppCode = generateDppCode(dppDTO);
            dpp.setOneCode(dppCode);
        } else if (deliveryProcurementPlanRepository.findById(dppDTO.getDppCode()).isEmpty()) {
            log.info("NewHave " + dppDTO.getDppCode());
        } else {
            log.info("haveOld " + dppDTO.getDppCode());
        }
        ;
        log.info("materialDTO = " + dppDTO);
        deliveryProcurementPlanRepository.save(dpp);
    }

    public String generateDppCode(DeliveryProcurementPlanDTO dto) {
        List<Product> products = productRepository.findByProducts();

        // 제품명에 따른 접두어 설정
        String prefix = "";

        for (Product product : products) {
            if (productRepository.findByProductCodeObj(dto.getPpCode()).getPName().equals(product.getPName())) {
                prefix = product.getPName();
            } else {
                prefix = "DEFAULT";
            }
        }

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = productionPlanRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%03d", prefix, nextSequence);
    }
}
