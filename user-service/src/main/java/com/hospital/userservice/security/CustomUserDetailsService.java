package com.hospital.userservice.security;

import com.hospital.userservice.model.User;
import com.hospital.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        
        return buildUserDetails(user);
    }
    
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        return buildUserDetails(user);
    }
    
    private UserDetails buildUserDetails(User user) {
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User account is disabled");
        }
        
        if (user.isAccountLocked()) {
            throw new UsernameNotFoundException("User account is locked");
        }
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList()))
                .accountExpired(false)
                .accountLocked(user.isAccountLocked())
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
}