package com.hospital.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    public boolean isValid() {
        return !revoked && !isExpired();
    }
    
    public void revoke() {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
    }
}