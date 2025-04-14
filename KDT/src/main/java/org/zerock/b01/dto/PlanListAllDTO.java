package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PlanListAllDTO {
    private String ppCode;
    private String pCode;
    private String pName;
    private Integer ppNum;
    private LocalDate ppStart;
    private LocalDate ppEnd;
    private String ppState;
    private String uName;

}
