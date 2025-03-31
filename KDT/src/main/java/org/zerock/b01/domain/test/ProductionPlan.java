package org.zerock.b01.domain.test;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.zerock.b01.domain.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "production_plan")
@Getter
@Setter
public class ProductionPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20, nullable = false)
    private Long planId;

    @Column(length = 20)
    private String productCode;

    @Column(length = 100)
    private String productName;

    private LocalDate productionStartDate;

    private LocalDate productionEndDate;

    private Integer productionQuantity;

    @Column(length = 50, unique = true)
    private String productionPlanCode; // 생산 계획 코드

    @Enumerated(EnumType.STRING)
    private CurrentStatus status;
}
