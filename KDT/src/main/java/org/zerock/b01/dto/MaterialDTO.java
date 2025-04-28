package org.zerock.b01.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {

    private String mCode;

    private String mType;

    private String mName;

    private String mUnitPrice;

    private String mMinNum;

    private String mImageUrl;

    private Float mDepth;

    private Float mHeight;

    private Float mWidth;

    private Float mWeight;

    private String mLeadTime;

    private String mComponentType;

    private LocalDate regDate;

    private String uId;

    private String pName;

    private Long sId;
}
