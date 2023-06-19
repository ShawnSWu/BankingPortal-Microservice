package com.sypulse.auth.domain;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtTokenService {

    private static final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour

    @Value("${app.jwt.secret}")
    private String secret;

    public String generateToken(BankUser userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public void validateTokenSecret(String token) {
        if (isTokenExpired(token)) {
            throw new JwtException("Token is not valid");
        }

        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UsernameNotFoundException("Token is not valid ");
        }
    }

    private Date extractExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UsernameNotFoundException("Token is not valid ");
        }
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpirationDate(token);
        return expirationDate.before(new Date());
    }
}
