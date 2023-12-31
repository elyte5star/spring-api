package com.elyte.controllers;
import com.elyte.domain.request.CreateUserRequest;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.request.ModifyEntityRequest;
import com.elyte.domain.request.PasswordUpdate;
import com.elyte.domain.request.ValidateOtpRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userid}")
    @Operation(summary = "Get a user by userid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findUserById(@PathVariable @Valid String userid)
            throws ResourceNotFoundException {
        return userService.userById(userid);
    }

    @PutMapping("/{userid}")
    @Operation(summary = "Update a user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> updateUser(@RequestBody ModifyEntityRequest user,
            @PathVariable String userid) throws ResourceNotFoundException {
        return userService.updateUserInfo(user, userid);
    }

    @PostMapping("/signup")
    @Operation(summary = "Create a user")
    public ResponseEntity<CustomResponseStatus> createUser(@RequestBody @Valid CreateUserRequest createUserRequest,
            final Locale locale) throws DataIntegrityViolationException, MessagingException {
        return userService.createUser(createUserRequest, locale);
    }

    @DeleteMapping("/{userid}")
    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> deleteUser(@PathVariable @Valid String userid)
            throws ResourceNotFoundException {
        return userService.deleteUser(userid);

    }

    @GetMapping("/signup/send-otp")
    @Operation(summary = "Send registration confirmation OTP")
    public ResponseEntity<CustomResponseStatus> sendOtp(@RequestParam("username") @RequestBody @Valid String username,
            final Locale locale)
            throws ResourceNotFoundException {
        return userService.sendOtp(username, locale);

    }

    @PostMapping(value = "/signup/verify-otp")
    @Operation(summary = "Verify OTP")
    public ResponseEntity<CustomResponseStatus> otpValidator(@RequestBody @Valid ValidateOtpRequest otp)
            throws Exception {
        return userService.validateOtp(otp);

    }

    @PostMapping(value = "/updatePassword")
    @Operation(summary = "Change user password", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> changeUserpassword(@RequestBody @Valid PasswordUpdate passwordUpdate) {
        return userService.handlePassWordChange(passwordUpdate);

    }

    @GetMapping("/reset/password")
    @Operation(summary = "Reset password request")
    public ResponseEntity<CustomResponseStatus> resetPassword(HttpServletRequest request,
            @RequestParam("email") @Valid String userEmail) throws ResourceNotFoundException, MessagingException {
        return userService.createPasswordResetTokenForUser(request, userEmail);

    }

    @GetMapping("/reset/confirm-token")
    @Operation(summary = "Confirm password request token")
    public ResponseEntity<CustomResponseStatus> validatePasswordResetToken(
            @RequestParam("token") @Valid final String token) throws ResourceNotFoundException {
        return userService.validatePasswordResetToken(token);

    }

    @GetMapping("/enableNewLocation")
    @Operation(summary = "Confirm password request token")
    public ResponseEntity<CustomResponseStatus> enableNewLocation(Locale locale,
            @RequestParam("token") @Valid final String token) throws ResourceNotFoundException {
        return userService.enableNewLocation(locale, token);

    }

}
