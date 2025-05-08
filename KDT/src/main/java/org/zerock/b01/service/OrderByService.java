package org.zerock.b01.service;

import org.zerock.b01.dto.OrderByDTO;

public interface OrderByService {
    void orderByRegister(OrderByDTO orderByDTO, String uId);
}
