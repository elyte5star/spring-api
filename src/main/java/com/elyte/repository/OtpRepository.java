package com.elyte.repository;
import com.elyte.domain.Otp;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface OtpRepository extends CrudRepository<Otp,String> {

    List<Otp> findByEmail(String email);
    
}
