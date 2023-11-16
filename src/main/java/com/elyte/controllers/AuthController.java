package com.elyte.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.elyte.domain.request.LoginRequestData;
import com.elyte.domain.response.ErrorResponse;
import com.elyte.domain.response.LoginResponseData;
import com.elyte.domain.response.Status;
import com.elyte.domain.response.TokenResponse;
import com.elyte.service.JwtCredentialsService;
import com.elyte.utils.ApplicationConsts;
import com.elyte.utils.EncryptionUtil;
import com.elyte.utils.JwtTokenUtil;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtCredentialsService jwtCredentialsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/token")
    public ResponseEntity<?> createToken(HttpServletRequest request, @RequestBody @Valid LoginRequestData loginRequestData)
            throws Exception {
        LocalDateTime current = LocalDateTime.now();
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestData.getUsername(),
                    loginRequestData.getPassword()));
            final UserDetails userDetails = jwtCredentialsService.loadUserByUsername(loginRequestData.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);
            LoginResponseData responseData = new LoginResponseData();
            Status status = Status.build(HttpStatus.OK.value(), ApplicationConsts.SRC, ApplicationConsts.SUCCESS,
                    ApplicationConsts.SEC, current.format(ApplicationConsts.dtf));
            TokenResponse tokenResponse = TokenResponse.build(EncryptionUtil.encrypt(token), "bearer",
                    userDetails.getUsername(), userDetails.isEnabled());
            responseData.setToken_data(tokenResponse);
            responseData.setStatus(status);

            return new ResponseEntity<>(responseData, HttpStatus.OK);

        } catch (DisabledException e) {

            log.error("---Disabled User---{}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("USER_DISABLED");
            Status status = Status.build(HttpStatus.CONFLICT.value(), e.getMessage(), false, e.getClass().getName(),
                    current.format(ApplicationConsts.dtf));
            errorResponse.setStatus(status);
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

        } catch (BadCredentialsException e) {

            ErrorResponse errorResponse = new ErrorResponse("INVALID_CREDENTIALS");
            Status status = Status.build(HttpStatus.BAD_REQUEST.value(), e.getMessage(), false, e.getClass().getName(),
                    current.format(ApplicationConsts.dtf));
            errorResponse.setStatus(status);

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        }

    }

}
