package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material extends BaseEntity {

    @Id
    private String mId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier;
}