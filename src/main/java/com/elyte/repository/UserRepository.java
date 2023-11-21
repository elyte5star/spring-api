package com.elyte.repository;

import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.User;
import java.util.UUID;


public interface UserRepository extends CrudRepository<User, UUID> {
    
    User findByUsername(String username);

    User findByUserid(UUID userid);

}
