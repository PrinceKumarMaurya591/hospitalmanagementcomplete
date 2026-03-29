# Security Hardening Guide

## Overview
This document outlines security best practices and hardening measures for the Hospital Management System.

## 1. Authentication & Authorization

### JWT Configuration
```yaml
# application-security.yml
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-change-in-production}
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days
  issuer: hospital-management-system
  audience: hospital-clients
```

### Password Policies
- Minimum length: 12 characters
- Require uppercase, lowercase, numbers, and special characters
- Password history: Last 5 passwords cannot be reused
- Maximum failed attempts: 5 before account lockout
- Lockout duration: 30 minutes

### Multi-Factor Authentication (Optional)
```java
// Enable for sensitive operations
@PreAuthorize("hasRole('ADMIN') and @securityService.hasMFA()")
public void performSensitiveOperation() {
    // Admin operations requiring MFA
}
```

## 2. Network Security

### HTTPS Configuration
```nginx
# nginx.conf
server {
    listen 443 ssl http2;
    server_name hospital.example.com;
    
    ssl_certificate /etc/ssl/certs/hospital.crt;
    ssl_certificate_key /etc/ssl/private/hospital.key;
    
    # Strong SSL configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;
    
    # HSTS
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
}
```

### Firewall Rules
```bash
# UFW configuration
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 8080/tcp  # API Gateway
sudo ufw enable
```

## 3. Database Security

### PostgreSQL Hardening
```sql
-- Secure PostgreSQL configuration
ALTER SYSTEM SET password_encryption = 'scram-sha-256';
ALTER SYSTEM SET ssl = on;
ALTER SYSTEM SET ssl_cert_file = '/var/lib/postgresql/server.crt';
ALTER SYSTEM SET ssl_key_file = '/var/lib/postgresql/server.key';

-- Create limited access users
CREATE USER app_user WITH PASSWORD 'strong-password';
GRANT CONNECT ON DATABASE hospital TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;

-- Enable row-level security
ALTER TABLE patients ENABLE ROW LEVEL SECURITY;
CREATE POLICY patient_access ON patients
    USING (created_by = current_user OR current_user = 'admin');
```

### SQL Injection Prevention
```java
// Use parameterized queries
@Query("SELECT p FROM Patient p WHERE p.status = :status AND p.createdAt >= :date")
List<Patient> findActivePatientsSince(@Param("status") String status, 
                                      @Param("date") LocalDateTime date);

// Never do this (vulnerable to SQL injection)
@Query("SELECT p FROM Patient p WHERE p.name = '" + name + "'") // BAD!
```

## 4. Application Security

### Input Validation
```java
@RestController
@Validated
public class PatientController {
    
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {
        // Bean Validation handles input validation
        return ResponseEntity.ok(patientService.createPatient(request));
    }
}

// Validation annotations in DTO
public class PatientRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;
    
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;
    
    @Email
    @NotBlank
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phoneNumber;
    
    @Past
    private LocalDate dateOfBirth;
}
```

### XSS Protection
```java
// Spring Security configuration
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .xssProtection(Customizer.withDefaults())
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                                     "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                     "style-src 'self' 'unsafe-inline'; " +
                                     "img-src 'self' data: https:; " +
                                     "font-src 'self' data:;")
                )
                .frameOptions(FrameOptionsConfig::sameOrigin)
            );
        return http.build();
    }
}

// Frontend protection (React)
import DOMPurify from 'dompurify';

const SafeComponent = ({ userContent }) => {
    const cleanContent = DOMPurify.sanitize(userContent);
    return <div dangerouslySetInnerHTML={{ __html: cleanContent }} />;
};
```

### CSRF Protection
```java
// Spring Security CSRF configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/auth/**") // Public endpoints
            );
        return http.build();
    }
}
```

## 5. API Security

### Rate Limiting
```java
// Redis-based rate limiting
@Component
public class RateLimiterService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public boolean isAllowed(String key, int maxRequests, long windowInSeconds) {
        String redisKey = "rate_limit:" + key;
        Long current = redisTemplate.opsForValue().increment(redisKey);
        
        if (current == 1) {
            redisTemplate.expire(redisKey, windowInSeconds, TimeUnit.SECONDS);
        }
        
        return current <= maxRequests;
    }
}

// Usage in controller
@GetMapping("/api/patients")
@RateLimit(maxRequests = 100, windowInSeconds = 60)
public ResponseEntity<List<PatientResponse>> getPatients() {
    // ...
}
```

### API Key Authentication
```java
// For external API consumers
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && isValidApiKey(apiKey)) {
            Authentication auth = new ApiKeyAuthenticationToken(apiKey);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## 6. Data Protection

### Encryption at Rest
```java
// Sensitive data encryption
@Entity
public class Patient {
    
    @Id
    private String id;
    
    @Convert(converter = EncryptionConverter.class)
    private String ssn; // Social Security Number
    
    @Convert(converter = EncryptionConverter.class)
    private String insurancePolicyNumber;
    
    // Other fields...
}

@Component
public class EncryptionConverter implements AttributeConverter<String, String> {
    
    private final EncryptionService encryptionService;
    
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptionService.encrypt(attribute);
    }
    
    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptionService.decrypt(dbData);
    }
}
```

### Data Masking
```java
// For logging and responses
public class PatientResponse {
    
    private String id;
    private String firstName;
    private String lastName;
    
    @JsonIgnore
    private String ssn; // Never expose in responses
    
    @JsonProperty("ssn")
    public String getMaskedSsn() {
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }
    
    @JsonProperty("phoneNumber")
    public String getMaskedPhoneNumber() {
        return phoneNumber.replaceAll(".(?=.{4})", "*");
    }
}
```

## 7. Logging & Monitoring

### Security Logging
```java
@Aspect
@Component
@Slf4j
public class SecurityLoggingAspect {
    
    @AfterThrowing(pointcut = "execution(* com.hospital..*.*(..))", 
                   throwing = "ex")
    public void logSecurityException(JoinPoint joinPoint, SecurityException ex) {
        log.warn("Security exception in {}: {}", 
                 joinPoint.getSignature().getName(), 
                 ex.getMessage());
        
        // Log to security audit log
        auditLogService.logSecurityEvent(
            SecurityEvent.builder()
                .eventType("SECURITY_EXCEPTION")
                .userId(getCurrentUserId())
                .ipAddress(getClientIp())
                .details(ex.getMessage())
                .build()
        );
    }
}
```

### Audit Trail
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Patient {
    
    @CreatedBy
    private String createdBy;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedBy
    private String lastModifiedBy;
    
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
    
    // Business fields...
}

@Entity
public class AuditLog {
    
    @Id
    private String id;
    
    private String eventType;
    private String userId;
    private String entityType;
    private String entityId;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    
    // Getters and setters...
}
```

## 8. Dependency Security

### Dependency Scanning
```xml
<!-- OWASP Dependency Check Maven plugin -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.0.2</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
        <skipTestScope>true</skipTestScope>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Regular Updates
```bash
# Check for vulnerabilities
npm audit
mvn dependency-check:check

# Update dependencies
npm update
mvn versions:use-latest-versions
```

## 9. Container Security

### Docker Security
```dockerfile
# Use minimal base image
FROM eclipse-temurin:17-jre-alpine

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy application
COPY --chown=appuser:appgroup target/*.jar app.jar

# Security scanning
# RUN trivy image --exit-code 1 --severity HIGH,CRITICAL myimage:latest
```

### Kubernetes Security
```yaml
# pod-security.yaml
apiVersion: v1
kind: PodSecurityPolicy
metadata:
  name: hospital-psp
spec:
  privileged: false
  allowPrivilegeEscalation: false
  requiredDropCapabilities:
    - ALL
  volumes:
    - 'configMap'
    - 'emptyDir'
    - 'secret'
  hostNetwork: false
  hostIPC: false
  hostPID: false
  runAsUser:
    rule: 'MustRunAsNonRoot'
  seLinux:
    rule: 'RunAsAny'
  supplementalGroups:
    rule: 'MustRunAs'
    ranges:
      - min: 1
        max: 65535
  fsGroup:
    rule: 'MustRunAs'
    ranges:
      - min: 1
        max: 65535
```

## 10. Incident Response

### Security Incident Plan
1. **Detection**: Monitor logs for suspicious activities
2. **Containment**: Isolate affected systems
3. **Eradication**: Remove malicious components
4. **Recovery**: Restore from clean backups
5. **Post-Incident**: Analyze and improve security

### Emergency Contacts
- Security Team: security@hospital.com
- Infrastructure Team: infra@hospital.com
- Legal Team: legal@hospital.com

## 11. Compliance

### HIPAA Compliance Checklist
- [x] Access controls and authentication
- [x] Audit controls and logging
- [x] Integrity controls
- [x] Transmission security
- [x] Data encryption at rest
- [x] Business associate agreements
- [x] Risk assessment and management
- [x] Security awareness training

### GDPR Compliance
- [x] Data minimization
- [x] Purpose limitation
- [x] Storage limitation
- [x] Right to access
- [x] Right to erasure
- [x] Data portability
- [x] Privacy by design

## 12. Regular Security Tasks

### Monthly Tasks
- Review access logs
- Update security patches
- Review firewall rules
- Test backup restoration

### Quarterly Tasks
- Security penetration testing
- Update encryption keys
- Review security policies
- Employee security training

### Annual Tasks
- Full security audit
- Update disaster recovery plan
- Review compliance requirements
- Security architecture review

## Conclusion
Security is an ongoing process, not a one-time setup. Regularly review and update security measures to protect against evolving threats. Always follow the principle of least privilege and defense in depth.