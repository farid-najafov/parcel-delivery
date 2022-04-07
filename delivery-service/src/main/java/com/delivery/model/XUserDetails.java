package com.delivery.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class XUserDetails implements UserDetails {

    private final String id;
    private final String fullName;
    private final String encryptedPassword;
    private final String email;
    private final String[] roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(roles)
                .map(s -> "ROLE_" + s)
                .map(s -> (GrantedAuthority) () -> s)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return encryptedPassword;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
