package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBy extends BaseEntity {

    @Id
    private String oCode;

    private String oNum;

    private String oDate;

    private String oTotalPrice;

    private String oExpectDate;

    @Enumerated(EnumType.STRING)
    private CurrentStatus oState;

    private String oRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier; // 공급업체 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dppCode", nullable = false)
    private DeliveryProcurementPlan deliveryProcurementPlan; // 조달 계획 외래키
}
