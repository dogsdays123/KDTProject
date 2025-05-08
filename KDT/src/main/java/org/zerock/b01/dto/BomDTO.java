package org.zerock.b01.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BomDTO {

    private Long bId;

    private String bRequireNum;

    private String mComponentType;

    private String mCode;

    private String pCode;

    private String pName;

    private String mName;

    private LocalDate regDate;

    private String uId;

}
