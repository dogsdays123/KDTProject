package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SupplierAllDTO {
    private String sName;
    private String sRegNum;
    private String sBusinessType;
    private String sManager;
    private String sPhone;
    private LocalDateTime regDate;
    private String sStatus;
}
