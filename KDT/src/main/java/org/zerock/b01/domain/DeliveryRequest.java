package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest extends BaseEntity {

    @Id
    private String drId;

    private String drCode;

    private String drNum;

    private String drDate;

    @Enumerated(EnumType.STRING)
    private CurrentStatus drState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oId", nullable = false)
    private Order order; // 발주서 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier; // 공급업체 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mId", nullable = false)
    private Material material; // 자재 외래키
}
