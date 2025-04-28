package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.service.AllSearch;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, String>, AllSearch {
    @Query("select p from Product p where p.pName=:pName")
    Product findByProduct(String pName);

    @Query("select m from Material m where m.mCode=:mCode")
    Optional<Material> findByMaterialCode(@Param("mCode") String mCode);
//    Material findByMaterialCode(String mCode);

    @Query("SELECT COUNT(m) FROM Material m WHERE m.mCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

    boolean existsByProduct_pCode(String pCode);
}
