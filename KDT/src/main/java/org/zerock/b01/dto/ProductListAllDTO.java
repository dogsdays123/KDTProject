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
public class ProductListAllDTO {
    private Long pId;
    private String pCode; //제품코드
    private String pName; //제품이름
    private LocalDate StartDate;
}
