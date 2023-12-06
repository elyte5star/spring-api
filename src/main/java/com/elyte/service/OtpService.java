package com.elyte.service;

import com.elyte.repository.OtpRepository;
import com.elyte.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.elyte.domain.Otp;
import com.elyte.domain.User;
import com.elyte.utils.RandomStringGen;
import java.time.LocalDateTime;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    public static final int OTP_VALIDITY = 5; // 5 minutes

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
        User user = userRepository.findByEmail(email);
        if (user.isEnabled()) return "enabled";
        for (Otp otpInDb : otps) {
            if (user != null && otp.equalsIgnoreCase(otpInDb.getOtpString())) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime createdAt = otpInDb.getCreated_at();
                long timeDifference = Duration.between(createdAt, now).toMinutes();
                if (timeDifference > OTP_VALIDITY)
                    return "expired";
                user.setEnabled(true);
                deleteOtp(otpInDb);
                return "valid";
            }
        }
        log.warn("[+] INVALID OTP ");
        return "invalid";
    }

}
