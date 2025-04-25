package org.zerock.b01.repository;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.domain.UserBy;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("select s from Supplier s")
    List<Supplier> findAllSupplier();

    @Query("select s from Supplier s where s.userBy=:userBy")
    Supplier findSupplierByUser(@Param("userBy") UserBy userBy);
}
