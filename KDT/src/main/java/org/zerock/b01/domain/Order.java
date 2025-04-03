package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    private String oId;

    private String oCode;

    private String oNum;

    private String oDate;

    private String oTotalPrice;

    private String oExpectDate;

    private String oState;

    private String oRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier; // 공급업체 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private DeliveryProcurementPlan deliveryProcurementPlan; // 조달 계획 외래키
}
