package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.Bom;

public interface BomRepository extends JpaRepository<Bom, Long> {
    
}
