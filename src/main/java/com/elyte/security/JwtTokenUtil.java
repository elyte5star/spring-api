package com.elyte.security;

//The JwtTokenUtil is responsible for performing JWT operations like creation and validation of the token.

// It makes use of the io.jsonwebtoken.Jwts for achieving this.

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.elyte.utils.EncryptionUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

@Component
public class JwtTokenUtil implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final long serialVersionUID = 7383112237L;

    public static final int JWT_TOKEN_VALIDITY = 1; // 60 minutes

    @Value("${api.jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // generate token for required data i.e. user details

    public String generateToken(UserPrincipal userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDetails.getUser().getEmail());
        claims.put("jti", UUID.randomUUID().toString());
        return doGenerateToken(claims, userDetails.getUsername(), userDetails.getUser().getUserid());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, String aud) {
        return Jwts.builder().claims(claims).issuer("elyte")
                .subject(subject).issuedAt(new Date(System.currentTimeMillis()))
                .audience().add(aud).and()
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 60 * 1000))
                .signWith(getSigningKey()).compact();

    }

    public Boolean validateToken(String token, UserPrincipal userDetails) {
        final String userName = getUserNameFromToken(token);
        return (!isTokenExpired(token) &&
                userName.equals(userDetails.getUsername()));
    }

    // retrieve username from jwt token
    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Object getCustomParamFromToken(String token, String param) {
        final Claims claims = getAllClaimsFromToken(token);
        log.debug("Requested param from token: {}", param);
        return claims.get(param);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        // Get the Authorization header from the request
        String authorizationHeader = request.getHeader("Authorization");

        // Check if the Authorization header is not null and starts with "Bearer "
        if ((authorizationHeader != null) && authorizationHeader.startsWith("Bearer ")) {
            // Extract the JWT token (remove "Bearer " prefix)
            String encryptedJwtToken = authorizationHeader.substring(7);

            return EncryptionUtil.decrypt(encryptedJwtToken);
        }

        // If the Authorization header is not valid, return null
        return null;
    }

}
