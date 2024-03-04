package com.elyte.repository;
import com.elyte.domain.Otp;
import com.elyte.domain.User;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.Date;


public interface OtpRepository extends CrudRepository<Otp,String> {

    Otp findByEmail(String email);

    Otp findByUser(User user);

    void deleteByExpiryDateLessThan(Date now);

    Stream<Otp> findAllByExpiryDateLessThan(Date now);

    Otp findByOtpString(String otpString);

    @Modifying
    @Query("delete from Otp t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
    
}
