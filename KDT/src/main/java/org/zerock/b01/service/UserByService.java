package org.zerock.b01.service;

import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.SupplierDTO;
import org.zerock.b01.dto.UserByDTO;

import java.util.List;

public interface UserByService {
    UserByDTO readOne(String uId);
    void agreeEmployee(String uId, String userRank);
    void disAgreeEmployee(String uId, String userRank);
    UserByDTO readOneForEmail(String uEmail);
    void registerAdmin(UserBy user);
    String registerUser(UserByDTO userByDTO);
    String changeUserProfile(String email);
    void changeUser(UserByDTO userByDTO);
    void agreeSupplier(String uId);
    void disAgreeSupplier(String uId);

    List<UserBy> readAllUser();

    static class MidExistException extends Exception {
    }

    void join(UserByDTO userByDTO, SupplierDTO supplierDTO) throws MidExistException;
}
