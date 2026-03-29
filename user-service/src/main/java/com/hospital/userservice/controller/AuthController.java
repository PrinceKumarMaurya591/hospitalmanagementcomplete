package com.hospital.userservice.controller;

import com.hospital.userservice.dto.*;
import com.hospital.userservice.service.AuthService;
import com.hospital.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "423", description = "Account locked")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email or username already exists")
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user by invalidating refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token")
    })
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Invalidate all refresh tokens for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout from all devices successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> logoutAll(HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        authService.logoutAll(userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Initiate password reset process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify email using verification token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid verification token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email", description = "Resend email verification link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification email sent"),
            @ApiResponse(responseCode = "400", description = "Email already verified"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> resendVerification(@RequestParam String email) {
        userService.resendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }
    
    private String getUserIdFromRequest(HttpServletRequest request) {
        // Extract user ID from JWT token in request
        // This would typically be done by a filter that adds user ID to request attributes
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in request");
        }
        return userId;
    }
}