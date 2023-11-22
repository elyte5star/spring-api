package com.elyte.utils;

import com.elyte.domain.User;
import com.elyte.repository.UserRepository;

public class CheckIfUserExist {

    public static Boolean isExisting(User entity, UserRepository userRep) {

        User userExistUser = userRep.findByUsernameOrEmailOrTelephone(entity.getUsername(), entity.getEmail(),
                entity.getTelephone());

        if (userExistUser != null) {

            return true;
        } else {

            userRep.save(entity);
            return false;

        }

    }

}