package com.elyte.configuration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

@Configuration
public class GoogleAuthConfig {

    @Value("${security.google-props.client-ids}")
    private String CLIENT_ID;

    private static final JsonFactory JSON_FACTORY = new GsonFactory();

    private static final HttpTransport TRANSPORT = new NetHttpTransport();

    @Bean
	GoogleIdTokenVerifier googleTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY).setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    
}
