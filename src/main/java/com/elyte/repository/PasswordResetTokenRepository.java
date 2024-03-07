package com.elyte.repository;
import com.elyte.domain.PasswordResetToken;
import com.elyte.domain.User;
import java.util.stream.Stream;
import java.util.Date;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;



public interface PasswordResetTokenRepository extends CrudRepository <PasswordResetToken,String>{

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

    void deleteByExpiryDateLessThan(Date now);

    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);

    
}
