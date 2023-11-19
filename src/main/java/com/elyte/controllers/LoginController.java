package com.elyte.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.elyte.domain.request.LoginRequestData;
import com.elyte.domain.response.LoginResponseData;
import com.elyte.domain.response.Status;
import com.elyte.domain.response.TokenResponse;
import com.elyte.security.CustomUserDetail;
import com.elyte.security.JwtTokenUtil;
import com.elyte.utils.ApplicationConsts;
import com.elyte.utils.EncryptionUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
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
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
public class LoginController {

        private static final Logger log = LoggerFactory.getLogger(LoginController.class);

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtTokenUtil jwtTokenUtil;

        @PostMapping("/token")
        public ResponseEntity<LoginResponseData> createToken(HttpServletRequest request,
                        @RequestBody @Valid LoginRequestData loginRequestData)
                        throws Exception {
                LocalDateTime current = LocalDateTime.now();
                try {

                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequestData.getUsername(),
                                                        loginRequestData.getPassword()));
                        final CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();

                        final String token = jwtTokenUtil.generateToken(userDetails);

                        Status status = Status.build(HttpStatus.OK.value(), ApplicationConsts.SRC,
                                        ApplicationConsts.SUCCESS,
                                        ApplicationConsts.SEC, current.format(ApplicationConsts.dtf));
                        TokenResponse tokenResponse = TokenResponse.build(EncryptionUtil.encrypt(token), "bearer",
                                        userDetails.getUsername(), userDetails.getUser().getEmail(),
                                        userDetails.isEnabled(), userDetails.getUser().isAdmin(),
                                        userDetails.getUser().getUserid());
                        LoginResponseData responseData = LoginResponseData.build(status, tokenResponse);
                        log.info("Authentication successful!");
                        return new ResponseEntity<>(responseData, HttpStatus.OK);

                } catch (DisabledException e) {

                        throw new DisabledException("USER_DISABLED", e);

                } catch (BadCredentialsException e) {

                        throw new BadCredentialsException("INVALID_CREDENTIALS", e);

                }

        }

}
