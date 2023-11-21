package com.elyte.security;

//The JwtTokenUtil is responsible for performing JWT operations like creation and validation of the token.

// It makes use of the io.jsonwebtoken.Jwts for achieving this.

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.UUID;

@Component
public class JwtTokenUtil implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final long serialVersionUID = 7383112237L;

    public static final int JWT_TOKEN_VALIDITY = 10 * 60; // 10 minutes

    @Value("${api.jwt.secret}")
    private String secret;

    // generate token for required data i.e. user details

    public String generateToken(JwtUserPrincipal userDetails) {

        // we can set extra info this claims hashmap and below defined
        // getCustomParamFromToken to get it by passing Map key.
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", userDetails.getUser().getUserid().toString());
        claims.put("email", userDetails.getUser().getEmail());
        claims.put("jti", UUID.randomUUID().toString());
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // while creating the token -
    // 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
    // 2. Sign the JWT using the HS512 algorithm and secret key.
    // 3. According to JWS Compact
    // Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    // compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    // public Boolean validateToken(String token, JwtUserPrincipal userDetails){
    // final String userName = getUserNameFromToken(token);
    // return (!isTokenExpired(token) &&
    // userName.equals(userDetails.getUsername()));
    // }

    public Boolean validateToken(String token, JwtUserPrincipal userDetails) {
        final String audience = getAudienceFromToken(token);
        return (!isTokenExpired(token) && audience.equals(userDetails.getUser().getUserid().toString()));
    }

    // retrieve username from jwt token
    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // retrieve userid/aud from jwt token
    public String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
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
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

}
