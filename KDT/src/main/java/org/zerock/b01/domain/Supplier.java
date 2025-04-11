package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sId;

    private String sName;

    private String sRegNum;

    private String sAddress;

    private String sAddressExtra;

    private String sManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cId")
    private Contract sContract;

    private String sBusinessType;

    private String sBusinessArray;

    private String sPhone;

    private String sFax;

    private String sPhoneDirect;

    private String sExponent;

    private String sAgree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;
}
