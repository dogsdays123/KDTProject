package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStock extends BaseEntity {

    @Id
    private Long isId;

    private String isNum;

    private String isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssId", nullable = false)
    private SupplierStock supplierStock; // 업체 자재 재고 외래키
}
