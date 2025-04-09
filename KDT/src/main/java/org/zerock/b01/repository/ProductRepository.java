package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p")
    List<Product> findByProducts();

    @Query("select p from Product p where p.pName=:pName")
    Optional<Product> findByProductName(@Param("pName") String pName);
}
