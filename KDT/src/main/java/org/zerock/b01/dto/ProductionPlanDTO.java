package org.zerock.b01.dto;

import lombok.*;
import org.zerock.b01.domain.Product;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionPlanDTO {

    private Long ppId;
    private String ppCode;
    private String ppName;
    private LocalDate ppStart;
    private LocalDate ppEnd;
    private Integer ppNum;
    private String pppCode;
    private String ppState;
}
