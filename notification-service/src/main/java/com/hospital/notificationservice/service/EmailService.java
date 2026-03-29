package com.hospital.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${notification.email.from:noreply@hospital.com}")
    private String fromEmail;
    
    @Value("${notification.email.reply-to:support@hospital.com}")
    private String replyToEmail;
    
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            // Prepare the Thymeleaf context
            Context context = new Context();
            context.setVariables(variables);
            
            // Process the template
            String htmlContent = templateEngine.process(templateName, context);
            
            // Create MIME message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setReplyTo(replyToEmail);
            
            // Send email
            mailSender.send(mimeMessage);
            
            log.info("Email sent successfully to: {}, subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}, subject: {}", to, subject, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    public void sendAppointmentConfirmation(String to, String patientName, String doctorName, 
                                           String appointmentDate, String appointmentTime) {
        Map<String, Object> variables = Map.of(
                "patientName", patientName,
                "doctorName", doctorName,
                "appointmentDate", appointmentDate,
                "appointmentTime", appointmentTime
        );
        
        sendEmail(to, "Appointment Confirmation - Hospital Management System", 
                 "appointment-confirmation", variables);
    }
    
    public void sendAppointmentReminder(String to, String patientName, String doctorName,
                                       String appointmentDate, String appointmentTime) {
        Map<String, Object> variables = Map.of(
                "patientName", patientName,
                "doctorName", doctorName,
                "appointmentDate", appointmentDate,
                "appointmentTime", appointmentTime
        );
        
        sendEmail(to, "Appointment Reminder - Hospital Management System",
                 "appointment-reminder", variables);
    }
    
    public void sendBillGenerated(String to, String patientName, String invoiceNumber,
                                 String amount, String dueDate) {
        Map<String, Object> variables = Map.of(
                "patientName", patientName,
                "invoiceNumber", invoiceNumber,
                "amount", amount,
                "dueDate", dueDate
        );
        
        sendEmail(to, "Bill Generated - Hospital Management System",
                 "bill-generated", variables);
    }
    
    public void sendPaymentReceipt(String to, String patientName, String transactionId,
                                  String amount, String paymentDate) {
        Map<String, Object> variables = Map.of(
                "patientName", patientName,
                "transactionId", transactionId,
                "amount", amount,
                "paymentDate", paymentDate
        );
        
        sendEmail(to, "Payment Receipt - Hospital Management System",
                 "payment-receipt", variables);
    }
    
    public void sendWelcomeEmail(String to, String userName) {
        Map<String, Object> variables = Map.of(
                "userName", userName
        );
        
        sendEmail(to, "Welcome to Hospital Management System",
                 "welcome-email", variables);
    }
}