package com.elyte.repository;

import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.User;
import java.util.UUID;


public interface UserRepository extends CrudRepository<User, UUID> {
    
    User findByUsername(String username);

    User findByUsernameOrEmailOrTelephone(String username,String email,String telephone);

    User findByUserid(UUID userid);

}
