package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InPut extends BaseEntity {

    @Id
    private String ipId;

    private String ipCode;

    private String ipNum;

    private String ipTrueNum;

    private String ipFalseNum;

    private String ipState;

    @ManyToOne(fetch = FetchType.LAZY)
    private DeliveryRequest deliveryRequest; // 납품 지시 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order; // 발주서 외래키
}
