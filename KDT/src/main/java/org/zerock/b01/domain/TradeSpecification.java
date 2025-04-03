package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSpecification extends BaseEntity {

    @Id
    private String tsId;

    private String tsNum;

    private String tsTotalPrice;

    private String tsUnitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier; // 공급업체 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material; // 자재 외래키
}
