package com.lara.userservice.service;

import com.lara.userservice.domain.User;
import com.lara.userservice.web.model.request.UserRequest;

public interface UserService {

    User saveUser(UserRequest user);
    Boolean verifyToken(String token);

}
