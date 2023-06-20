package com.sypulse.auth.domain;

import com.sypulse.auth.persentation.dto.UserTokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtTokenService {

    private static final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour

    @Value("${auth.jwt.secret}")
    private String secret;

    public String generateToken(BankUser bankUser) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(bankUser.getId())
                .claim("email", bankUser.getEmail())
                .claim("firstName", bankUser.getFirstName())
                .claim("lastName", bankUser.getLastName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public UserTokenInfo validateTokenSecret(String token) {
        UserTokenInfo user;
        if (isTokenExpired(token)) {
            throw new JwtException("Token is not valid");
        }

        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            // 提取使用者 ID
            user = UserTokenInfo.builder()
                    .id(claims.getSubject())
                    .firstName(claims.get("firstName").toString())
                    .lastName(claims.get("lastName").toString())
                    .email(claims.get("email").toString())
                    .iat(claims.getIssuedAt())
                    .expire(claims.getExpiration())
                    .build();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UsernameNotFoundException("Token is not valid ");
        }
        return user;
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
