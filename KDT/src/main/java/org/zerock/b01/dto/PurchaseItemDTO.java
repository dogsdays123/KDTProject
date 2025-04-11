package org.zerock.b01.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemDTO {
    private String materialName; // 자재명
    private String width; // 가로
    private String depth; // 깊이
    private String height; // 높이세로 규격 가로x깊이x높이
    private String quantity; // 조달수량(요구수량)
    private String dueDate; // 조달납기일

    //  자재 공급 업체의 정보
    private String supplier; // 공급업체명
}
