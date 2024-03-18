package com.elyte.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


@Service
public class GmailValidation {

    @Autowired
    private GoogleIdTokenVerifier verifier;

    public GoogleIdToken verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdToken gIdToken = verifier.verify(idTokenString.substring(7));
        if (!verifier.verify(gIdToken)) {
            throw new BadCredentialsException("Invalid Google Token");
        }
        return gIdToken;
    }

}
