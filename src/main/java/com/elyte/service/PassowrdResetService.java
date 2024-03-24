package com.elyte.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.elyte.domain.PasswordResetToken;
import com.elyte.domain.User;
import com.elyte.domain.enums.EmailType;
import com.elyte.repository.PasswordResetTokenRepository;
import com.elyte.repository.UserRepository;
import com.elyte.security.events.GeneralUserEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
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
    private Environment env;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
        if (env.getProperty("client.url") != null) {
            return env.getProperty("client.url");
        }
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public String createPasswordResetTokenForUser(HttpServletRequest request, String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null)
            return "NotFound";
        final String token = this.randomString(24);
        Date expiry = this.calculateExpiryDate(EXPIRATION);
        saveIssuedToken(user,token,expiry);
        String contextPath = getAppUrl(request);
        String encryptedToken = EncryptionUtil.encrypt(token);
        String url = contextPath + "/users/reset/confirm-token?token=" + encryptedToken;
        int expiryTime = (EXPIRATION / 60);
        EmailAlert emailAlert = new EmailAlert();
        emailAlert.setEmailType(EmailType.RESET_USER_PASSWORD);
        emailAlert.setRecipientEmail(user.getEmail());
        emailAlert.setRecipientUsername(user.getUsername());
        emailAlert.setSubject("Reset your password");
        emailAlert.setData(Map.of("username", user.getUsername(), "code", encryptedToken,"url",url, "duration", expiryTime));
        eventPublisher.publishEvent(new GeneralUserEvent(emailAlert, user, request.getLocale()));
        return encryptedToken;
    }

    public void saveIssuedToken(User user, String token, Date expiry) {
        PasswordResetToken myToken = passwordTokenRepository.findByUser(user);
        if (myToken == null) {
            PasswordResetToken myNewToken = new PasswordResetToken();
            myNewToken.setToken(token);
            myNewToken.setUser(user);
            myNewToken.setExpiryDate(expiry);
            passwordTokenRepository.save(myNewToken);
        } else {
            myToken.setToken(token);
            myToken.setExpiryDate(expiry);
            passwordTokenRepository.save(myToken);
        }
    }

}
