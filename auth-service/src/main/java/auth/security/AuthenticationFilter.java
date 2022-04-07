package auth.security;

import auth.model.auth.AuthLoginRq;
import auth.model.auth.AuthLoginRs;
import auth.model.entity.UserEntity;
import auth.service.JwtService;
import auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationFilter(
            UserService merchantService, AuthenticationManager authManager, JwtService jwtService) {
        super.setAuthenticationManager(authManager);
        this.userService = merchantService;
        this.jwtService = jwtService;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException {
        AuthLoginRq loginRq = objectMapper.readValue(req.getInputStream(), AuthLoginRq.class);
        resp.addHeader("rememberMe", loginRq.getRememberMe());

        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(loginRq.getEmail(), loginRq.getPassword()));
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(
            HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication authResult) {
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserEntity userEntity = userService.findByEmail(username);

        String token = jwtService
                .generateToken(userEntity.getId(), Boolean.parseBoolean(resp.getHeader("rememberMe")));

        resp.addHeader("token", token);
        resp.addHeader("userId", userEntity.getId());
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), AuthLoginRs.ok());
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest req, HttpServletResponse resp, AuthenticationException failed) throws IOException {
        resp.setStatus(HttpStatus.FORBIDDEN.value());
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), AuthLoginRs.fail());
    }
}
