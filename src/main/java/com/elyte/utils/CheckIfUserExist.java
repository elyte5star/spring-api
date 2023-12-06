package com.elyte.utils;

import com.elyte.domain.User;
import com.elyte.repository.UserRepository;
import com.elyte.domain.request.CreateUserRequest;
import java.util.List;

public class CheckIfUserExist {

    public static Boolean isExisting(CreateUserRequest entity, UserRepository userRep) {
        List<User> userExistUser = userRep.findByUsernameOrEmailOrTelephone(entity.getUsername(), entity.getEmail(),
                entity.getTelephone());
        return (!userExistUser.isEmpty());
    }
}