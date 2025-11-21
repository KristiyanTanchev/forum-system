package com.team3.forum.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter @Setter
public class CustomUserDetails extends User {
    private int id;
    private String email;
    private boolean isAdmin;
    public CustomUserDetails(String username,
                             String password,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean credentialsNonExpired,
                             boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities,
                             int userId,
                             String email,
                             boolean isAdmin) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = userId;
        this.email = email;
        this.isAdmin = isAdmin;
    }
}

