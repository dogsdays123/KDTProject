package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String>, AllSearch {
    @Query("select p from Product p")
    List<Product> findByProducts();

//    @Query("select p from Product p where p.pCode =:pCode")
//    Optional<Product> findByProductCode(String pCode);

    @Query("select p from Product p where p.pName=:pName")
    Optional<Product> findByProductName(@Param("pName") String pName);

    @Query("select p from Product p where p.pCode =:pCode")
    Optional<Product> findByProductId(@Param("pCode") String pCode);

}
