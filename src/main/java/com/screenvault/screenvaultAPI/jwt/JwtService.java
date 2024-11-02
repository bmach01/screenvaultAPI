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

    private String getSecretKey() {
        return System.getenv("SCREENVAULT_JWT_SECRET_KEY");
    }
    private final String jwtTypeClaim = "TYPE";

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .signWith(getSigningKey())
                .claim(jwtTypeClaim, JwtType.TOKEN)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .signWith(getSigningKey())
                .claim(jwtTypeClaim, JwtType.REFRESH_TOKEN)
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValidToken(String token, User user) {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();

        return username.equals(user.getUsername()) &&
                isNotExpired(claims.getExpiration()) &&
                JwtType.valueOf((String)claims.get(jwtTypeClaim)) == JwtType.TOKEN;
    }

    public boolean isValidRefreshToken(String token, User user) {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();

        return username.equals(user.getUsername()) &&
                isNotExpired(claims.getExpiration()) &&
                JwtType.valueOf((String)claims.get(jwtTypeClaim)) == JwtType.REFRESH_TOKEN;
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
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
