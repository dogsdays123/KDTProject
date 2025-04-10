package org.zerock.b01.service;

import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.ProductionPlanDTO;

public interface ProductionPlanService {
    String registerProductionPlan(ProductionPlanDTO productionPlanDTO);
    ProductionPlan findProductionPlan(ProductionPlanDTO productionPlanDTO);
}
