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

    private String rState;

    @ManyToOne(fetch = FetchType.LAZY)
    private InPut inPut; // 입고 외래키
}
