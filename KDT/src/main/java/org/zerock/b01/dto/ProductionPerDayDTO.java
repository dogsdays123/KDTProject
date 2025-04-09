package org.zerock.b01.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPerDayDTO {
    private Long ppdId;
    private int ppdNum; //일당 생산량

    //productionPlan
    private String ppCode; //생산계획 코드
    private LocalDate ppdDate;
}