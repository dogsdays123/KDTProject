package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryProcurementPlan extends BaseEntity {

    @Id
    private String dppId;

    private String dppCode;

    private String dppRequireNum;

    private String dppNum;

    private String dppDate;

    private String dppState;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductionPlan productionPlan; // 생산계획코드 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material; // 자재 외래키
}
