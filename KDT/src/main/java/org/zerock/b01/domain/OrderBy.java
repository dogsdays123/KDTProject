package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBy extends BaseEntity {

    @Id
    private String oCode;

    private String oNum;

    private String oTotalPrice;

    private LocalDate oExpectDate;

    @Enumerated(EnumType.STRING)
    private CurrentStatus oState;

    private String oRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dppCode", nullable = false)
    private DeliveryProcurementPlan deliveryProcurementPlan; // 조달 계획 외래키
}
