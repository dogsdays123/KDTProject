package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.SupplierDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.repository.SupplierRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.UserByService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class UserByServiceImpl implements UserByService {

    private final ModelMapper modelMapper;
    private final UserByRepository userByRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupplierRepository supplierRepository;
    private final JavaMailSender mailSender;

    @Override
    public String registerUser(UserByDTO userByDTO){
        UserBy userBy = modelMapper.map(userByDTO, UserBy.class);
        String uId = userByRepository.save(userBy).getUId();
        return uId;
    }

    @Override
    public void join(UserByDTO userByDTO, SupplierDTO supplierDTO) throws MidExistException{
        String uId = userByDTO.getUId();
        log.info("look at me @@@@@@@@@@   " + uId);
        boolean exist = userByRepository.existsById(uId);

        if(exist){
            throw new MidExistException();
        }

        UserBy userBy = modelMapper.map(userByDTO, UserBy.class);
        userBy.changeUPassword(passwordEncoder.encode(userByDTO.getUPassword()));
        userBy.addRole(MemberRole.USER);

        log.info("=======================");
        log.info(userBy);
        log.info(userBy.getRoleSet());

        userByRepository.save(userBy);

        if(supplierDTO !=null && userByRepository.findById(userBy.getUId()).isPresent()){
            Supplier supplier = modelMapper.map(supplierDTO, Supplier.class);
            supplier.setUserBy(userBy);
            log.info("-^-^-" + supplier.getUserBy());
            supplierRepository.save(supplier);
        }
    }

    @Override
    public UserByDTO readOne(String uId){
        Optional<UserBy> result = userByRepository.findById(uId);
        log.info("ServiceAllId@@@@" + result.toString());

        if(result.isPresent()){
            UserBy userBy = result.orElseThrow();
            UserByDTO userByDTO = modelMapper.map(userBy, UserByDTO.class);
            return userByDTO;
        } else {
            log.info("널!!!!!");
            return null;
        }
    }

    @Override
    public UserByDTO readOneForEmail(String uEmail){
        Optional<UserBy> result = userByRepository.findByuEmail(uEmail);
        log.info("ServiceEmail@@@@" + result.toString());

        if(result.isPresent()){
            UserBy userBy = result.orElseThrow();
            UserByDTO userByDTO = modelMapper.map(userBy, UserByDTO.class);
            return userByDTO;
        } else {
            log.info("널!!!!!");
            return null;
        }
    }

    @Override
    public List<UserBy> readAllUser(){
        List<UserBy> userByDTOList = userByRepository.findAll();
        return userByDTOList;
    }

    @Override
    public String changeUserProfile(String email){
        // 이메일 유효성 검사
        Optional<UserBy> user = userByRepository.findByuEmail(email);
        String msg;

        if (user.isEmpty()) {
            msg = "해당 이메일로 등록된 사용자가 없습니다.";
        } else {
            // 임시 비밀번호 생성 (혹은 링크 생성)
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            // 사용자 비밀번호 업데이트 (암호화 필요!)
            UserBy realUser = user.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));
            realUser.changeUPassword(passwordEncoder.encode(tempPassword));
            userByRepository.save(realUser);

            // 이메일 발송
            sendPasswordResetEmail(realUser.getUEmail(), tempPassword);
            msg = "가입하신 이메일로 임시 비밀번호가 발송되었습니다.";
        }
        return msg;
    }

    public void sendPasswordResetEmail(String toEmail, String tempPassword){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[비밀번호 재설정] 임시 비밀번호 안내");
        message.setText("임시 비밀번호는: " + tempPassword + "\n로그인 후 반드시 변경해주세요.");

        mailSender.send(message);
    }
}
