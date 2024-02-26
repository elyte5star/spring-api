package com.elyte.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.User;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;




public interface UserRepository extends PagingAndSortingRepository<User, String>,CrudRepository<User, String> {
    
    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findByUsernameOrEmailOrTelephone(String username,String email,String telephone);

    User findByUserid(String userid);

    @Query("SELECT u FROM User u WHERE u.userid <> ?1 and ( u.email = ?2 or u.telephone=?3)")
    List<User> checkIfUserDetailsIstaken(String userid,String email,String telephone);

 
}
