package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.ProductionPerDay;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.ProductionPerDayDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.ProductionPerDayRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.service.ProductionPerDayService;

import java.util.List;

@Log4j2
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
    public void registers(List<ProductionPerDayDTO> productionPerDayDTOs) {

        log.info("count!!" + productionPerDayDTOs.size());

        for (ProductionPerDayDTO productionPerDayDTO : productionPerDayDTOs) {
            log.info("date&&" + productionPerDayDTO.getPpdDate());

            ProductionPerDay productionPerDay = modelMapper.map(productionPerDayDTO, ProductionPerDay.class);
            ProductionPlan plan = productionPlanRepository.findByProductionPerDay(productionPerDayDTO.getPpCode());

            productionPerDay.setProductionPlan(plan);
            productionPerDayRepository.save(productionPerDay);
        }
    }

    private ProductionPerDay dtoToEntity(ProductionPerDayDTO dto) {

        ProductionPlan plan = productionPlanRepository.findByProductionPlanCode(dto.getPpCode());

        return ProductionPerDay.builder()
                .ppdId(dto.getPpdId())
                .ppdDate(dto.getPpdDate())
                .ppdNum(dto.getPpdNum())
                .productionPlan(plan)
                .build();
    }
}
