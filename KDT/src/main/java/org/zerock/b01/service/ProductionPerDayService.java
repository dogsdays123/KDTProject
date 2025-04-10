package org.zerock.b01.service;

import org.zerock.b01.dto.ProductionPerDayDTO;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;

public interface ProductionPerDayService {
    void register(ProductionPerDayDTO dto);
    void registers(List<ProductionPerDayDTO> productionPerDayDTOs);
}
