package com.hospital.userservice.service;

import com.hospital.userservice.dto.*;
import com.hospital.userservice.exception.DuplicateResourceException;
import com.hospital.userservice.exception.InvalidCredentialsException;
import com.hospital.userservice.exception.ResourceNotFoundException;
import com.hospital.userservice.kafka.UserEventProducer;
import com.hospital.userservice.model.RefreshToken;
import com.hospital.userservice.model.Role;
import com.hospital.userservice.model.User;
import com.hospital.userservice.repository.RefreshTokenRepository;
import com.hospital.userservice.repository.UserRepository;
import com.hospital.userservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserEventProducer userEventProducer;
    private final EmailService emailService;
    
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = (User) authentication.getPrincipal();
            
            // Reset failed login attempts on successful login
            user.resetFailedLoginAttempts();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = generateAndSaveRefreshToken(user);
            
            return buildLoginResponse(accessToken, refreshToken, user);
            
        } catch (BadCredentialsException e) {
            // Increment failed login attempts
            userRepository.findByEmail(loginRequest.getEmail())
                    .ifPresent(user -> {
                        user.incrementFailedLoginAttempts();
                        userRepository.save(user);
                    });
            
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
    
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        
        // Validate password match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(registerRequest.getRoles())
                .verificationToken(UUID.randomUUID().toString())
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser);
        
        // Publish user registration event
        userEventProducer.publishUserRegistered(savedUser);
        
        return mapToUserResponse(savedUser);
    }
    
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        
        if (!storedToken.isValid()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        User user = storedToken.getUser();
        
        // Generate new access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(user, null, null)
        );
        
        // Generate new refresh token and revoke old one
        String newRefreshToken = generateAndSaveRefreshToken(user);
        storedToken.revoke();
        refreshTokenRepository.save(storedToken);
        
        return buildLoginResponse(newAccessToken, newRefreshToken, user);
    }
    
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }
    
    @Transactional
    public void logoutAll(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());
    }
    
    private String generateAndSaveRefreshToken(User user) {
        String token = jwtTokenProvider.generateRefreshToken(user);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    private LoginResponse buildLoginResponse(String accessToken, String refreshToken, User user) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationDateFromToken(accessToken).getTime() - System.currentTimeMillis())
                .user(LoginResponse.UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .roles(user.getRoles())
                        .emailVerified(user.isEmailVerified())
                        .build())
                .build();
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}