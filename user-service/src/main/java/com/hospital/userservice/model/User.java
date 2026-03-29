package com.hospital.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(nullable = false)
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 100)
    @Column(nullable = false)
    private String password;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
    
    @Column(name = "email_verified")
    @Builder.Default
    private boolean emailVerified = false;
    
    @Column(name = "verification_token")
    private String verificationToken;
    
    @Column(name = "reset_password_token")
    private String resetPasswordToken;
    
    @Column(name = "reset_password_expires")
    private LocalDateTime resetPasswordExpires;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "account_locked")
    @Builder.Default
    private boolean accountLocked = false;
    
    @Column(name = "lock_time")
    private LocalDateTime lockTime;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper methods
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    public void removeRole(Role role) {
        this.roles.remove(role);
    }
    
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
    
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLocked = true;
            this.lockTime = LocalDateTime.now();
        }
    }
    
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.accountLocked = false;
        this.lockTime = null;
    }
}