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
    private Long dppId;

    private String dppCode;

    private String dppRequireNum;

    private String dppNum;

    private String dppDate;

    @Enumerated(EnumType.STRING)
    private CurrentStatus dppState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppId", nullable = false)
    private ProductionPlan productionPlan; // 생산계획코드 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mId", nullable = false)
    private Material material; // 자재 외래키
}
