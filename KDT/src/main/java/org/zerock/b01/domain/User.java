package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    private String uId;

    private String uName;

    private String uAddress;

    private String userType;

    private String uEmail;

    private String uPhone;

    private String uBirthDay;
}