package org.zerock.b01.service;

import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.SupplierDTO;
import org.zerock.b01.dto.UserByDTO;

import java.util.List;

public interface UserByService {
    UserByDTO readOne(String uId);
    UserByDTO readOneForEmail(String uEmail);
    String registerUser(UserByDTO userByDTO);
    String changeUserProfile(String email);

    List<UserBy> readAllUser();

    static class MidExistException extends Exception {
    }

    void join(UserByDTO userByDTO, SupplierDTO supplierDTO) throws MidExistException;
}
