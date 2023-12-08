package com.elyte.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.elyte.domain.User;
import com.elyte.domain.request.LoginRequestData;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.response.TokenResponse;
import com.elyte.repository.UserRepository;
import com.elyte.security.UserPrincipal;
import com.elyte.service.OtpService;
import com.elyte.security.JwtTokenUtil;
import com.elyte.utils.ApplicationConsts;
import com.elyte.utils.EncryptionUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
public class JwtLoginController {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private OtpService otpService;

        @Autowired
        private JwtTokenUtil jwtTokenUtil;

        @PostMapping("/token")
        public ResponseEntity<CustomResponseStatus> createToken(HttpServletRequest request,
                        @RequestBody @Valid LoginRequestData loginRequestData,final Locale locale)
                        throws Exception {

                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequestData.getUsername(),
                                                        loginRequestData.getPassword()));
                        final UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

                        final String token = jwtTokenUtil.generateToken(userDetails);

                        TokenResponse tokenResponse = TokenResponse.build(EncryptionUtil.encrypt(token), "bearer",
                                        userDetails.getUsername(), userDetails.getUser().getEmail(),
                                        userDetails.isEnabled(), userDetails.getUser().isAdmin(),
                                        userDetails.getUser().getUserid());

                        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(),
                                        ApplicationConsts.I200_MSG,
                                        ApplicationConsts.SUCCESS,
                                        request.getRequestURL().toString(), ApplicationConsts.timeNow(), tokenResponse);

                        return new ResponseEntity<>(resp, HttpStatus.OK);

                } catch (DisabledException e) {
                        makeOtpRequest(loginRequestData.getUsername(), locale);
                       
                        throw new DisabledException("USER_DISABLED.OTP SENT TO EMAIL", e);

                } catch (BadCredentialsException e) {

                        throw new BadCredentialsException("INVALID_CREDENTIALS", e);

                }

        }

        public void makeOtpRequest(String username,final Locale locale) throws MessagingException{
                User user = userRepository.findByUsername(username);
                otpService.generateOtp(locale,user);  
        }

        

}
