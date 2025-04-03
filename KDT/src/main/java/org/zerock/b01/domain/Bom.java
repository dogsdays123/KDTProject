package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bom extends BaseEntity {

    @Id
    private String bId;

    private String bRequireNum;

    private String bComponentType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product; // 제품 외래키
}
