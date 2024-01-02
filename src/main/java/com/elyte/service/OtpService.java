package com.elyte.service;

import com.elyte.repository.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.elyte.domain.Otp;
import com.elyte.domain.User;
import com.elyte.domain.request.EmailAlert;
import com.elyte.utils.UtilityFunctions;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class OtpService extends UtilityFunctions{

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailAlertService emailAlertService;


    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    public static final int OTP_VALIDITY = 5; // 5 minutes

    public Otp generateOtp(Locale locale,User user) throws MessagingException {
        String randomString = this.randomString(6);
        Otp otp = new Otp();
        otp.setEmail(user.getEmail());
        otp.setOtpString(randomString);
        otp.setUser(user);
        otp.setExpiryDate(this.calculateExpiryDate(OTP_VALIDITY));
        otp = otpRepository.save(otp);
        EmailAlert mailObject = new EmailAlert(user.getEmail(), user.getUsername(), "Confirm your account");
        emailAlertService.sendSimpleHtmlMail(mailObject, otp.getOtpString(), OTP_VALIDITY, locale,
                    this.VERIFY_USER_EMAIL_TEMPLATE_NAME);
        return otp;
    }

    public void deleteOtp(Otp otp) {
        otpRepository.delete(otp);
    }

    public String verifyOtp(String email, String otp) {
        List<Otp> otps = otpRepository.findByEmail(email);
        for (Otp otpInDb : otps) {
            if (otp.equalsIgnoreCase(otpInDb.getOtpString())) {
                if (otpInDb.getUser().isEnabled()) {
                    return "enabled";
                } else if (!isOtpExpired(otpInDb)) {
                    otpInDb.getUser().setEnabled(true);
                    deleteOtp(otpInDb);
                    return "valid";
                } else {
                    return "expired";
                }
            }
        }
        log.warn("[+] INVALID OTP ");
        return "invalid";
    }

    private boolean isOtpExpired(Otp otp) {
        final Calendar cal = Calendar.getInstance();
        return otp.getExpiryDate().before(cal.getTime());

    }

}
