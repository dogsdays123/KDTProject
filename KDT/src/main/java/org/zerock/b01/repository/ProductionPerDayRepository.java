package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.ProductionPerDay;

import java.util.List;

public interface ProductionPerDayRepository extends JpaRepository<ProductionPerDay, Long> {

    @Query("select p from ProductionPerDay p where p.productionPlan.ppCode=:ppCode")
    ProductionPerDay findByProductionId(String ppCode);
}
