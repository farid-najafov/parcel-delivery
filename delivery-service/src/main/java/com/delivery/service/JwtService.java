package com.delivery.service;

import com.delivery.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(Constants.HEADER))
                .filter(token -> token.startsWith(Constants.PREFIX))
                .map(token -> token.substring(Constants.PREFIX.length()));
    }

    public Optional<Jws<Claims>> tokenToClaims(String token) {
        try {
            return Optional.of(Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token));
        } catch (SignatureException ex) {
            log.error("JWT: Invalid signature");
        } catch (MalformedJwtException ex) {
            log.error("JWT: Invalid token");
        } catch (ExpiredJwtException ex) {
            log.error("JWT: Expired token");
        } catch (UnsupportedJwtException ex) {
            log.error("JWT: Unsupported token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT: token is empty.");
        }
        return Optional.empty();
    }

    public String claimsToId(Jws<Claims> claims) {
        return claims.getBody().getSubject();
    }
}
