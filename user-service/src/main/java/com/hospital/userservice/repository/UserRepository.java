package com.hospital.userservice.repository;

import com.hospital.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByVerificationToken(String verificationToken);
    
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.accountLocked = true AND u.lockTime < :threshold")
    List<User> findLockedAccountsBefore(@Param("threshold") java.time.LocalDateTime threshold);
    
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.createdAt < :threshold")
    List<User> findUnverifiedAccountsBefore(@Param("threshold") java.time.LocalDateTime threshold);
    
    List<User> findByRolesContaining(com.hospital.userservice.model.Role role);
}