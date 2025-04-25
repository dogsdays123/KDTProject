package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material extends BaseEntity {

    @Id
    private String mCode; // 자재코드

    private String mType; // 자재 유형

    private String mName; // 자재명

    private String mUnitPrice; // 단위당 가격

    private String mMinNum; // 최소 공급 수량

    private String mImageUrl; // 이미지

    private Float mDepth; // 높이

    private Float mHeight; // 세로

    private Float mWidth; // 가로

    private Float mWeight; // 무게

    private String mLeadTime; // 조달시간

    private String mComponentType; //부품종류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pCode", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = true)
    private Supplier supplier;

    public void set(String mCode) {
        this.mCode = mCode;
    }
}