package org.zerock.b01.repository.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.test.ProductionPerDay;

import java.util.List;

public interface ProductionPerDayRepository extends JpaRepository<ProductionPerDay, Long> {

    @Query("select p from ProductionPerDay p where p.productionPlan.planId=:id")
    List<ProductionPerDay> findByProductionId(Long id);
}
