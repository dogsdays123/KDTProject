package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

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
    private String ppId;

    private String ppName;

    private String ppCode;

    private String ppNum;

    private String ppStart;

    private String ppEnd;

    @Enumerated(EnumType.STRING)
    private CurrentStatus ppState;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "pId", nullable = false)
    private String pppCode; // 제품 외래키

    public void set(String ppCode){
        this.ppCode = ppCode;
    }
}
