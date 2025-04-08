package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductionPerDay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ppdId;

    private int ppdNum; //일당 생산량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppCode", nullable = false)
    private ProductionPlan productionPlan;

    private LocalDate pDate;
}
