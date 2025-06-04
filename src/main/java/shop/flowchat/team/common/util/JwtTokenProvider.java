package shop.flowchat.team.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;

    public UUID getMemberIdFromToken(String token) {
        token = validationAuthorizationHeader(token);
        Claims claims = getClaims(token);
        String subject = claims.getSubject(); // "UUID:ROLE"
        return UUID.fromString(subject.split(":")[0]);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String  validationAuthorizationHeader(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
        }
        return token.substring("Bearer ".length());
    }

}
