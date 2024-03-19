package com.elyte.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class GmailValidation {

    @Autowired
    GoogleIdTokenVerifier verifier;

    private static final Logger log = LoggerFactory.getLogger(
            GmailValidation.class);

    public GoogleIdToken verifyToken(String idTokenString) {

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            // Get profile information from payload
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            if (!emailVerified) {
                System.out.println("Invalid ID token.");
               throw new BadCredentialsException("Invalid ID token ");
            }
            //String email = payload.getEmail();
            //String userId = payload.getSubject();
            // String name = (String) payload.get("name");
            // String pictureUrl = (String) payload.get("picture");
            // String locale = (String) payload.get("locale");
            // String familyName = (String) payload.get("family_name");
            // String givenName = (String) payload.get("given_name");
            return idToken;
        }
        throw new BadCredentialsException("Invalid ID token ");
    }
}
