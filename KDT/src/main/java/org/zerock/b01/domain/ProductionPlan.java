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
    private String ppCode;

    private String pName;

    private Integer ppNum;

    private LocalDate ppStart;

    private LocalDate ppEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;

    @Enumerated(EnumType.STRING)
    private CurrentStatus ppState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pCode", nullable = false)
    private Product product; // 제품 외래키

    public void set(String ppCode){
        this.ppCode = ppCode;
    }
}
