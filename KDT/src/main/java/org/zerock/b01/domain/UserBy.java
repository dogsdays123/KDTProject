package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBy extends BaseEntity {

    @Id
    private String uId;

    private String uPassword;

    private String uName;

    private String uAddress;

    private String userType;

    private String uEmail;

    private String uPhone;

    private LocalDate uBirthDay;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<MemberRole> roleSet = new HashSet<>();
}