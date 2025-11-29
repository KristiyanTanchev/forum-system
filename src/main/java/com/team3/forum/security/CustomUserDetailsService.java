package com.team3.forum.security;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.User;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = userRepository.findByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        if (user.isDeleted()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                !user.isDeleted(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                !user.isBlocked(), // accountNonLocked
                getAuthorities(user),
                user.getId(),
                user.getEmail(),
                user.isAdmin()
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return authorities;
    }
}