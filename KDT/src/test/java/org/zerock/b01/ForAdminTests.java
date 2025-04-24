package org.zerock.b01;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.service.UserByService;

import java.util.Set;

@SpringBootTest
@Log4j2
public class ForAdminTests {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserByService userByService;

    @Test
    public void testRegister(){
        UserBy user = UserBy.builder()
                .uId("Admin")
                .uPassword(passwordEncoder.encode("1234"))
                .uName("관리자")
                .uEmail("Admin@admin.admin")
                .roleSet(Set.of(MemberRole.ADMIN))
                .build();

        userByService.registerAdmin(user);
    }
}
