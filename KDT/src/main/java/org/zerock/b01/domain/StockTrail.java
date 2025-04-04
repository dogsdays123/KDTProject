package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTrail extends BaseEntity {

    @Id
    private Long stId;

    private String stStock;

    private String stNum;

    private String stPrice;

    private String stDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mId", nullable = false)
    private Material material; // 자재 외래키
}
