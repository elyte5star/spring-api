package com.elyte.repository;
import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.NewLocationToken;

public interface NewLocationTokenRepository extends CrudRepository<NewLocationToken,String> {
    NewLocationToken findByToken(String token);
}
