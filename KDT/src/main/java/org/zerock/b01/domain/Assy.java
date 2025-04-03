package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assy extends BaseEntity {

    @Id
    private String aId;

    private String aNum;

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product; // 제품 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductionPlan productionPlan; // 생산 계획 외래키
}
