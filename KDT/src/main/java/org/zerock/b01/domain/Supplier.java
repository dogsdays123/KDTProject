package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity {

    @Id
    private String sId;

    private String sName;

    private String sRegNum;

    private String sAddress;

    private String sAddressExtra;

    private String sManager;

    private String sContract;

    private String sBusinessType;

    private String sPhone;

    private String sAgree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;
}
