package com.hospital.userservice.service;

import com.hospital.userservice.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.email.verification-enabled:true}")
    private boolean verificationEnabled;
    
    @Async
    public void sendVerificationEmail(User user) {
        if (!verificationEnabled) {
            log.info("Email verification is disabled, skipping verification email for user: {}", user.getEmail());
            return;
        }
        
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("user", user);
            variables.put("verificationUrl", frontendUrl + "/verify-email?token=" + user.getVerificationToken());
            variables.put("appName", "Hospital Management System");
            
            String subject = "Verify Your Email - Hospital Management System";
            String body = buildEmailBody("email/verification-email", variables);
            
            sendEmail(user.getEmail(), subject, body, true);
            log.info("Verification email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }
    
    @Async
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("user", user);
            variables.put("resetUrl", frontendUrl + "/reset-password?token=" + resetToken);
            variables.put("appName", "Hospital Management System");
            variables.put("expiryHours", 24);
            
            String subject = "Password Reset Request - Hospital Management System";
            String body = buildEmailBody("email/password-reset-email", variables);
            
            sendEmail(user.getEmail(), subject, body, true);
            log.info("Password reset email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
        }
    }
    
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("user", user);
            variables.put("appName", "Hospital Management System");
            variables.put("loginUrl", frontendUrl + "/login");
            variables.put("supportEmail", "support@hospital.com");
            
            String subject = "Welcome to Hospital Management System";
            String body = buildEmailBody("email/welcome-email", variables);
            
            sendEmail(user.getEmail(), subject, body, true);
            log.info("Welcome email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }
    
    @Async
    public void sendAccountLockedEmail(User user) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("user", user);
            variables.put("appName", "Hospital Management System");
            variables.put("supportEmail", "support@hospital.com");
            variables.put("unlockUrl", frontendUrl + "/unlock-account?userId=" + user.getId());
            
            String subject = "Account Locked - Hospital Management System";
            String body = buildEmailBody("email/account-locked-email", variables);
            
            sendEmail(user.getEmail(), subject, body, true);
            log.info("Account locked email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send account locked email to: {}", user.getEmail(), e);
        }
    }
    
    @Async
    public void sendEmail(String to, String subject, String body, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);
            
            mailSender.send(message);
            log.debug("Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private String buildEmailBody(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}