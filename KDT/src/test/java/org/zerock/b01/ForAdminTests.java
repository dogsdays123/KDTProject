package org.zerock.b01;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.SupplierDTO;
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

    @Autowired
    ModelMapper modelMapper;

    @Test
    public void testRegister(){
        UserBy user = UserBy.builder()
                .uId("Admin")
                .uPassword(passwordEncoder.encode("1234"))
                .uName("관리자")
                .uEmail("Admin@admin.admin")
                .roleSet(Set.of(MemberRole.ADMIN))
                .userJob("관리자")
                .status("관리자")
                .uPhone("01000000000")
                .build();

        SupplierDTO supplierDTO = SupplierDTO.builder()
                .sName("admin")
                .sRegNum("12345678")
                .sManager("admin")
                .sStatus("관리자")
                .build();

        userByService.registerAdmin(user, supplierDTO);
    }

    @Test
    public void testRegisterUnit() throws UserByService.MidExistException {
        String tester[] = {"", "", ""};
        int regNum = 10;
        for(int i=0; i < regNum; i++) {
            if(i < regNum/2){
                tester[0] = "생산부서";
                tester[1] = "our";
                UserBy user = UserBy.builder()
                        .uId("S"+i)
                        .uPassword(passwordEncoder.encode("1234"))
                        .uName("김생산"+i)
                        .userType(tester[1])
                        .userJob(tester[0])
                        .uEmail("Admin"+i+"@admin.admin")
                        .roleSet(Set.of(MemberRole.USER))
                        .uPhone("01000000000")
                        .build();

                UserByDTO userDTO = modelMapper.map(user, UserByDTO.class);
                userByService.join(userDTO, null);
            } else {
                tester[0] = "협력회사";
                tester[1] = "other";
                tester[2] = "오리배";
                UserBy user = UserBy.builder()
                        .uId("testUnit"+i)
                        .uPassword(passwordEncoder.encode("1234"))
                        .uName("테스터"+i)
                        .userType(tester[1])
                        .userJob(tester[0])
                        .uEmail("Admin"+i+"@admin.admin")
                        .roleSet(Set.of(MemberRole.USER))
                        .uPhone("01000000000")
                        .build();

                UserByDTO userDTO = modelMapper.map(user, UserByDTO.class);

                SupplierDTO supplierDTO = SupplierDTO.builder()
                        .sName(tester[2]+i)
                        .sRegNum("12345678" + i)
                        .sManager(tester[2]+i)
                        .sStatus("대기중")
                        .build();

                userByService.join(userDTO, supplierDTO);
            }
        }
    }
}
