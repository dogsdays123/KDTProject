package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressInspection extends BaseEntity {

    @Id
    private String psId;

    private String psNum;

    private String psDgree;

    private String psDate;

    private String psState;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order; // 발주서 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier; // 협력업체 외래키
}
