package org.zerock.b01.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.ProductionPerDay;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.ProductionPerDayDTO;
import org.zerock.b01.repository.ProductionPerDayRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.service.ProductionPerDayService;

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

        ProductionPlan plan = productionPlanRepository.findByProductionPlanCode(dto.getPpCode());

        return ProductionPerDay.builder()
                .ppdId(dto.getPpdId())
                .pDate(dto.getPpdDate())
                .ppdNum(dto.getPpdNum())
                .productionPlan(plan)
                .build();
    }
}
