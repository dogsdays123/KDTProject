package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.InventoryStock;
import org.zerock.b01.domain.SupplierStock;
import org.zerock.b01.service.AllSearch;

import java.util.List;

public interface SupplierStockRepository extends JpaRepository<SupplierStock, Long>, AllSearch {

    @Query("SELECT ss FROM SupplierStock ss WHERE ss.supplier.sId = :sId")
    List<SupplierStock> findAllBySupplierId(@Param("sId") Long sId);
}
