package com.elyte.service;

import com.elyte.repository.OtpRepository;
import com.elyte.security.events.RegistrationCompleteEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elyte.domain.Otp;
import com.elyte.domain.User;
import com.elyte.domain.enums.EmailType;
import com.elyte.domain.request.EmailAlert;
import com.elyte.utils.UtilityFunctions;
import org.springframework.stereotype.Service;

@Service
public class OtpService extends UtilityFunctions implements ApplicationListener<RegistrationCompleteEvent>{

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailAlertService emailAlertService;


    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    public static final int OTP_VALIDITY = 5; // 5 minutes

    public Otp generateOtp(User user,String appUrl,Locale locale){
        final String randomString = this.randomString(16);
        Map<String, Object> data = new HashMap<>();
        Otp otp = new Otp();
        otp.setEmail(user.getEmail());
        otp.setOtpString(randomString);
        otp.setUser(user);
        otp.setExpiryDate(this.calculateExpiryDate(OTP_VALIDITY));
        otp = otpRepository.save(otp);
        data.put("username",user.getUsername());
        data.put("otp", randomString);
        data.put("duration", OTP_VALIDITY);
        data.put("home", appUrl);
        data.put("confirmationUrl", appUrl + "/signup/verify-otp?token=" + randomString);
        EmailAlert emailAlert = new EmailAlert();
        emailAlert.setEmailType(EmailType.NEW_USER_OTP_VERIFICATION);
        emailAlert.setRecipientEmail(user.getEmail());
        emailAlert.setRecipientUsername(user.getUsername());
        emailAlert.setSubject("Registration Confirmation");
        emailAlert.setData(data);
        emailAlertService.sendEmailAlert(emailAlert, locale);
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

    @Override
    public void onApplicationEvent(final RegistrationCompleteEvent event) {
        this.generateOtp(event.getUser(),event.getAppUrl(), event.getLocale());
    }

}
