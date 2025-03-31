package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.test.ProductionPlan;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {

    @Query("SELECT COUNT(p) FROM ProductionPlan p WHERE p.productionPlanCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

    @Query("select pp from ProductionPlan pp where pp.productionPlanCode=:productionPlanCode")
    ProductionPlan findByProductionPlanCode(String productionPlanCode);
}
