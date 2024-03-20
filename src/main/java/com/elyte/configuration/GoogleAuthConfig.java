package com.elyte.configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.elyte.domain.SecProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

@Configuration
public class GoogleAuthConfig {

    @Autowired
    SecProperties secProperties;

    private static final JsonFactory JSON_FACTORY = new GsonFactory();

    private static final HttpTransport TRANSPORT = new NetHttpTransport();

    @Bean
	GoogleIdTokenVerifier googleTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY).setAudience(secProperties.getGoogleProps().getClientIds())
                .build();
    }

    
}
