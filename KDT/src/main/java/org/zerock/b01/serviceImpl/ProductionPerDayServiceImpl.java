package org.zerock.b01.serviceImpl;

import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void register(ProductionPerDayDTO dto) {
        ProductionPerDay entity = dtoToEntity(dto);
        productionPerDayRepository.save(entity);
    }

    @Override
    public void registers(ProductionPerDayDTO[] productionPerDayDTOs) {

        for (ProductionPerDayDTO productionPerDayDTO : productionPerDayDTOs) {
            ProductionPerDay productionPerDay = modelMapper.map(productionPerDayDTO, ProductionPerDay.class);
            if(productionPerDayRepository.findByProductionId(productionPerDayDTO.getPpCode()) == null){
                productionPerDayRepository.save(productionPerDay);
            } else {

            }
        }

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
