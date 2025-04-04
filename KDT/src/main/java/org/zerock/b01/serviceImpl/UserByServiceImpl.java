package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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
        Optional<UserBy> result = userByRepository.findByEmail(uEmail);
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
