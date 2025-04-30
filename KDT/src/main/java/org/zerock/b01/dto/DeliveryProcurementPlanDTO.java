package org.zerock.b01.dto;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.ProductionPlan;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryProcurementPlanDTO {
    private String dppCode;

    private String dppRequireNum;

    private String dppNum;

    private String dppDate;

    private String dppState;

    private String ppCode; // 생산계획코드 외래키

    private String mCode; // 자재 외래키
}
