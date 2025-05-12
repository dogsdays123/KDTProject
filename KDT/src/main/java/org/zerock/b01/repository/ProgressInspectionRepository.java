package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.DeliveryProcurementPlan;
import org.zerock.b01.domain.ProgressInspection;
import org.zerock.b01.service.AllSearch;

public interface ProgressInspectionRepository extends JpaRepository<ProgressInspection, Long>, AllSearch {
    @Query("select ps from ProgressInspection ps where ps.orderBy.oCode =:oCode")
    ProgressInspection findByOCode(@Param("oCode") String oCode);
}
