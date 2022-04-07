package auth.model.auth;

import lombok.Data;

@Data
public class AuthLoginRq {

    private String email;
    private String password;
    private String rememberMe;
}
