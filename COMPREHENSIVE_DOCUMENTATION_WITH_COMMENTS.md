# Hospital Management System - Comprehensive Documentation with Detailed Comments

## Overview

The Hospital Management System is a modern, microservices-based application designed to manage hospital operations including patient management, doctor scheduling, appointment booking, billing, and reporting. The system follows a microservices architecture with separate services for each domain and uses modern technologies including Spring Boot, React, Docker, and Kafka.

## Detailed Component Analysis with Comments

### 1. API Gateway Service

#### GatewayApplication.java
```java
package com.hospital.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication  // Marks this as a Spring Boot application
@EnableDiscoveryClient   // Enables service registration and discovery with Eureka
@EnableWebFluxSecurity   // Enables WebFlux security for reactive applications
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);  // Starts the Spring Boot application
    }
}
```

**What this class does:**
- Main entry point for the API Gateway service
- Enables service discovery to register with Eureka server
- Configures WebFlux security for handling authentication in reactive applications
- Bootstraps the Spring Boot application

#### application.yml (API Gateway Configuration)
```yaml
server:
  port: 8080  # API Gateway runs on port 8080

spring:
  application:
    name: api-gateway  # Service name for service discovery
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true  # Enables automatic route creation from service discovery
          lower-case-service-id: true  # Converts service names to lowercase for routing
```

**Key Configuration Sections:**

1. **Server Configuration**:
   - Port 8080: Main entry point for all API requests
   - Excludes Spring Security auto-configuration for custom security setup

2. **Spring Cloud Gateway**:
   - Service discovery integration: Automatically creates routes based on registered services
   - HTTP client configuration: Connection pooling and timeout settings
   - Default filters: CORS handling and session management

3. **Eureka Client**:
   - Registers with Eureka service registry
   - Fetches registry every 5 seconds for service discovery
   - Configures instance metadata for health checks

4. **Resilience4j Circuit Breaker**:
   - Configures circuit breakers for each microservice
   - Prevents cascading failures when services are unavailable
   - Configurable failure thresholds and wait durations

5. **Security Configuration**:
   - JWT secret for token validation
   - Token validity periods (24 hours for access, 7 days for refresh)
   - CORS configuration for frontend integration

#### Dockerfile (API Gateway)
```dockerfile
# Build stage - Uses Maven to build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B  # Downloads all dependencies
COPY src ./src
RUN mvn clean package -DskipTests  # Builds the application JAR

# Runtime stage - Uses lightweight JRE for running the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring  # Creates non-root user for security
USER spring:spring  # Switches to non-root user
COPY --from=build /app/target/api-gateway-1.0.0.jar app.jar  # Copies built JAR
EXPOSE 8080  # Exposes port 8080
ENTRYPOINT ["java", "-jar", "app.jar"]  # Runs the application
```

**What this Dockerfile does:**
- Uses multi-stage build for smaller final image
- Build stage: Compiles Java code and creates JAR file
- Runtime stage: Uses lightweight Alpine Linux with JRE
- Creates non-root user for better security
- Exposes port 8080 for API access

### 2. User Service

#### UserServiceApplication.java
```java
package com.hospital.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDiscoveryClient  // Registers with Eureka service registry
@EnableJpaAuditing      // Enables automatic timestamp auditing for JPA entities
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

**What this class does:**
- Main entry point for User Service
- Enables JPA auditing for automatic `createdAt` and `updatedAt` timestamps
- Registers with service discovery for other services to find it

#### User.java (Entity Model)
```java
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)  // Enables automatic timestamp updates
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)  // Uses UUID for primary key
    private String id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String username;
    
    // ... other fields
    
    @ElementCollection(fetch = FetchType.EAGER)  // Roles stored in separate table
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    // Helper methods for business logic
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLocked = true;  // Locks account after 5 failed attempts
            this.lockTime = LocalDateTime.now();
        }
    }
}
```

**What nginx.conf does:**
- **Worker Configuration**: Sets maximum concurrent connections
- **Gzip Compression**: Reduces file sizes for faster loading
- **Security Headers**: Protects against common web vulnerabilities
- **React Router Support**: Handles client-side routing with fallback to index.html
- **API Proxy**: Routes API requests to backend API Gateway
- **Static File Caching**: Improves performance with long cache times for static assets

### 5. Docker Compose Configuration

#### docker-compose.yml (Orchestration)
```yaml
# version: '3.8'  # Docker Compose version

services:
  # PostgreSQL Databases - Each service has its own database
  patient-db:
    image: postgres:15-alpine
    container_name: patient-db
    environment:
      POSTGRES_DB: patient_db  # Database name
      POSTGRES_USER: hospital   # Database user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}  # Password from environment
    ports:
      - "5438:5432"  # Maps host port 5438 to container port 5432
    volumes:
      - patient-data:/var/lib/postgresql/data  # Persistent storage
    networks:
      - hospital-network  # Connects to application network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U hospital -d patient_db"]  # Health check
      interval: 10s
      timeout: 5s
      retries: 5
```

**Key Docker Compose Sections:**

1. **Database Services**:
   - Separate PostgreSQL instances for each microservice
   - Persistent volumes for data storage
   - Health checks for service reliability
   - Network isolation within `hospital-network`

2. **Message Broker**:
   - Kafka with Zookeeper for event-driven architecture
   - Configures brokers, listeners, and replication
   - Health monitoring for message queue availability

3. **Cache Service**:
   - Redis for session storage and caching
   - Password-protected Redis instance
   - Persistent volume for cache data

4. **Infrastructure Services**:
   - Config Server: Centralized configuration management
   - Service Registry (Eureka): Service discovery and registration
   - API Gateway: Entry point for all API requests

5. **Microservices**:
   - Each business service (user, patient, doctor, appointment, billing, notification)
   - Builds from local Dockerfiles
   - Environment-specific configuration
   - Health checks and dependency management

6. **Networking**:
   - `hospital-network`: Bridge network for inter-service communication
   - Isolated from host network for security
   - DNS-based service discovery using service names

7. **Volumes**:
   - Persistent storage for databases and Redis
   - Named volumes for data persistence across container restarts
   - Separate volumes for each service's data

### 6. Other Key Services

#### Appointment Service
**Purpose**: Manages medical appointments between patients and doctors
**Key Features**:
- Scheduling with date/time validation
- Status tracking (scheduled, confirmed, completed, cancelled)
- Integration with Kafka for event-driven notifications
- Conflict detection for overlapping appointments

#### Doctor Service
**Purpose**: Manages doctor profiles and availability
**Key Features**:
- Doctor information management (specialization, qualifications, experience)
- Availability scheduling and calendar management
- Rating system for doctor performance
- License validation and expiry tracking

#### Billing Service
**Purpose**: Handles hospital billing and payments
**Key Features**:
- Invoice generation for appointments and services
- Payment processing and tracking
- Integration with appointment service for automatic billing
- Financial reporting and analytics

#### Notification Service
**Purpose**: Sends notifications across multiple channels
**Key Features**:
- Email notifications (verification, appointment reminders, billing)
- SMS integration for critical alerts
- Kafka event consumption for real-time notifications
- Template-based notification system

### 7. Property Files Analysis

#### Common Configuration Patterns

**Database Configuration**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://service-db:5432/service_db  # Service-specific database
    username: hospital  # Consistent username across services
    password: ${POSTGRES_PASSWORD}  # Environment variable for security
    hikari:
      maximum-pool-size: 10  # Connection pooling for performance
      minimum-idle: 5
```

**Service Discovery Configuration**:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://service-registry:8761/eureka/  # Eureka server URL
    fetch-registry: true  # Fetches registry of other services
    register-with-eureka: true  # Registers itself with Eureka
```

**Kafka Configuration**:
```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092  # Kafka broker address
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      properties:
        acks: all  # Highest reliability setting
        retries: 3  # Retry failed messages
```

**Security Configuration**:
```yaml
security:
  jwt:
    secret: ${JWT_SECRET}  # JWT signing key from environment
    validity: 86400000  # 24 hours in milliseconds
    refresh-validity: 604800000  # 7 days in milliseconds
```

### 8. Architecture Patterns and Design Decisions

#### Microservices Architecture
**Why Microservices?**
- **Scalability**: Individual services can be scaled independently
- **Technology Diversity**: Different services can use different technologies if needed
- **Team Autonomy**: Teams can develop and deploy services independently
- **Fault Isolation**: Failure in one service doesn't bring down the entire system

#### Event-Driven Architecture
**Kafka Integration**:
- **Loose Coupling**: Services communicate via events rather than direct calls
- **Event Sourcing**: Maintains history of all state changes
- **Real-time Processing**: Enables real-time notifications and updates
- **Scalability**: Kafka handles high-volume event streams efficiently

#### Database Per Service Pattern
**Benefits**:
- **Data Isolation**: Each service owns its data schema
- **Technology Flexibility**: Different databases for different needs
- **Independent Scaling**: Database scaling per service requirements
- **Reduced Coupling**: Services don't share database tables

#### API Gateway Pattern
**Responsibilities**:
- **Request Routing**: Routes requests to appropriate services
- **Authentication**: Centralized JWT validation
- **Rate Limiting**: Protects services from excessive requests
- **CORS Handling**: Manages cross-origin requests for frontend
- **Circuit Breaking**: Prevents cascading failures

### 9. Deployment and Operations

#### Health Checks
Each service implements Spring Boot Actuator health endpoints:
- `/actuator/health`: Basic health status
- `/actuator/info`: Service information
- `/actuator/metrics`: Performance metrics
- `/actuator/prometheus`: Prometheus metrics endpoint

#### Monitoring Stack
- **Prometheus**: Metrics collection from all services
- **Grafana**: Visualization and dashboards
- **ELK Stack**: Centralized logging (Elasticsearch, Logstash, Kibana)
- **Spring Boot Actuator**: Built-in monitoring endpoints

#### Security Considerations
1. **Network Security**:
   - Services communicate over internal Docker network
   - API Gateway exposes only necessary endpoints
   - Database ports not exposed to host machine

2. **Authentication & Authorization**:
   - JWT-based stateless authentication
   - Role-based access control (RBAC)
   - Token refresh mechanism
   - Secure password storage with bcrypt

3. **Data Security**:
   - Environment variables for sensitive configuration
   - Database connection pooling with SSL support
   - Input validation and sanitization
   - SQL injection prevention with JPA

### 10. Development Workflow

#### Local Development
1. **Environment Setup**:
   ```bash
   docker-compose up -d  # Starts all services
   mvn clean package     # Builds Java services
   npm run dev          # Starts frontend development server
   ```

2. **Service Development**:
   - Each service can be developed independently
   - Hot reload for Java services with Spring DevTools
   - Hot module replacement for React frontend

3. **Testing**:
   - Unit tests with JUnit and Mockito
   - Integration tests with TestContainers
   - API testing with Postman collection
   - End-to-end tests with Cypress

#### CI/CD Pipeline
1. **Build Phase**:
   - Maven builds for Java services
   - npm builds for frontend
   - Docker image creation

2. **Test Phase**:
   - Unit test execution
   - Integration test execution
   - Code quality checks (SonarQube)

3. **Deployment Phase**:
   - Docker image tagging and pushing to registry
   - Kubernetes deployment manifests
   - Environment-specific configuration injection

### 11. Troubleshooting Guide

#### Common Issues and Solutions

1. **Service Discovery Failures**:
   ```bash
   # Check Eureka server status
   curl http://localhost:8761/eureka/apps
   
   # Verify service registration
   docker-compose logs service-registry
   ```

2. **Database Connection Issues**:
   ```bash
   # Check database health
   docker-compose ps | grep db
   
   # Test database connection
   docker exec -it patient-db psql -U hospital -d patient_db
   ```

3. **Kafka Connectivity Problems**:
   ```bash
   # Check Kafka status
   docker-compose logs kafka
   
   # List Kafka topics
   docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
   ```

4. **API Gateway Routing Issues**:
   ```bash
   # Check gateway routes
   curl http://localhost:8080/actuator/gateway/routes
   
   # Verify service discovery
   curl http://localhost:8080/actuator/gateway/globalfilters
   ```

#### Logging and Debugging
- **Application Logs**: Each service writes to `logs/` directory
- **Docker Logs**: `docker-compose logs <service-name>`
- **Centralized Logging**: ELK stack for aggregated logs
- **Debug Endpoints**: Spring Boot Actuator for runtime inspection

### 12. Performance Optimization

#### Database Optimization
1. **Indexing Strategy**:
   - Primary keys on all tables
   - Foreign key indexes for join operations
   - Composite indexes for common query patterns
   - Unique constraints for data integrity

2. **Query Optimization**:
   - JPA query hints for performance
   - Pagination for large result sets
   - Lazy loading for relationships
   - Batch operations for bulk inserts/updates

#### Caching Strategy
1. **Redis Cache**:
   - Session storage for user authentication
   - Frequently accessed data caching
   - Cache invalidation strategies
   - Distributed cache for clustered deployments

2. **HTTP Caching**:
   - Browser caching for static assets
   - CDN integration for production
   - Cache-control headers for API responses
   - ETag support for conditional requests

#### Load Balancing
1. **Horizontal Scaling**:
   - Multiple instances of stateless services
   - Load balancer configuration
   - Session affinity when required
   - Health check integration

2. **Database Scaling**:
   - Read replicas for heavy read workloads
   - Connection pooling optimization
   - Query result caching
   - Database partitioning for large datasets

### 13. Future Enhancements

#### Planned Features
1. **Telemedicine Integration**:
   - Video consultation support
   - Real-time chat functionality
   - Medical document sharing
   - Prescription digital signing

2. **AI/ML Capabilities**:
   - Predictive analytics for patient readmission
   - Automated diagnosis assistance
   - Treatment recommendation engine
   - Anomaly detection in patient data

3. **Mobile Applications**:
   - iOS and Android native apps
   - Push notifications
   - Offline functionality
   - Biometric authentication

#### Technical Improvements
1. **Kubernetes Migration**:
   - Container orchestration for production
   - Auto-scaling based on metrics
   - Rolling deployments with zero downtime
   - Service mesh integration (Istio)

2. **GraphQL API**:
   - Flexible data fetching for frontend
   - Reduced over-fetching/under-fetching
   - Strongly typed API schema
   - Real-time subscriptions

3. **gRPC for Inter-Service Communication**:
   - High-performance RPC framework
   - Protocol buffers for efficient serialization
   - Bi-directional streaming
   - Built-in load balancing

### 14. Conclusion

The Hospital Management System represents a comprehensive, modern healthcare management solution built with best practices in software architecture and development. The system's microservices architecture provides scalability, maintainability, and flexibility for future growth.

**Key Strengths**:
1. **Modular Design**: Clear separation of concerns with dedicated services
2. **Event-Driven Architecture**: Real-time updates and loose coupling
3. **Security-First Approach**: Comprehensive authentication and authorization
4. **Containerization**: Consistent deployment across environments
5. **Monitoring & Observability**: Built-in metrics, logging, and health checks

**Development Philosophy**:
- **Test-Driven Development**: Comprehensive test coverage
- **Continuous Integration**: Automated build and test pipelines
- **Infrastructure as Code**: Docker and Docker Compose for environment consistency
- **Documentation-Driven**: Comprehensive documentation for maintainability

This documentation provides a detailed understanding of each component's purpose, implementation details, and interactions within the system. It serves as a valuable resource for developers, operations teams, and stakeholders involved in the Hospital Management System project.
```

**What this class does:**
- Defines the User entity with JPA annotations
- Implements security features: failed login attempts tracking, account locking
- Uses UUID for primary keys (better for distributed systems)
- Includes auditing fields (createdAt, updatedAt) automatically managed
- Defines helper methods for business logic

#### UserService.java (Business Logic)
```java
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
                .map(this::mapToUserResponse)  // Converts entity to DTO
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponse updateUser(String id, User userUpdates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Update username with uniqueness check
        if (userUpdates.getUsername() != null && !userUpdates.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userUpdates.getUsername())) {
                throw new DuplicateResourceException("Username already taken");
            }
            user.setUsername(userUpdates.getUsername());
        }
        
        // Similar checks for email, roles, etc.
        return mapToUserResponse(userRepository.save(user));
    }
    
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        String resetToken = UUID.randomUUID().toString();  // Generates secure token
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpires(LocalDateTime.now().plusHours(24));  // 24-hour expiry
        
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user, resetToken);  // Sends reset email
    }
}
```

**Key Methods and Their Functions:**

1. **`getAllUsers()`**: Retrieves all users (read-only transaction)
2. **`getUserById()`**: Finds user by ID with proper error handling
3. **`updateUser()`**: Updates user details with validation (username/email uniqueness)
4. **`changePassword()`**: Changes password with current password verification
5. **`initiatePasswordReset()`**: Starts password reset process with token generation
6. **`resetPassword()`**: Completes password reset with token validation
7. **`verifyEmail()`**: Verifies email using verification token
8. **`unlockAccount()`**: Resets failed login attempts and unlocks account

#### application.yml (User Service Configuration)
```yaml
server:
  port: 8081
  servlet:
    context-path: /user-service  # All endpoints prefixed with /user-service

spring:
  application:
    name: user-service  # Service name for discovery
  config:
    import: "optional:configserver:http://config-server:8888"  # Config server integration
  
  datasource:
    url: jdbc:postgresql://user-db:5432/user_db  # Database connection
    username: hospital
    password: ${POSTGRES_PASSWORD:StrongPassword123!}
  
  jpa:
    hibernate:
      ddl-auto: update  # Automatically updates database schema
    show-sql: false  # Disables SQL logging in production
  
  kafka:
    bootstrap-servers: kafka:9092  # Kafka connection for event publishing
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
  
  mail:
    host: smtp.gmail.com  # Email configuration for notifications
    port: 587
    username: ${EMAIL_USERNAME:}
    password: ${EMAIL_PASSWORD:}
```

**Configuration Sections Explained:**

1. **Database Configuration**:
   - Connects to PostgreSQL database `user_db`
   - Uses connection pooling with HikariCP
   - Configures JPA with PostgreSQL dialect

2. **Kafka Configuration**:
   - Connects to Kafka broker for event publishing
   - Configures producer settings for reliability
   - Defines topics for user events

3. **Email Configuration**:
   - SMTP settings for sending verification and reset emails
   - Uses environment variables for credentials

4. **Security Configuration**:
   - JWT settings for token generation and validation
   - Token expiry times (24 hours for access, 7 days for refresh)

### 3. Patient Service

#### Patient.java (Entity Model)
```java
@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patient_email", columnList = "email", unique = true),
    @Index(name = "idx_patient_phone", columnList = "phone"),
    @Index(name = "idx_patient_name", columnList = "firstName, lastName")
})
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")  // JSON date format
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @Type(JsonBinaryType.class)  // Stores JSON data in PostgreSQL jsonb type
    @Column(name = "medical_history", columnDefinition = "jsonb")
    private JsonNode medicalHistory;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();  // Automatically updates timestamp
    }
    
    public String getFullName() {
        return firstName + " " + lastName;  // Helper method for full name
    }
    
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();  // Calculates age
    }
}
```

**What this class does:**
- Defines Patient entity with comprehensive medical information
- Uses JSONB type for flexible medical history storage
- Implements database indexes for performance optimization
- Includes helper methods for business logic (getFullName, getAge)
- Automatic timestamp updates with `@PreUpdate`

### 4. Frontend Application

#### App.tsx (Main Application Component)
```typescript
/**
 * Main Application Component
 * 
 * This is the root component of the Hospital Management System frontend.
 * It handles routing, authentication state, and renders the appropriate
 * UI components based on the user's authentication status and role.
 */
function App() {
  // Get authentication state from Redux store
  const { isAuthenticated, isLoading, user } = useSelector((state: RootState) => state.auth);

  // Show loading spinner while checking authentication status
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <LoadingSpinner size="large" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Show navigation bar only for authenticated users */}
      {isAuthenticated && <Navbar />}
      
      <div className="flex">
        {/* Show sidebar only for authenticated users */}
        {isAuthenticated && <Sidebar />}
        
        {/* Main content area with conditional margin based on sidebar presence */}
        <main className={`flex-1 ${isAuthenticated ? 'ml-64' : ''}`}>
          <div className="p-6">
            {/* Application Routes with Role-Based Access Control */}
            <Routes>
              {/* Public routes - accessible without authentication */}
              <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />
              
              {/* Protected routes - require authentication */}
              <Route path="/dashboard" element={
                <PrivateRoute>
                  {/* Role-specific dashboard rendering */}
                  {user?.role === 'ADMIN' ? <AdminDashboard /> : 
                   user?.role === 'DOCTOR' ? <DoctorDashboard /> : 
                   <PatientDashboard />}
                </PrivateRoute>
              } />
              
              {/* Patient management routes with specific role permissions */}
              <Route path="/patients" element={
                <PrivateRoute allowedRoles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
                  <PatientList />
                </PrivateRoute>
              } />
            </Routes>
          </div>
        </main>
      </div>
    </div>
  );
}
```

**Key Features of App Component:**

1. **Authentication State Management**:
   - Reads auth state from Redux store
   - Shows loading spinner during auth checks
   - Conditionally renders UI based on auth status

2. **Role-Based Routing**:
   - Different dashboards for Admin, Doctor, Patient roles
   - Route-level permission checks with `PrivateRoute` component
   - Redirects unauthorized users appropriately

3. **Layout Management**:
   - Conditionally shows Navbar and Sidebar for authenticated users
   - Adjusts main content margin based on sidebar presence
   - Responsive design with Tailwind CSS

#### Dockerfile (Frontend)
```dockerfile
# Build stage - Uses Node.js to build React application
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production  # Installs production dependencies only
COPY . .
RUN npm run build  # Builds the React application

# Production stage - Uses Nginx to serve static files
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html  # Copies built files
COPY nginx.conf /etc/nginx/nginx.conf  # Custom Nginx configuration
EXPOSE 80  # Exposes port 80 for HTTP
CMD ["nginx", "-g", "daemon off;"]  # Starts Nginx in foreground
```

**What this Dockerfile does:**
- Multi-stage build: Node.js for building, Nginx for serving
- Production-only dependency installation for smaller image
- Builds optimized production bundle of React app
- Uses lightweight Alpine Nginx for serving static files
- Custom Nginx configuration for routing and optimization

#### nginx.conf (Frontend Web Server Configuration)
```nginx
events {
    worker_connections 1024;  # Maximum concurrent connections per worker
}

http {
    server {
        listen 80;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html;

        # Gzip compression for better performance
        gzip on;
        gzip_vary on;
        gzip_min_length 1024;
        gzip_types text/plain text/css text/xml text/javascript application/javascript application/json;

        # Security headers
        add_header X-Frame-Options "SAMEORIGIN" always;  # Prevents clickjacking
        add_header X-Content-Type-Options "nosniff" always;  # Prevents MIME sniffing
        add_header X-XSS-Protection "1; mode=block" always;  # XSS protection

        # Handle React Router - single page application routing
        location / {
            try_files $uri $uri/ /index.html;  # Falls back to index.html for client-side routing
        }

        # API proxy to backend - routes API requests to API Gateway
        location /api/ {
            proxy_pass http://api-gateway:8080/;  # Forwards API requests to gateway
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;  # WebSocket support
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;  # Client IP address
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  # Forward chain
            proxy_set_header X-Forwarded-Proto $scheme;  # Original protocol
            proxy_cache_bypass $http_upgrade;
            proxy_read_timeout 300s;  # Long timeout for API calls
            proxy_connect_timeout 75s;
        }

        # Static files cache - improves performance
        location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
            expires 1y;  # Cache static files for 1 year
            add_header Cache-Control "public, immutable";  # Cache control headers
        }
    }
}
