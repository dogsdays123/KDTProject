package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.repository.BomRepository;
import org.zerock.b01.service.BomService;

import java.util.List;

@Log4j2
@Service
public class BomServiceImpl implements BomService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    BomRepository bomRepository;

    public List<Bom> getBoms() {
        return bomRepository.findAll();
    }
}
