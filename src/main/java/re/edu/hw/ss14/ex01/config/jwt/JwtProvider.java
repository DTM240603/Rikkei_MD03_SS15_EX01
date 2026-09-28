package re.edu.hw.ss14.ex01.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, "access", accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, "refresh", refreshTokenExpiration);
    }

    public String extractEmailFromAccessToken(String token) {
        Claims claims = parseClaims(token);
        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new IllegalArgumentException("Token không phải access token");
        }
        return claims.getSubject();
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    private String generateToken(UserDetails userDetails, String tokenType, long expiration) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + expiration);
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .claim("tokenType", tokenType)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
