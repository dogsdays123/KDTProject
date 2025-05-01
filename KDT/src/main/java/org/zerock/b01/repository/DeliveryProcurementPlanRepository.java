package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.DeliveryProcurementPlan;
import org.zerock.b01.domain.Material;
import org.zerock.b01.service.AllSearch;

public interface DeliveryProcurementPlanRepository extends JpaRepository<DeliveryProcurementPlan, String>, AllSearch {


}
