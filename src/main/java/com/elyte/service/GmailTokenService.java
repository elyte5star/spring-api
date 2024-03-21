package com.elyte.service;

import com.elyte.domain.User;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.UserRepository;
import com.elyte.security.CredentialsService;
import com.elyte.security.UserPrincipal;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class GmailTokenService {

    @Autowired
    GoogleIdTokenVerifier googleIdTokenVerifier;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(
            GmailTokenService.class);

    public void createUser(String idTokenString) {
        Payload payload = validateToken(idTokenString);
        String email = payload.getEmail();
        String userId = payload.getSubject();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");
        String issuer = payload.getIssuer();
        log.info( email+" " +userId+ " " +name+ " " +locale +" " +familyName+" " + pictureUrl+ " " +givenName+ " " + issuer);

    }

    private Payload validateToken(String idTokenString) {
        log.debug("Token received from frontend " + idTokenString);
        GoogleIdToken decodedToken = null;
        try {
            if (idTokenString != null) {
                decodedToken = googleIdTokenVerifier.verify(idTokenString);
            }
        } catch (GeneralSecurityException | IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }

        if (decodedToken != null) {
            Payload payload = decodedToken.getPayload();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            if (!emailVerified) {
                System.out.println("Invalid ID token.");
                throw new BadCredentialsException("Account not verified ");
            }
            return payload;
        }
        throw new BadCredentialsException(" Invalid ID token ");
    }

    public UserPrincipal authenticateUser(String idTokenString) {
        Payload payload = validateToken(idTokenString);
        String email = payload.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BadCredentialsException(" Invalid credentials.");
        } else if (!user.isUsing2FA()) {
            throw new AccessDeniedException(" External login disabled.");

        }
        final UserPrincipal userDetails = (UserPrincipal) credentialsService.loadUserByUsername(user.getUsername());
        return userDetails;
    }
}
