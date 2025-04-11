package org.zerock.b01.service;

import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;

public interface ProductionPlanService {
    String registerProductionPlan(ProductionPlanDTO productionPlanDTO, String uName);
    ProductionPlan findProductionPlan(ProductionPlanDTO productionPlanDTO);
    List<ProductionPlan> getPlans();
}
