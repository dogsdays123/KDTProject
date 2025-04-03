package org.zerock.b01.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity {

    @Id
    private String cId;

    private String cCode;

    private String cMName;

    private String cSize;

    private String cUintPrice;

    private String cTax;

    private String cSignA;

    private String cSignB;

    private String cDate;
}
