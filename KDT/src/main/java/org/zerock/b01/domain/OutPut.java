package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutPut extends BaseEntity {

    @Id
    private String opId;

    private String opCode;

    private String opANum;

    private String opState;

    @ManyToOne(fetch = FetchType.LAZY)
    private Assy assy; // 조립 구조 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductionPlan productionPlan; // 생산 계획 외래키
}
