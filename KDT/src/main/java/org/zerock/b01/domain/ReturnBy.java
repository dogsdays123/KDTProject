package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBy extends BaseEntity {

    @Id
    private String rCode;

    private Long rNum;

    @Enumerated(EnumType.STRING)
    private CurrentStatus rState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ipCode", nullable = false)
    private InPut inPut; // 입고 외래키
}
