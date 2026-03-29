package com.hospital.userservice.service;

import com.hospital.userservice.dto.ChangePasswordRequest;
import com.hospital.userservice.dto.UserResponse;
import com.hospital.userservice.exception.DuplicateResourceException;
import com.hospital.userservice.exception.InvalidCredentialsException;
import com.hospital.userservice.exception.ResourceNotFoundException;
import com.hospital.userservice.model.Role;
import com.hospital.userservice.model.User;
import com.hospital.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserResponse(user);
    }
    
    @Transactional
    public UserResponse updateUser(String id, User userUpdates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Update fields if provided
        if (userUpdates.getUsername() != null && !userUpdates.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userUpdates.getUsername())) {
                throw new DuplicateResourceException("Username already taken");
            }
            user.setUsername(userUpdates.getUsername());
        }
        
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdates.getEmail())) {
                throw new DuplicateResourceException("Email already registered");
            }
            user.setEmail(userUpdates.getEmail());
            user.setEmailVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            emailService.sendVerificationEmail(user);
        }
        
        if (userUpdates.getRoles() != null && !userUpdates.getRoles().isEmpty()) {
            user.setRoles(userUpdates.getRoles());
        }
        
        if (userUpdates.isEnabled() != user.isEnabled()) {
            user.setEnabled(userUpdates.isEnabled());
        }
        
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
    
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Transactional
    public UserResponse changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        
        // Validate new password match
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
    
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpires(LocalDateTime.now().plusHours(24));
        
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user, resetToken);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset token"));
        
        if (user.getResetPasswordExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        user.resetFailedLoginAttempts();
        
        userRepository.save(user);
    }
    
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token"));
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        
        userRepository.save(user);
    }
    
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        
        if (user.getVerificationToken() == null) {
            user.setVerificationToken(UUID.randomUUID().toString());
            userRepository.save(user);
        }
        
        emailService.sendVerificationEmail(user);
    }
    
    @Transactional
    public void unlockAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.resetFailedLoginAttempts();
        userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRolesContaining(role).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
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