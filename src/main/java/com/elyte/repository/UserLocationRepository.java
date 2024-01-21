package com.elyte.repository;
import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.UserLocation;
import com.elyte.domain.User;
public interface UserLocationRepository extends CrudRepository<UserLocation,String>{

    UserLocation findByCountryAndUser(String country,User user);
    
}
