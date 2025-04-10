package org.zerock.b01.dto;

import lombok.*;

import java.util.List;

// PDF 출력용 DTO -- purchaserOrder.html 의 구매 발주서 작성 기능
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequestDTO {
    private String supplierEmail; // 회사 이메일
    private String deliveryDate; // 공급 납기일
    private String unitPrice; // 단가
    private String significant; // 비고 특이사항
    private List<PurchaseItemDTO> items; // 선택된 자재 목록
}
