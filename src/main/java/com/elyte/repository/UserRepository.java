package com.elyte.repository;

import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
