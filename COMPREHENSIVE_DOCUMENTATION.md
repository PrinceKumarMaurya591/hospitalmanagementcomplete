# Hospital Management System - Comprehensive Documentation

## Overview

The Hospital Management System is a modern, microservices-based application designed to manage hospital operations including patient management, doctor scheduling, appointment booking, billing, and reporting. The system follows a microservices architecture with separate services for each domain and uses modern technologies including Spring Boot, React, Docker, and Kafka.

## System Architecture

### Microservices Structure

1. **API Gateway** (`/api-gateway/`)
   - Entry point for all client requests
   - Routes requests to appropriate microservices
   - Handles authentication and rate limiting
   - Configurable routing via config-server

2. **Appointment Service** (`/appointment-service/`)
   - Manages medical appointments between patients and doctors
   - Handles scheduling, status updates, and cancellations
   - Integrates with Kafka for event-driven notifications
   - REST API for CRUD operations on appointments

3. **Doctor Service** (`/doctor-service/`)
   - Manages doctor profiles and information
   - Tracks doctor availability, qualifications, and schedules
   - Handles doctor ratings and appointment statistics
   - Validates medical license expiration

4. **Patient Service** (`/patient-service/`)
   - Manages patient records and information
   - Tracks patient medical history and demographics
   - Handles patient registration and profile management

5. **Billing Service** (`/billing-service/`)
   - Manages hospital billing and payments
   - Generates invoices for appointments and services
   - Processes payments and tracks financial transactions

6. **User Service** (`/user-service/`)
   - Manages user authentication and authorization
   - Handles user registration, login, and role management
   - Implements JWT-based authentication

7. **Notification Service** (`/notification-service/`)
   - Sends notifications (email, SMS, push) for appointments, bills, etc.
   - Integrates with external notification providers

8. **Config Server** (`/config-server/`)
   - Centralized configuration management for all microservices
   - Externalizes configuration properties
   - Supports different environments (dev, test, prod)

9. **Service Registry** (`/service-registry/`)
   - Service discovery using Eureka
   - Enables dynamic service registration and discovery
   - Facilitates load balancing and failover

### Frontend Application (`/frontend/`)
- React-based single-page application
- TypeScript for type safety
- Tailwind CSS for styling
- Redux for state management
- React Router for navigation
- Role-based access control (RBAC)

## Key Components Documentation

### 1. Appointment Service Components

#### Appointment Entity (`Appointment.java`)
- **Purpose**: Represents a medical appointment between a patient and doctor
- **Key Fields**:
  - `id`: Unique identifier (auto-generated)
  - `patientId`: Reference to patient
  - `doctorId`: Reference to doctor
  - `appointmentDateTime`: Scheduled date/time
  - `appointmentType`: Type of appointment (consultation, follow-up, etc.)
  - `status`: Current status (scheduled, confirmed, completed, cancelled)
  - `reason`: Medical reason for appointment
  - `notes`: Additional notes
  - `createdAt`, `updatedAt`: Timestamps for tracking

#### Appointment Service (`AppointmentService.java`)
- **Purpose**: Core business logic for appointment management
- **Key Methods**:
  - `createAppointment()`: Creates new appointment with Kafka event
  - `getAppointmentById()`: Retrieves appointment by ID
  - `getAppointmentsByPatientId()`: Gets all appointments for a patient
  - `getAppointmentsByDoctorId()`: Gets all appointments for a doctor
  - `updateAppointment()`: Updates appointment details
  - `updateAppointmentStatus()`: Changes appointment status
  - `deleteAppointment()`: Deletes appointment with cancellation event
  - `getAppointmentsByDateRange()`: Filters appointments by date range
  - `getAppointmentsByStatus()`: Filters appointments by status

#### Appointment Controller (`AppointmentController.java`)
- **Purpose**: REST API endpoints for appointment operations
- **Endpoints**:
  - `POST /api/appointments`: Create new appointment
  - `GET /api/appointments/{id}`: Get appointment by ID
  - `GET /api/appointments/patient/{patientId}`: Get appointments by patient
  - `GET /api/appointments/doctor/{doctorId}`: Get appointments by doctor
  - `PUT /api/appointments/{id}`: Update appointment
  - `PATCH /api/appointments/{id}/status`: Update appointment status
  - `DELETE /api/appointments/{id}`: Delete appointment
  - `GET /api/appointments/date-range`: Get appointments by date range
  - `GET /api/appointments/status/{status}`: Get appointments by status

### 2. Doctor Service Components

#### Doctor Entity (`Doctor.java`)
- **Purpose**: Represents a medical doctor with comprehensive information
- **Key Fields**:
  - Personal details: `firstName`, `lastName`, `email`, `phone`
  - Professional details: `specialization`, `department`, `qualification`, `experienceYears`
  - Availability: `availability`, `isAvailable`, `isActive`
  - Financial: `consultationFee`
  - Performance metrics: `rating`, `totalRatings`, `totalAppointments`
  - License information: `licenseNumber`, `licenseExpiryDate`
  - Additional info: `bio`, `languages`, `profilePictureUrl`
  - Timestamps: `createdAt`, `updatedAt`

- **Key Methods**:
  - `getFullName()`: Returns concatenated first and last name
  - `addRating()`: Updates average rating with new rating
  - `incrementAppointments()`: Increases appointment count
  - `isLicenseValid()`: Checks if medical license is valid

### 3. Frontend Application

#### Main App Component (`App.tsx`)
- **Purpose**: Root component managing routing and authentication
- **Key Features**:
  - Role-based routing with different dashboards for Admin, Doctor, Patient
  - Public routes: Login, Register, Forgot Password
  - Protected routes with role-based access control
  - Loading state management during authentication
  - Conditional rendering of Navbar and Sidebar

#### Routing Structure:
- **Public Routes**: Accessible without authentication
  - `/login`: User login page
  - `/register`: User registration page
  - `/forgot-password`: Password recovery page

- **Protected Routes**: Require authentication
  - `/dashboard`: Role-specific dashboard
  - `/patients/*`: Patient management (Admin, Doctor, Receptionist)
  - `/doctors/*`: Doctor management (Admin, Receptionist)
  - `/appointments/*`: Appointment management (All roles)
  - `/bills/*`: Billing management (Admin, Receptionist, Patient)
  - `/reports`: Reports dashboard (Admin only)
  - `/profile`, `/change-password`: User settings (All authenticated users)

### 4. API Gateway Components

#### Route Configuration (`RouteConfiguration.java`)
- **Purpose**: Configures routing rules for API Gateway
- **Key Features**:
  - Programmatic route configuration as fallback to YAML
  - Integration with JWT authentication filter
  - Support for custom routing logic
  - Service discovery integration via Eureka

## Database Design

### Appointment Table (`appointments`)
```sql
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date_time TIMESTAMP NOT NULL,
    appointment_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Doctor Table (`doctors`)
```sql
CREATE TABLE doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    qualification VARCHAR(200) NOT NULL,
    experience_years INT,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    availability TEXT,
    bio TEXT,
    languages VARCHAR(255),
    license_number VARCHAR(100),
    license_expiry_date DATE,
    is_available BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    rating DECIMAL(3,2) DEFAULT 0.0,
    total_ratings INT DEFAULT 0,
    total_appointments INT DEFAULT 0,
    profile_picture_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_doctor_email (email),
    INDEX idx_doctor_phone (phone),
    INDEX idx_doctor_name (first_name, last_name),
    INDEX idx_doctor_specialization (specialization),
    INDEX idx_doctor_department (department),
    INDEX idx_doctor_active (is_active)
);
```

## Event-Driven Architecture

### Kafka Integration
- **Appointment Events**: `APPOINTMENT_CREATED`, `APPOINTMENT_UPDATED`, `APPOINTMENT_STATUS_UPDATED`, `APPOINTMENT_CANCELLED`
- **Event Producers**: `AppointmentEventProducer` in appointment service
- **Event Consumers**: Notification service, billing service, analytics service

### Event Flow:
1. Appointment created → `APPOINTMENT_CREATED` event → Notification service sends confirmation
2. Appointment updated → `APPOINTMENT_UPDATED` event → Relevant services update their state
3. Appointment status changed → `APPOINTMENT_STATUS_UPDATED` event → Billing service triggers invoicing
4. Appointment cancelled → `APPOINTMENT_CANCELLED` event → Notification service sends cancellation

## Security Implementation

### Authentication & Authorization
- **JWT-based authentication** via API Gateway
- **Role-Based Access Control (RBAC)** with roles: ADMIN, DOCTOR, PATIENT, RECEPTIONIST
- **Spring Security** for backend services
- **Redux-based auth state management** in frontend

### API Security
- **HTTPS enforcement** for all endpoints
- **CORS configuration** for frontend-backend communication
- **Rate limiting** to prevent abuse
- **Input validation** using Jakarta Validation API

## Deployment Architecture

### Docker & Docker Compose
- Each microservice has its own `Dockerfile`
- `docker-compose.yml` for local development and testing
- Service discovery via Eureka
- Config server for centralized configuration

### Monitoring & Observability
- **Prometheus** for metrics collection
- **Grafana** for visualization
- **Spring Boot Actuator** for health checks
- **Centralized logging** with ELK stack

## Development Guidelines

### Code Structure
- **Backend**: Follows Spring Boot best practices with layered architecture (Controller → Service → Repository → Model)
- **Frontend**: Follows React best practices with component-based architecture
- **Package Structure**: Domain-driven package organization
- **Naming Conventions**: Clear, descriptive names following Java/TypeScript conventions

### Testing Strategy
- **Unit Tests**: JUnit for backend, Jest for frontend
- **Integration Tests**: Spring Boot Test for backend services
- **API Tests**: Postman collection available
- **End-to-End Tests**: Cypress for frontend

### Code Quality
- **Static Analysis**: SonarQube integration
- **Code Formatting**: Prettier for frontend, Checkstyle for backend
- **Dependency Management**: Maven for backend, npm/yarn for frontend

## API Documentation

### Base URLs
- **API Gateway**: `http://localhost:8080`
- **Frontend**: `http://localhost:3000`
- **Service Registry**: `http://localhost:8761`
- **Config Server**: `http://localhost:8888`

### Common Headers
```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
Accept: application/json
```

### Error Responses
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Appointment not found with ID: 123",
  "path": "/api/appointments/123"
}
```

## Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+
- PostgreSQL 14+

### Setup Instructions
1. Clone the repository
2. Run `docker-compose up` to start all services
3. Access frontend at `http://localhost:3000`
4. Use default credentials: admin/admin

### Development Workflow
1. Make changes to relevant service
2. Run tests: `mvn test` or `npm test`
3. Build Docker image: `docker build -t service-name .`
4. Test locally with Docker Compose
5. Create pull request with comprehensive documentation

## Troubleshooting

### Common Issues
1. **Service discovery failures**: Check Eureka server is running
2. **Database connection issues**: Verify PostgreSQL is accessible
3. **CORS errors**: Check API Gateway CORS configuration
4. **Authentication failures**: Verify JWT token validity and expiration

### Logs Location
- **Application logs**: `/logs` directory in each service
- **Docker logs**: `docker-compose logs <service-name>`
- **System logs**: Check Docker daemon logs

## Performance Considerations

### Database Optimization
- Proper indexing on frequently queried columns
- Connection pooling with HikariCP
- Query optimization with JPA hints
- Database partitioning for large tables

### Caching Strategy
- Redis for session storage
- Spring Cache for frequently accessed data
- CDN for static assets in production

### Load Balancing
- API Gateway load balancing across service instances
- Database read replicas for heavy read operations
- Horizontal scaling of stateless services

## Future Enhancements

### Planned Features
1. **Telemedicine integration**: Video consultation support
2. **AI-powered diagnostics**: Machine learning for preliminary diagnosis
3. **Mobile applications**: iOS and Android native apps
4. **IoT integration**: Medical device connectivity
5. **Blockchain**: Secure medical records management

### Technical Improvements
1. **GraphQL API**: For flexible data fetching
2. **gRPC**: For inter-service communication
3. **Kubernetes**: For production orchestration
4. **Service Mesh**: Istio for advanced traffic management
5. **Chaos Engineering**: Resilience testing

## Conclusion

This Hospital Management System represents a modern, scalable, and maintainable solution for healthcare management. The microservices architecture ensures loose coupling and independent deployability, while the event-driven design enables real-time updates and integrations. The comprehensive documentation ensures maintainability and facilitates onboarding of new developers.

For additional support or to report issues, please refer to the project's GitHub repository or contact the development team.