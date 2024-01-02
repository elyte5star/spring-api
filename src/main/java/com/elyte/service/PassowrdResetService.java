package com.elyte.service;
import java.util.Calendar;
import com.elyte.domain.PasswordResetToken;
import com.elyte.domain.User;
import com.elyte.repository.PasswordResetTokenRepository;
import com.elyte.repository.UserRepository;
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
public class PassowrdResetService extends ApplicationConsts{

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

        return !isTokenFound(passToken) ? "NotFound": isTokenExpired(passToken) ? "expired" : null;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public String createPasswordResetTokenForUser(HttpServletRequest request, String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) return "NotFound";
        String token = RandomStringGen.randomString(16);
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(RandomStringGen.calculateExpiryDate(EXPIRATION));
        passwordTokenRepository.save(myToken);
        EmailAlert mailObject =new EmailAlert(user.getEmail(), user.getUsername(), "Reset your password");
        String contextPath = getAppUrl(request);        
        String encryptedToken =  EncryptionUtil.encrypt(token);
        String url = contextPath + "/users/reset/confirm-token?token=" + encryptedToken;
        emailAlertService.sendSimpleHtmlMail(mailObject, url, (EXPIRATION/60), request.getLocale(),
                this.RESET_USER_PASSWORD);
        return encryptedToken;

    }

    

}
