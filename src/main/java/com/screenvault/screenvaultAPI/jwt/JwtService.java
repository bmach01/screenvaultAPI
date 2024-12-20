package com.screenvault.screenvaultAPI.jwt;

import com.screenvault.screenvaultAPI.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String jwtTypeClaim = "TYPE";
    private static final int ACCESS_TOKEN_EXP_TIME = 5 * 60 * 1000;
    private static final int REFRESH_TOKEN_EXP_TIME = 7 * 24 * 60 * 60 * 1000;

    private String getSecretKey() {
        return System.getenv("SCREENVAULT_JWT_SECRET_KEY");
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP_TIME))
                .signWith(getSigningKey())
                .claim(jwtTypeClaim, JwtType.ACCESS_TOKEN)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP_TIME))
                .signWith(getSigningKey())
                .claim(jwtTypeClaim, JwtType.REFRESH_TOKEN)
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValidToken(String token, String user) {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();

        return username.equals(user) &&
                isNotExpired(claims.getExpiration()) &&
                JwtType.valueOf((String) claims.get(jwtTypeClaim)) == JwtType.ACCESS_TOKEN;
    }

    public boolean isValidRefreshToken(String token, User user) {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();

        return username.equals(user.getUsername()) &&
                isNotExpired(claims.getExpiration()) &&
                JwtType.valueOf((String) claims.get(jwtTypeClaim)) == JwtType.REFRESH_TOKEN;
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private boolean isNotExpired(Date expirationDate) {
        return expirationDate.after(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
