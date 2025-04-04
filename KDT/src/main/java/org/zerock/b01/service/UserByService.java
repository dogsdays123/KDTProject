package org.zerock.b01.service;

import org.zerock.b01.dto.UserByDTO;

public interface UserByService {
    UserByDTO readOne(String uId);
    UserByDTO readOneForEmail(String uEmail);
    String registerUser(UserByDTO userByDTO);

    static class MidExistException extends Exception {

    }
    void join(UserByDTO userByDTO) throws MidExistException;
}
