package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressInspection extends BaseEntity {

    @Id
    private Long psId;

    private String psNum;

    private String psDegree;

    private LocalDate psDate;

    @Enumerated(EnumType.STRING)
    private CurrentStatus psState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oCode", nullable = false)
    private OrderBy orderBy; // 발주서 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier; // 협력업체 외래키
}
