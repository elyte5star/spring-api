package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.elyte.domain.SecProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MsalValidation {

    @Autowired
    private SecProperties secProperties;

    @Autowired
    private Environment env;

    private static final Logger log = LoggerFactory.getLogger(MsalValidation.class);

    public String getPublicKey(String token) {
        try {
            // Decode the JWT token and extract the "kid" claim
            String kid = getTokenKeyId(token);

            // Get the public key from Azure AD OpenID configuration endpoint
            URL endpointUrl = new URL(secProperties.getMsalProps().getLoginAuthority() + "/discovery/keys?appid=" + secProperties.getMsalProps().getClientId());
            String publicKeyResponse = getAzureADPublicKey(endpointUrl);

            // Extract the value of x5c for the matching key ID
            return extractX5CValue(publicKeyResponse, kid);
        } catch (Exception e) {
            // Handle any exceptions here
            log.error("Error Verifying MSAL :" + e.getLocalizedMessage());
            return null;
        }
    }

    private String extractX5CValue(String publicKeyResponse, String kid) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(publicKeyResponse);
        JsonNode keysNode = jsonNode.get("keys");

        if (keysNode.isArray()) {
            for (JsonNode keyNode : keysNode) {
                String keyId = keyNode.get("kid").asText();
                if (keyId.equals(kid)) {
                    JsonNode x5cNode = keyNode.get("x5c");
                    if (x5cNode.isArray() && x5cNode.size() == 1) {
                        return x5cNode.get(0).asText();
                    } else {
                        throw new RuntimeException("Invalid x5c value in the Azure AD public key response.");
                    }
                }
            }
        }

        throw new RuntimeException("Matching key ID not found in the Azure AD public key response.");
    }

    private String getAzureADPublicKey(URL endpointUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) endpointUrl.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();
            return response.toString();
        } else {
            throw new RuntimeException("Failed to fetch Azure AD public key. Response code: " + responseCode);
        }
    }

    private String getTokenKeyId(String token) throws Exception {
        // Decode the JWT token and extract the "kid" claim
        String[] parts = token.split("\\.");
        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(header);
        return jsonNode.get("kid").asText();
    }

    private PublicKey getX509PublicKey(String publicKeyString) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PemObject pemObject;
        try (PemReader pemReader = new PemReader(new StringReader(publicKeyString))) {
            pemObject = pemReader.readPemObject();
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(pemObject.getContent());
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(inputStream);
        return certificate.getPublicKey();
    }

    public Claims decodeAndVerifyToken(String token) {
        if (!isMsalEnabled())
            return null;
        String publicKeyString = "-----BEGIN PUBLIC KEY-----\n" + getPublicKey(token) + "\n-----END PUBLIC KEY-----";
        PublicKey publicKey = null;
        try {
            publicKey = getX509PublicKey(publicKeyString);
            return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            // Throw an exception for any invalid token
            throw new RuntimeException("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    private boolean isMsalEnabled() {
        return Boolean.parseBoolean(env.getProperty("security.msal-props.enabled"));
    }
}
