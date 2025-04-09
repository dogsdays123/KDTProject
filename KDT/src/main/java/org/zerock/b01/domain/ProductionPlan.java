package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20, nullable = false)
    private Long ppId;

    private String ppName;

    private String ppCode;

    private Integer ppNum;

    private LocalDate ppStart;

    private LocalDate ppEnd;

    @Enumerated(EnumType.STRING)
    private CurrentStatus ppState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pId", nullable = false)
    private Product product; // 제품 외래키

    public void set(String ppCode){
        this.ppCode = ppCode;
    }
}
