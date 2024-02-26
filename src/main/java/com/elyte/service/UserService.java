package com.elyte.service;

import com.elyte.domain.NewLocationToken;
import com.elyte.domain.Otp;
import com.elyte.domain.User;
import com.elyte.domain.UserLocation;
import com.elyte.domain.request.CreateUserRequest;
import com.elyte.domain.request.ModifyEntityRequest;
import com.elyte.domain.request.PasswordUpdate;
import com.elyte.domain.request.ValidateOtpRequest;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.exception.InvalidOldPasswordException;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.NewLocationTokenRepository;
import com.elyte.repository.UserLocationRepository;
import com.elyte.repository.UserRepository;
import com.elyte.security.UserPrincipal;
import com.elyte.security.events.RegistrationCompleteEvent;
import com.elyte.utils.CheckIfUserExist;
import com.elyte.utils.CheckNullEmptyBlank;
import com.elyte.utils.UtilityFunctions;
import com.maxmind.geoip2.DatabaseReader;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends UtilityFunctions {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserLocationRepository userLocationRepository;

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private ActiveUsersService activeUsers;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  @Qualifier("GeoIPCountry")
  private DatabaseReader databaseReader;

  @Autowired
  private PassowrdResetService passowrdResetService;

  @Autowired
  private NewLocationTokenRepository newLocationTokenRepository;

  @Autowired
  private OtpService otpService;

  @Autowired
  private Environment env;

  public ResponseEntity<CustomResponseStatus> getUsers(Pageable pageable) {
    Page<User> allUsersInDb = userRepository.findAll(pageable);
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.OK.value(),
      this.I200_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      allUsersInDb
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public ResponseEntity<CustomResponseStatus> createUser(
    CreateUserRequest createUserRequest,
    Locale locale
  ) throws DataIntegrityViolationException, MessagingException {
    if (!CheckIfUserExist.isExisting(createUserRequest, userRepository)) {
      User newUser = new User();
      newUser.setUsername(createUserRequest.getUsername());
      newUser.setPassword(
        new BCryptPasswordEncoder().encode(createUserRequest.getPassword())
      );
      newUser.setTelephone(createUserRequest.getTelephone());
      newUser.setEmail(createUserRequest.getEmail());
      newUser.setCreatedBy(createUserRequest.getUsername());
      newUser = userRepository.save(newUser);
      this.addUserLocation(newUser, this.getClientIP());
      eventPublisher.publishEvent(
        new RegistrationCompleteEvent(newUser, locale, getAppUrl(request))
      );
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.CREATED.value(),
        this.I200_MSG,
        this.SUCCESS,
        this.SRC,
        this.timeNow(),
        Map.of("userid", newUser.getUserid())
      );
      return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    throw new DataIntegrityViolationException(
      "A USER WITH THE DETAILS EXIST ALREADY"
    );
  }

  public ResponseEntity<CustomResponseStatus> userById(String userid)
    throws ResourceNotFoundException {
    User user = userRepository.findByUserid(userid);
    if (user == null) {
      throw new ResourceNotFoundException(
        "User with id :" + userid + " not found!"
      );
    }
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.OK.value(),
      this.I200_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      user
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public ResponseEntity<CustomResponseStatus> updateUserInfo(
    ModifyEntityRequest user,
    String userid
  ) throws ResourceNotFoundException {
    User userInDb = userRepository.findByUserid(userid);

    if (userInDb == null) {
      throw new ResourceNotFoundException(
        "User with id :" + userid + " not found!"
      );
    }

    if (
      CheckNullEmptyBlank.check(user.getEmail()) &
      !(user.getEmail().equals(userInDb.getEmail()))
    ) {
      userInDb.setEmail(user.getEmail());
    }

    if (
      CheckNullEmptyBlank.check(user.getTelephone()) &
      !(user.getTelephone().equals(userInDb.getTelephone()))
    ) {
      userInDb.setTelephone(user.getTelephone());
    }
    List<User> usersList = userRepository.checkIfUserDetailsIstaken(
      userid,
      userInDb.getEmail(),
      userInDb.getTelephone()
    );
    if (usersList.isEmpty()) {
      userInDb = userRepository.save(userInDb);
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.NO_CONTENT.value(),
        this.I204_MSG,
        this.SUCCESS,
        this.SRC,
        this.timeNow(),
        userInDb
      );
      return new ResponseEntity<>(resp, HttpStatus.OK);
    }
    throw new DataIntegrityViolationException("A USER WITH THE DETAILS EXIST");
  }

  public ResponseEntity<CustomResponseStatus> enable2F(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new ResourceNotFoundException(
        "User with username :" + username + " not found!"
      );
    }
    if (user.isUsing2FA()) user.setUsing2FA(false); else user.setUsing2FA(true);
    user = userRepository.save(user);
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.NO_CONTENT.value(),
      this.I204_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      user
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public ResponseEntity<CustomResponseStatus> deleteUser(String userid)
    throws ResourceNotFoundException {
    Optional<User> userInDb = userRepository.findById(userid);

    if (userInDb.isPresent()) {
      try {
        userRepository.deleteById(userid);
        CustomResponseStatus status = new CustomResponseStatus(
          HttpStatus.NO_CONTENT.value(),
          this.I200_MSG,
          this.SUCCESS,
          this.SRC,
          this.timeNow(),
          null
        );
        return new ResponseEntity<>(status, HttpStatus.OK);
      } catch (Exception e) {
        CustomResponseStatus status = new CustomResponseStatus(
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          this.E500_MSG,
          this.FAILURE,
          e.getClass().getName(),
          this.timeNow(),
          null
        );
        return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    throw new ResourceNotFoundException(
      "User with id :" + userid + " not found!"
    );
  }

  public ResponseEntity<CustomResponseStatus> createPasswordResetTokenForUser(
    HttpServletRequest request,
    String email
  ) throws ResourceNotFoundException, MessagingException {
    final String result = passowrdResetService.createPasswordResetTokenForUser(
      request,
      email
    );
    if ("NotFound".equals(result)) {
      throw new ResourceNotFoundException(
        "User with email :" + email + " not found!"
      );
    }
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.OK.value(),
      this.I200_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      result
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public ResponseEntity<CustomResponseStatus> validatePasswordResetToken(
    String encryptedToken
  ) throws ResourceNotFoundException {
    final String result = passowrdResetService.validatePasswordResetToken(
      encryptedToken
    );
    if (result == null) {
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.OK.value(),
        this.I200_MSG,
        this.SUCCESS,
        this.SRC,
        this.timeNow(),
        "Reset Token Validated!"
      );
      return new ResponseEntity<>(resp, HttpStatus.OK);
    } else if ("expired".equals(result)) {
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.FORBIDDEN.value(),
        this.E403_SMTP_MSG,
        this.FAILURE,
        this.SRC,
        this.timeNow(),
        null
      );
      return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    } else {
      throw new ResourceNotFoundException(
        "Token :" + encryptedToken + " not found!"
      );
    }
  }

  private String getAppUrl(HttpServletRequest request) {
    if (env.getProperty("client.url") != null) {
      return env.getProperty("client.url");
    }
    return (
      "http://" +
      request.getServerName() +
      ":" +
      request.getServerPort() +
      request.getContextPath()
    );
  }

  private void changeUserPassword(final User user, final String password) {
    user.setPassword(new BCryptPasswordEncoder().encode(password));
    userRepository.save(user);
  }

  public ResponseEntity<CustomResponseStatus> handlePassWordChange(
    PasswordUpdate passwordUpdate
  ) {
    final User user =
      this.findByUsername(
          (
            (UserPrincipal) SecurityContextHolder
              .getContext()
              .getAuthentication()
              .getPrincipal()
          ).getUsername()
        );
    if (
      !this.checkValidOldPassword(user, passwordUpdate.getOldPassword())
    ) throw new InvalidOldPasswordException();
    this.changeUserPassword(user, passwordUpdate.getNewPassword());
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.NO_CONTENT.value(),
      this.I204_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      null
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public boolean checkValidOldPassword(
    final User user,
    final String oldPasswordString
  ) {
    return new BCryptPasswordEncoder()
      .matches(oldPasswordString, user.getPassword());
  }

  private boolean isGeoIpLibEnabled() {
    return Boolean.parseBoolean(env.getProperty("geo.ip.lib.enabled"));
  }

  public NewLocationToken isNewLocationLogin(String username, String ip) {
    String country = "LocalHost";
    if (!isGeoIpLibEnabled()) {
      return null;
    }
    try {
      if (!this.checkIfLocalHost(ip)) {
        final InetAddress ipAddress = InetAddress.getByName(ip);
        country = databaseReader.country(ipAddress).getCountry().getName();
      }
      log.warn(country + "====****");
      final User user = userRepository.findByUsername(username);
      final UserLocation userLocation = userLocationRepository.findByCountryAndUser(
        country,
        user
      );
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
    final String token = this.randomString(32);
    final NewLocationToken newLocationToken = new NewLocationToken();
    newLocationToken.setToken(token);
    newLocationToken.setUserLocation(location);
    return newLocationTokenRepository.save(newLocationToken);
  }

  public ResponseEntity<CustomResponseStatus> enableNewLocation(
    Locale locale,
    @Valid String token
  ) {
    final String result = isValidNewLocationToken(token);
    if (result == null) {
      throw new ResourceNotFoundException("Invalid Login Location!");
    }
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.OK.value(),
      this.I200_MSG_LOC,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      result
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public String isValidNewLocationToken(String token) {
    final NewLocationToken locationToken = newLocationTokenRepository.findByToken(
      token
    );
    if (locationToken != null) {
      UserLocation userLocation = locationToken.getUserLocation();
      userLocation.setEnabled(true);
      userLocation = userLocationRepository.save(userLocation);
      newLocationTokenRepository.delete(locationToken);
      return userLocation.getCountry();
    }
    return null;
  }

  public ResponseEntity<CustomResponseStatus> sendRegistrationOtp(
    String username,
    Locale locale
  ) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new ResourceNotFoundException(
        "User with username :" + username + " not found!"
      );
    }
    Otp otp = otpService.generateOtp(user, getAppUrl(request), locale);
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.CREATED.value(),
      this.I200_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      Map.of("userid", user.getUserid(), "otp", otp.getOtpString())
    );
    return new ResponseEntity<>(resp, HttpStatus.CREATED);
  }

  public ResponseEntity<CustomResponseStatus> validateOtp(
    @Valid ValidateOtpRequest otp
  ) {
    String status = otpService.verifyOtp(otp.getEmail(), otp.getOtpString());
    if ("valid".equals(status)) {
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.ACCEPTED.value(),
        this.I200_MSG,
        this.SUCCESS,
        this.SRC,
        this.timeNow(),
        "Account verified!"
      );
      return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);
    } else if ("expired".equals(status)) {
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.FORBIDDEN.value(),
        this.E403_SMTP_MSG,
        this.FAILURE,
        this.SRC,
        this.timeNow(),
        null
      );
      return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    } else if ("invalid".equals(status)) {
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.UNAUTHORIZED.value(),
        this.E401_SMTP_MSG,
        this.FAILURE,
        this.SRC,
        this.timeNow(),
        null
      );
      return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
    } else {
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.GONE.value(),
        this.E410_SMTP_MSG,
        this.FAILURE,
        this.SRC,
        this.timeNow(),
        "User already verified!"
      );
      return new ResponseEntity<>(resp, HttpStatus.GONE);
    }
  }

  public ResponseEntity<CustomResponseStatus> getLoggedUsers() {
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.OK.value(),
      this.I200_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      activeUsers.getActiveUsers()
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public ResponseEntity<CustomResponseStatus> isActiveUser(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new ResourceNotFoundException(
        "User with username : " + username + " not found!"
      );
    }
    CustomResponseStatus resp = new CustomResponseStatus(
      HttpStatus.OK.value(),
      this.I200_MSG,
      this.SUCCESS,
      this.SRC,
      this.timeNow(),
      activeUsers.isUserActive(username) ? "User online" : "User is offline"
    );
    return new ResponseEntity<>(resp, HttpStatus.OK);
  }

  public void addUserLocation(User user, String ip) {
    String country = "LocalHost";
    if (!isGeoIpLibEnabled()) return;
    try {
      if (!this.checkIfLocalHost(ip)) {
        final InetAddress ipAddress = InetAddress.getByName(ip);
        country = databaseReader.country(ipAddress).getCountry().getName();
      }
      UserLocation location = new UserLocation(country, user);
      location.setEnabled(true);
      userLocationRepository.save(location);
    } catch (final Exception e) {
      log.error("[x] An error occurred while verifying login country", e);
      throw new RuntimeException(e);
    }
  }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public ResponseEntity<CustomResponseStatus> renewOTP(
    String oldOtpString,
    HttpServletRequest request
  ) {
    final Otp otp = otpService.generateNewOtp(oldOtpString);
    if (otp != null) {
      User user = otp.getUser();
      otpService.sendNewUserVerificationEmail(
        user,
        otp.getOtpString(),
        getAppUrl(request),
        request.getLocale()
      );
      log.debug(otp.getOtpString());
      CustomResponseStatus resp = new CustomResponseStatus(
        HttpStatus.CREATED.value(),
        this.I200_MSG,
        this.SUCCESS,
        this.SRC,
        this.timeNow(),
        Map.of("userOtp", otp.getOtpString())
      );
      return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }
    throw new ResourceNotFoundException("Unknown old otp : " + oldOtpString);
  }
}
