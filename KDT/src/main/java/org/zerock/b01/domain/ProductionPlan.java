package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlan extends BaseEntity {

    @Id
    private String ppId;

    private String ppCode;

    private String ppNum;

    private String ppState;

    private String ppStart;

    private String ppEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product; // 제품 외래키
}
