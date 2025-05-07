package org.zerock.b01.repository;

import org.hibernate.query.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.DeliveryRequest;
import org.zerock.b01.domain.OrderBy;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface OrderByRepository extends JpaRepository<OrderBy, String>, AllSearch {

    @Query("select o from OrderBy o where o.oCode=:oCode")
    Optional<OrderBy> findByOrderByCode(@Param("oCode") String oCode);

    @Query("select s.deliveryProcurementPlan.supplier.sName from OrderBy s")
    List<String> findSupplierNames();

    @Query("select s.deliveryProcurementPlan.material.mName from OrderBy s")
    List<String> findMaterialNames();
}
