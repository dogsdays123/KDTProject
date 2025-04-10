package org.zerock.b01.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemDTO {

    private String procureCode; // 조달 계획 코드
    private String materialCode; // 자재 코드
    private String materialName; // 자재 명
    private String quantity; // 자재수량
    private String dueDate; // 조달납기일
    private String supplier; // 공급업체명
    // 규격
    private String width;
    private String height;
    private String depth;
}
