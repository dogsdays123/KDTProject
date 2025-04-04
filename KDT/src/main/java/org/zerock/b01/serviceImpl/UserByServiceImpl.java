package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.UserByService;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class UserByServiceImpl implements UserByService {

    private final ModelMapper modelMapper;
    private final UserByRepository userByRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String registerUser(UserByDTO userByDTO){
        UserBy userBy = modelMapper.map(userByDTO, UserBy.class);
        String uId = userByRepository.save(userBy).getUId();
        return uId;
    }

    @Override
    public void join(UserByDTO userByDTO) throws MidExistException{
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
}
