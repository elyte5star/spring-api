package com.elyte.repository;

import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.User;
import com.elyte.domain.UserAddress;

public interface UserAddressRepository extends CrudRepository<UserAddress,String> {
    UserAddress findByUser(User user);
}
