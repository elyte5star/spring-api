package com.elyte.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.elyte.domain.request.CloudLogin;
import com.elyte.domain.request.LoginRequestData;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.response.JwtResponse;
import com.elyte.domain.response.TokenResponse;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.security.UserPrincipal;
import com.elyte.service.GmailTokenService;
import com.elyte.service.MsalTokenService;
import com.elyte.security.JwtTokenUtil;
import com.elyte.utils.UtilityFunctions;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.Operation;
import com.elyte.utils.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import org.eclipse.jetty.io.QuietException.Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class JwtLoginController extends UtilityFunctions {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtTokenUtil jwtTokenUtil;

       

        @Autowired
        private GmailTokenService gmailTokenService;

        @Autowired
        private MsalTokenService msalValidation;

        private static final Logger log = LoggerFactory.getLogger(JwtLoginController.class);

        @PostMapping("/token")
        public ResponseEntity<CustomResponseStatus> createToken(HttpServletRequest request,
                        @RequestBody @Valid LoginRequestData loginRequestData, final Locale locale)
                        throws Exception {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequestData.getUsername(),
                                                loginRequestData.getPassword()));
                final UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
                return createTokenFromUserService(request, userDetails);

        }

        @PostMapping(path = "/form-login", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
        @Operation(summary = "Form login")
        public ResponseEntity<CustomResponseStatus> handleNonBrowserSubmissions(HttpServletRequest request,
                        @Valid LoginRequestData loginRequestData, final Locale locale) throws Exception {
                log.debug("credentials Submitted thru a form");
                return createToken(request, loginRequestData, locale);
        }

        @PostMapping(path = "/get-token")
        @Operation(summary = "External login")
        public Object cloudLogin(HttpServletRequest request,
                        @RequestBody @Valid CloudLogin cloudLogin, final Locale locale)
                        throws Exception, GeneralSecurityException, IOException {
                log.debug(" Multifactor  Authentication invoked! ");
                if (cloudLogin.getAuthType().equals("MSOFT")) {
                        final UserPrincipal userDetails = msalValidation.authenticateUser(cloudLogin.getToken());
                        return createTokenFromUserService(request, userDetails);

                } else if (cloudLogin.getAuthType().equals("GMAIL")) {
                        final UserPrincipal userDetails = gmailTokenService.authenticateUser(cloudLogin.getToken());
                        return createTokenFromUserService(request, userDetails);
                }

                throw new ResourceNotFoundException("Unknown Authentication type: " + cloudLogin.getAuthType());
        }

        private ResponseEntity<CustomResponseStatus> createTokenFromUserService(HttpServletRequest request,
                        UserPrincipal userDetails) throws Exception {
                final JwtResponse jwtResponse = jwtTokenUtil.generateToken(userDetails);
                TokenResponse tokenResponse = new TokenResponse(EncryptionUtil.encrypt(jwtResponse.getJwtToken()),
                                "bearer",
                                userDetails.getUsername(), userDetails.getUser().getEmail(),
                                userDetails.isEnabled(), userDetails.getUser().isAdmin(),
                                userDetails.getUser().getUserid());
                CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(),
                                this.I200_MSG,
                                this.SUCCESS,
                                request.getRequestURL().toString(), this.timeNow(), tokenResponse);
                return new ResponseEntity<>(resp, HttpStatus.OK);
                // return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtResponse.getJwtCookie().toString())
                //                 .body(resp);// 

        }

        @GetMapping("/signout")
        @Operation(summary = "Signout")
        public ResponseEntity<?> logoutUser(HttpServletRequest request) {
                ResponseCookie cookie = jwtTokenUtil.getCleanJwtCookie();
                CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(),
                                this.I200_MSG,
                                this.SUCCESS,
                                request.getRequestURL().toString(), this.timeNow(), "You've been signed out!");
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resp);
        }

}
