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


    boolean existsBySupplier_sIdAndMaterial_mCode(Long sId, String mCode);

    @Query("SELECT ss.leadTime FROM SupplierStock ss WHERE ss.material.mCode = :mCode")
    String findLeadTimeByMCode(@Param("mCode") String mCode);

    @Query("SELECT ss.supplier.sName FROM SupplierStock ss WHERE ss.material.mCode = :mCode")
    List<String> findSNameByMCode(@Param("mCode") String mCode);

    @Query("SELECT ss.leadTime FROM SupplierStock ss WHERE ss.supplier.sName=:sName and ss.material.mCode = :mCode")
    String findLeadTimeByETC(@Param("sName") String sName, @Param("mCode") String mCode);
}
