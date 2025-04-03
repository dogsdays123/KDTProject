package org.zerock.b01.serviceImpl.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.ProductionPerDay;
import org.zerock.b01.dto.test.ProductionPerDayDTO;
import org.zerock.b01.repository.test.ProductionPerDayRepository;
import org.zerock.b01.repository.test.ProductionPlanRepository;
import org.zerock.b01.service.test.ProductionPerDayService;

@Service
public class ProductionPerDayServiceImpl implements ProductionPerDayService {

    @Autowired
    private ProductionPerDayRepository productionPerDayRepository;

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public void register(ProductionPerDayDTO dto) {
        ProductionPerDay entity = dtoToEntity(dto);
        productionPerDayRepository.save(entity);
    }

    private ProductionPerDay dtoToEntity(ProductionPerDayDTO dto) {

        ProductionPlan plan = productionPlanRepository.findByProductionPlanCode(dto.getProductionPlanCode());

        return ProductionPerDay.builder()
                .id(dto.getId())
                .productionDate(dto.getProductionDate())
                .productionQuantity(dto.getProductionQuantity())
                .productionPlan(plan)
                .build();
    }
}
