package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Return extends BaseEntity {

    @Id
    private String rId;

    private String rCode;

    private String rNum;

    @Enumerated(EnumType.STRING)
    private CurrentStatus rState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ipId", nullable = false)
    private InPut inPut; // 입고 외래키
}
