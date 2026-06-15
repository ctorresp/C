package grupoC.mascotas.security;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secretKeyBase64){
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        }

        public Boolean validateToken(String token){
            try {
                Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        public Claims getClaimsFromToken(String token){
            return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        }

        public String getUuidFromToken(String token) {
            return getClaimsFromToken(token).getSubject();
        }

        public String generateServiceToken(String subject) {
            Instant now = Instant.now();
            Date issuedAt = Date.from(now);
            Date expiration = Date.from(now.plus(7, ChronoUnit.DAYS));

            JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .claim("rol", "SERVICE")
                .signWith(secretKey, SignatureAlgorithm.HS256);

            return builder.compact();
        }

}
