package com.legacykeep.legacy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * User Details Service for the Legacy Service.
 * 
 * Provides user authentication details for JWT token validation.
 * Since this is a microservice, it creates UserDetails from JWT claims
 * without requiring a database lookup.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LegacyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for: {}", username);
        
        // For microservice architecture, we trust the JWT token
        // and create UserDetails from the token claims
        // In a real scenario, you might want to validate the user exists
        // by calling the Auth Service or checking Redis cache
        
        return User.builder()
                .username(username)
                .password("") // No password needed for JWT authentication
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
