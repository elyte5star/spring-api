package com.elyte.repository;
import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.User;



public interface UserRepository extends CrudRepository<User, String> {
    
    User findByUsername(String username);

    User findByUsernameOrEmailOrTelephone(String username,String email,String telephone);

    User findByUserid(String userid);

}
