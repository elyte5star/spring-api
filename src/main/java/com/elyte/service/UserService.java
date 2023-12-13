package com.elyte.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.elyte.domain.NewLocationToken;
import com.elyte.domain.Otp;
import com.elyte.domain.User;
import com.elyte.domain.UserLocation;
import com.elyte.domain.request.CreateUserRequest;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.NewLocationTokenRepository;
import com.elyte.repository.UserLocationRepository;
import com.elyte.repository.UserRepository;
import com.elyte.security.UserPrincipal;
import com.elyte.utils.ApplicationConsts;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.request.ModifyEntityRequest;
import com.elyte.domain.request.ValidateOtpRequest;
import java.util.Optional;
import com.elyte.utils.CheckNullEmptyBlank;
import com.elyte.utils.RandomStringGen;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import com.elyte.utils.CheckIfUserExist;
import com.maxmind.geoip2.DatabaseReader;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.env.Environment;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private ActiveUsersService activeUsers;

    @Autowired
    @Qualifier("GeoIPCountry")
    private DatabaseReader databaseReader;

    @Value("${attachment.invoice}")
    private String attachmentPath;

    @Autowired
    private PassowrdResetService passowrdResetService;

    @Autowired
    private NewLocationTokenRepository newLocationTokenRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private Environment env;

    public ResponseEntity<CustomResponseStatus> getUsers() {
        Iterable<User> allUsersInDb = userRepository.findAll();
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), allUsersInDb);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> addUser(CreateUserRequest createUserRequest, Locale locale)
            throws DataIntegrityViolationException, MessagingException {
        if (!CheckIfUserExist.isExisting(createUserRequest, userRepository)) {
            User newUser = new User();
            newUser.setUsername(createUserRequest.getUsername());
            newUser.setPassword(new BCryptPasswordEncoder().encode(createUserRequest.getPassword()));
            newUser.setTelephone(createUserRequest.getTelephone());
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setLastLoginDate("0");
            newUser = userRepository.save(newUser);
            return sendOtp(newUser.getUsername(), locale);
        }

        throw new DataIntegrityViolationException("A USER WITH THE DETAILS EXIST ALREADY");

    }

   
    public ResponseEntity<CustomResponseStatus> userById(String userid) throws ResourceNotFoundException {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> updateUserInfo(ModifyEntityRequest user, String userid)
            throws ResourceNotFoundException {
        User userInDb = userRepository.findByUserid(userid);

        if (userInDb == null) {

            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }

        if (!CheckNullEmptyBlank.check(user.getEmail()) & !(user.getEmail().equals(userInDb.getEmail()))) {
            userInDb.setEmail(user.getEmail());

        }
        if (!CheckNullEmptyBlank.check(user.getUsername())
                & !(user.getUsername().equals(userInDb.getUsername()))) {

            userInDb.setUsername(user.getUsername());

        }
        if (!CheckNullEmptyBlank.check(user.getTelephone())
                & !(user.getTelephone().equals(userInDb.getTelephone()))) {

            userInDb.setTelephone(user.getTelephone());

        }
        if (!CheckNullEmptyBlank.check(user.getPassword())) {

            userInDb.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        }
        List<User> usersList = userRepository.checkIfUserDetailsIstaken(userid, userInDb.getUsername(),
                userInDb.getEmail(), userInDb.getTelephone());
        if (usersList.isEmpty()) {
            userInDb = userRepository.save(userInDb);
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.NO_CONTENT.value(),
                    ApplicationConsts.I204_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, ApplicationConsts.timeNow(), userInDb);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
        throw new DataIntegrityViolationException("A USER WITH THE DETAILS EXIST");
    }

    public ResponseEntity<CustomResponseStatus> deleteUser(String userid) throws ResourceNotFoundException {
        Optional<User> userInDb = userRepository.findById(userid);

        if (userInDb.isPresent()) {
            try {
                userRepository.deleteById(userid);
                CustomResponseStatus status = new CustomResponseStatus(HttpStatus.NO_CONTENT.value(),
                        ApplicationConsts.I200_MSG,
                        ApplicationConsts.SUCCESS,
                        ApplicationConsts.SRC, ApplicationConsts.timeNow(), null);
                return new ResponseEntity<>(status, HttpStatus.OK);

            } catch (Exception e) {
                CustomResponseStatus status = new CustomResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ApplicationConsts.E500_MSG,
                        ApplicationConsts.FAILURE,
                        e.getClass().getName(), ApplicationConsts.timeNow(), null);
                return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        throw new ResourceNotFoundException("User with id :" + userid + " not found!");

    }

    public ResponseEntity<CustomResponseStatus> createPasswordResetTokenForUser(HttpServletRequest request,
            String email) throws ResourceNotFoundException, MessagingException {
        final String result = passowrdResetService.createPasswordResetTokenForUser(request, email);
        if ("NotFound".equals(result)) {
            throw new ResourceNotFoundException("User with email :" + email + " not found!");
        }
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), result);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> validatePasswordResetToken(String encryptedToken)
            throws ResourceNotFoundException {
        final String result = passowrdResetService.validatePasswordResetToken(encryptedToken);
        if (result == null) {
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, ApplicationConsts.timeNow(), "Reset Token Validated!");
            return new ResponseEntity<>(resp, HttpStatus.OK);

        } else if ("expired".equals(result)) {
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.FORBIDDEN.value(),
                    ApplicationConsts.E403_SMTP_MSG,
                    ApplicationConsts.FAILURE, ApplicationConsts.SRC, ApplicationConsts.timeNow(), null);
            return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);

        } else {
            throw new ResourceNotFoundException("Token :" + encryptedToken + " not found!");

        }

    }

    public void changeUserPassword(final User user, final String password) {
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(user);
    }

    private boolean isGeoIpLibEnabled() {
        return Boolean.parseBoolean(env.getProperty("geo.ip.lib.enabled"));
    }

    public NewLocationToken newLocationLogin(String username, String ip) {
        if (!isGeoIpLibEnabled()) {
            return null;
        }
        try {
            final InetAddress ipAddress = InetAddress.getByName(ip);
            final String country = databaseReader.country(ipAddress).getCountry().getName();
            log.warn(country + "====****");
            final User user = userRepository.findByUsername(username);
            final UserLocation userLocation = userLocationRepository.findByCountryAndUser(country, user);
            if ((userLocation == null) || !userLocation.isEnabled()) {
                return createNewLocationToken(country, user);

            }

        } catch (final Exception e) {
            return null;

        }
        return null;

    }

    private NewLocationToken createNewLocationToken(String country, User user) {
        UserLocation location = new UserLocation();
        location.setCountry(country);
        location.setUser(user);
        location = userLocationRepository.save(location);
        final String token = RandomStringGen.randomString(32);
        final NewLocationToken newLocationToken = new NewLocationToken();
        newLocationToken.setToken(token);
        newLocationToken.setUserLocation(location);
        return newLocationTokenRepository.save(newLocationToken);

    }

    public UserPrincipal getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (UserPrincipal) authentication.getPrincipal();
    }

    public ResponseEntity<CustomResponseStatus> enableNewLocation(Locale locale, @Valid String token) {
        final String result = isValidNewLocationToken(token);
        if (result == null) {
            throw new ResourceNotFoundException("Invalid Login Location!");
        }
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG_LOC,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), result);
        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

    public String isValidNewLocationToken(String token) {
        final NewLocationToken locationToken = newLocationTokenRepository.findByToken(token);
        if (locationToken != null) {

            UserLocation userLocation = locationToken.getUserLocation();
            userLocation.setEnabled(true);
            userLocation = userLocationRepository.save(userLocation);
            newLocationTokenRepository.delete(locationToken);
            return userLocation.getCountry();
        }
        return null;

    }

    public ResponseEntity<CustomResponseStatus> sendOtp(String username, Locale locale) throws MessagingException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User with username :" + username + " not found!");
        }
        Otp otp = otpService.generateOtp(locale, user);
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.CREATED.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(),
                Map.of("userid", user.getUserid(), "otp", otp.getOtpString()));
        return new ResponseEntity<>(resp, HttpStatus.CREATED);

    }

    public ResponseEntity<CustomResponseStatus> validateOtp(@Valid ValidateOtpRequest otp) {
        String status = otpService.verifyOtp(otp.getEmail(), otp.getOtpString());
        if ("valid".equals(status)) {
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.ACCEPTED.value(),
                    ApplicationConsts.I200_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, ApplicationConsts.timeNow(), "Account verified!");
            return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);
        } else if ("expired".equals(status)) {
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.FORBIDDEN.value(),
                    ApplicationConsts.E403_SMTP_MSG,
                    ApplicationConsts.FAILURE, ApplicationConsts.SRC, ApplicationConsts.timeNow(), null);
            return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
        } else if ("invalid".equals(status)) {
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.UNAUTHORIZED.value(),
                    ApplicationConsts.E401_SMTP_MSG,
                    ApplicationConsts.FAILURE, ApplicationConsts.SRC, ApplicationConsts.timeNow(), null);
            return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
        } else {
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.GONE.value(),
                    ApplicationConsts.E410_SMTP_MSG,
                    ApplicationConsts.FAILURE, ApplicationConsts.SRC, ApplicationConsts.timeNow(),
                    "User already verified!");
            return new ResponseEntity<>(resp, HttpStatus.GONE);
        }

    }

    public ResponseEntity<CustomResponseStatus> getLoggedUsers() {
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), activeUsers.getActiveUsers());
        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

    public ResponseEntity<CustomResponseStatus> isActiveUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User with username : " + username + " not found!");
        }
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), activeUsers.isUserActive(username)?"User online":"User is offline");
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
