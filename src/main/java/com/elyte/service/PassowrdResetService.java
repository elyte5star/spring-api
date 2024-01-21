package com.elyte.service;

import java.util.Calendar;
import java.util.Map;

import com.elyte.domain.PasswordResetToken;
import com.elyte.domain.User;
import com.elyte.domain.enums.EmailType;
import com.elyte.repository.PasswordResetTokenRepository;
import com.elyte.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import com.elyte.utils.UtilityFunctions;
import com.elyte.utils.EncryptionUtil;
import com.elyte.domain.request.EmailAlert;

@Service
@Transactional
public class PassowrdResetService extends UtilityFunctions {

    private static final int EXPIRATION = 60 * 24;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailAlertService emailAlertService;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    public String validatePasswordResetToken(String encryptedToken) {
        String token = EncryptionUtil.decrypt(encryptedToken);
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        return !isTokenFound(passToken) ? "NotFound" : isTokenExpired(passToken) ? "expired" : null;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public String createPasswordResetTokenForUser(HttpServletRequest request, String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null)
            return "NotFound";
        String token = this.randomString(16);
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(this.calculateExpiryDate(EXPIRATION));
        passwordTokenRepository.save(myToken);
        String contextPath = getAppUrl(request);
        String encryptedToken = EncryptionUtil.encrypt(token);
        String url = contextPath + "/users/reset/confirm-token?token=" + encryptedToken;
        int expiryTime = (EXPIRATION / 60);
        EmailAlert emailAlert = new EmailAlert();
        emailAlert.setEmailType(EmailType.RESET_USER_PASSWORD);
        emailAlert.setRecipientEmail(user.getEmail());
        emailAlert.setRecipientUsername(user.getUsername());
        emailAlert.setSubject("Reset your password");
        emailAlert.setData(Map.of("username",user.getUsername(),"link",url,"duration",expiryTime));
        emailAlertService.sendEmailAlert(emailAlert,request.getLocale());
        return encryptedToken;
    }

}
