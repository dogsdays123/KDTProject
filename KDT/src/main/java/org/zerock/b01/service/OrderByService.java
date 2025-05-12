package org.zerock.b01.service;

import org.zerock.b01.dto.OrderByDTO;

import java.util.Map;

public interface OrderByService {
    void orderByRegister(OrderByDTO orderByDTO, String uId);
    Map<String, Double> getMonthlyOrderSummary();
}
