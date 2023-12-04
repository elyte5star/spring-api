package com.elyte.service;

import com.elyte.repository.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.elyte.domain.Otp;
import com.elyte.utils.RandomStringGen;
import java.time.LocalDateTime;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    public static final int OTP_VALIDITY = 5; // 3 minutes

    public Otp generateOtp(String email) {
        String randomString = RandomStringGen.randomString(6);
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpString(randomString);
        otp.setDuration(OTP_VALIDITY);
        otp = otpRepository.save(otp);
        return otp;
    }

    public void deleteOtp(Otp otp) {
        otpRepository.delete(otp);
    }

    public String verifyOtp(String email, String otp) {
        List<Otp> otps = otpRepository.findByEmail(email);
        for (Otp otpInDb : otps) {
            if (otp.equalsIgnoreCase(otpInDb.getOtpString())) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime createdAt = otpInDb.getCreated_at();
                long timeDifference = Duration.between(createdAt, now).toMinutes();
                if (timeDifference > OTP_VALIDITY) return "expired";
                deleteOtp(otpInDb);
                return "valid";
            }
        }
        log.warn("[+] INVALID OTP ");
        return "invalid";
    }

}
