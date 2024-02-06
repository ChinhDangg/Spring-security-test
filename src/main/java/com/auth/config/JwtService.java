package com.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${api.secret-key}")
    private String SECRET_KEY;

    public String extractUsername(String jwtToken) {
        try {
            return extractAllClaims(jwtToken).getSubject();
        } catch(ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (Exception ignored) {}
        return null;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Jwts.claims(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 900))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String userName = extractUsername(jwtToken);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(jwtToken);
    }

    public boolean isTokenValidAndExpiredWithin(String jwtToken, UserDetails userDetails) {
        Claims claims = null;
        try {
            claims = extractAllClaims(jwtToken);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        } catch (Exception ignored) { return false; }
        String userName = claims.getSubject();
        Instant expiredTime = claims.getExpiration().toInstant();
        System.out.println("token expired time: " + expiredTime);
        Instant cookiesMaxTime = new Date((Long)claims.get("cookieMaxTime")).toInstant();
        System.out.println("cookies max time: " + cookiesMaxTime);
        boolean expiredWithin = expiredTime.isBefore(cookiesMaxTime);
        return (userName.equals(userDetails.getUsername())) && expiredWithin;
    }

    public boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    public Date extractExpiration(String jwtToken) {
        try {
            return extractAllClaims(jwtToken).getExpiration();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        } catch (Exception ignored) {}
        return null;
    }

    public Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
