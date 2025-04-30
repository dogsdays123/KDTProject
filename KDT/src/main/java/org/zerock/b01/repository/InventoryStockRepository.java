package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.InventoryStock;
import org.zerock.b01.service.AllSearch;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long>, AllSearch {
}
