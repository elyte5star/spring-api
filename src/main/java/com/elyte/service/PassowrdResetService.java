package com.elyte.service;
import java.util.Calendar;
import java.util.Date;

import com.elyte.domain.PasswordResetToken;
import com.elyte.domain.User;
import com.elyte.repository.PasswordTokenRepository;
import com.elyte.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import com.elyte.utils.ApplicationConsts;
import com.elyte.utils.EncryptionUtil;
import com.elyte.utils.RandomStringGen;
import com.elyte.domain.request.EmailAlert;


@Service
@Transactional
public class PassowrdResetService {

    private static final int EXPIRATION = 60 * 24;

    private static final Logger log = LoggerFactory.getLogger(PassowrdResetService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailAlertService emailAlertService;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

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

        return !isTokenFound(passToken) ? "NotFound": isTokenExpired(passToken) ? "expired" : null;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public String createPasswordResetTokenForUser(HttpServletRequest request, String email) throws MessagingException {
        
        User user = userRepository.findByEmail(email);
        if (user == null) return "NotFound";
        String token = RandomStringGen.randomString(24);
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(calculateExpiryDate());
        myToken = passwordTokenRepository.save(myToken);
        EmailAlert mailObject = EmailAlert.build(user.getEmail(), user.getUsername(), "Reset your password");

        String contextPath = getAppUrl(request);

        
        String encryptedToken =  EncryptionUtil.encrypt(token);

        String url = contextPath + "/users/changePassword?token=" + encryptedToken;

        emailAlertService.sendSimpleHtmlMail(mailObject, url, (EXPIRATION/60), request.getLocale(),
                ApplicationConsts.RESET_USER_PASSWORD);
        return encryptedToken;

    }

    private Date calculateExpiryDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, EXPIRATION);
        return new Date(cal.getTime().getTime());
    }

}
