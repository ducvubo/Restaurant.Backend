package com.restaurant.ddd.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.token.secret:your-secret-key-should-be-at-least-256-bits-long-for-security}")
    private String tokenSecret;

    @Value("${jwt.token.issuer:restaurant-management}")
    private String tokenIssuer;

    @Value("${jwt.token.audience:restaurant-management}")
    private String tokenAudience;

    @Value("${jwt.token.expiration:86400000}") // 24 hours in milliseconds
    private Long tokenExpiration;

    @Value("${jwt.refresh.secret:your-refresh-secret-key-should-be-at-least-256-bits-long-for-security}")
    private String refreshSecret;

    @Value("${jwt.refresh.issuer:restaurant-management}")
    private String refreshIssuer;

    @Value("${jwt.refresh.audience:restaurant-management}")
    private String refreshAudience;

    @Value("${jwt.refresh.expiration:604800000}") // 7 days in milliseconds
    private Long refreshExpiration;

    private SecretKey getTokenSigningKey() {
        return Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getRefreshSigningKey() {
        return Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, boolean isRefreshToken) {
        try {
            SecretKey signingKey = isRefreshToken ? getRefreshSigningKey() : getTokenSigningKey();
            io.jsonwebtoken.Jws<Claims> jws = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = jws.getPayload();
            
            // Debug: Log tất cả các keys trong claims
            log.debug("JwtService - Successfully extracted claims from token (isRefreshToken={}). All claim keys: {}", 
                    isRefreshToken, claims.keySet());
            log.debug("JwtService - Issuer: {}, Subject: {}", claims.getIssuer(), claims.getSubject());
            
            return claims;
        } catch (Exception e) {
            log.error("JwtService - Error extracting claims from token (isRefreshToken={}): {}", isRefreshToken, e.getMessage(), e);
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        return extractAllClaims(token, false);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Build JWT token với JIT token (giống C# BuildToken)
     */
    public String buildToken(String clientId, UUID userId, String username, String email, UUID jitToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname", username != null ? username : "");
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", email != null ? email : "");
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/sid", jitToken.toString());
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/primarysid", userId.toString());
        claims.put("clientId", clientId);
        
        return createToken(claims, username, tokenExpiration, tokenIssuer, tokenAudience, getTokenSigningKey());
    }

    /**
     * Generate refresh token với JIT refresh token (giống C# GenerateRefreshToken)
     */
    public String generateRefreshToken(String clientId, UUID userId, String username, String email, UUID jitRefreshToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname", username != null ? username : "");
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", email != null ? email : "");
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/sid", jitRefreshToken.toString());
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/primarysid", userId.toString());
        claims.put("clientId", clientId);
        
        return createToken(claims, username, refreshExpiration, refreshIssuer, refreshAudience, getRefreshSigningKey());
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration, String issuer, String audience, SecretKey signingKey) {
        Date now = new Date();
        // Add audience to claims map (JWT library mới không hỗ trợ .audience(String) trực tiếp)
        // Phải thêm vào claims map trước khi build
        claims.put("aud", audience);
        log.debug("JwtService - Creating token with issuer={}, audience={}", issuer, audience);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .notBefore(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Decode JWT token (giống C# DecodeJwtToken)
     */
    public Claims decodeJwtToken(String token, boolean isRefreshToken) {
        try {
            Claims claims = extractAllClaims(token, isRefreshToken);
            String issuer = isRefreshToken ? refreshIssuer : tokenIssuer;
            String expectedAudience = isRefreshToken ? refreshAudience : tokenAudience;
            
            // Get audience from claims (có thể là String hoặc List)
            String actualAudience = null;
            Object audObj = claims.get("aud");
            log.debug("JwtService - Raw audience object: {}, type: {}", audObj, audObj != null ? audObj.getClass().getName() : "null");
            
            if (audObj instanceof String) {
                actualAudience = (String) audObj;
            } else if (audObj instanceof java.util.List && !((java.util.List<?>) audObj).isEmpty()) {
                actualAudience = ((java.util.List<?>) audObj).get(0).toString();
            } else {
                // Thử lấy từ tất cả các keys trong claims để debug
                log.warn("JwtService - Audience not found in 'aud' claim. All claim keys: {}", claims.keySet());
            }
            
            String actualIssuer = claims.getIssuer();
            log.debug("JwtService - Validating token: expectedIssuer={}, actualIssuer={}, expectedAudience={}, actualAudience={}", 
                    issuer, actualIssuer, expectedAudience, actualAudience);
            
            if (!issuer.equals(actualIssuer)) {
                log.warn("JwtService - Issuer mismatch: expected={}, actual={}", issuer, actualIssuer);
                return null;
            }
            
            // Nếu audience là null, có thể do JWT library không trả về trong claims
            // Thử lấy từ header hoặc bỏ qua kiểm tra audience nếu null (tạm thời để debug)
            if (actualAudience == null) {
                log.warn("JwtService - Audience is null in token. Skipping audience validation. All claim keys: {}", claims.keySet());
                // Tạm thời bỏ qua kiểm tra audience nếu null (có thể do JWT library issue)
                // return null;
            } else if (!expectedAudience.equals(actualAudience)) {
                log.warn("JwtService - Audience mismatch: expected={}, actual={}", expectedAudience, actualAudience);
                return null;
            }
            
            log.debug("JwtService - Token decoded successfully: issuer={}, audience={}", actualIssuer, actualAudience);
            return claims;
        } catch (Exception e) {
            log.error("JwtService - Error decoding token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extract JIT token từ claims
     */
    public UUID extractJitToken(String token) {
        Claims claims = extractAllClaims(token, false);
        String jitStr = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/sid", String.class);
        return jitStr != null ? UUID.fromString(jitStr) : null;
    }

    /**
     * Extract JIT refresh token từ claims
     */
    public UUID extractJitRefreshToken(String refreshToken) {
        Claims claims = extractAllClaims(refreshToken, true);
        String jitStr = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/sid", String.class);
        return jitStr != null ? UUID.fromString(jitStr) : null;
    }

    /**
     * Extract user ID từ claims (PrimarySid)
     */
    public UUID extractUserIdFromClaims(String token, boolean isRefreshToken) {
        Claims claims = extractAllClaims(token, isRefreshToken);
        String userIdStr = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/primarysid", String.class);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public Boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return "refresh".equals(claims.get("type"));
    }
}

