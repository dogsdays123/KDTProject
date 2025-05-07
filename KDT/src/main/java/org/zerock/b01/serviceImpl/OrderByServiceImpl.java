package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.DeliveryProcurementPlan;
import org.zerock.b01.domain.OrderBy;
import org.zerock.b01.dto.OrderByDTO;
import org.zerock.b01.repository.DeliveryProcurementPlanRepository;
import org.zerock.b01.repository.OrderByRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.OrderByService;

@Log4j2
@Service
public class OrderByServiceImpl implements OrderByService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    DeliveryProcurementPlanRepository dppRepository;
    @Autowired
    UserByRepository userByRepository;
    @Autowired
    OrderByRepository orderByRepository;

    public void orderByRegister(OrderByDTO orderByDTO, String uId) {
        DeliveryProcurementPlan dpp = dppRepository.findById(orderByDTO.getDppCode()).orElseThrow();
        OrderBy orderBy = modelMapper.map(orderByDTO, OrderBy.class);
        orderBy.setUserBy(userByRepository.findById(uId).orElseThrow());
        orderBy.setDeliveryProcurementPlan(dpp);
        orderBy.setOState(CurrentStatus.ON_HOLD);

        orderByRepository.save(orderBy);
    }
}
