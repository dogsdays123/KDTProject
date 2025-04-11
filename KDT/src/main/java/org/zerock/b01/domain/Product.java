package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    private String pCode;

    private String pName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;
}