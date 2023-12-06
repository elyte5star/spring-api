package com.elyte.repository;
import com.elyte.domain.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

public interface PasswordTokenRepository extends CrudRepository <PasswordResetToken,String>{

    PasswordResetToken findByToken(String token);
    
}
