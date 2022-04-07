package auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@PropertySource("classpath:jwt.properties")
public class JwtService {

    @Value("${jwt.expiry.normal}")
    private Long expirationNormal;

    @Value("${jwt.expiry.remember}")
    private Long expirationRemember;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(String randomId, boolean rememberMe) {

        Date now = new Date();
        final long delta = rememberMe ? expirationRemember : expirationNormal;
        Date expiration = new Date(now.getTime() + delta);

        return Jwts.builder()
                .setSubject(randomId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
