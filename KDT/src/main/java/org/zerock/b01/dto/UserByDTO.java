package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserByDTO {
    private String uId;
    private String uPassword;
    private String uName;
    private String uAddress;
    private String userType;
    private String userJob;
    private String uEmail;
    private String uPhone;
    private LocalDate uBirthDay;


    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
