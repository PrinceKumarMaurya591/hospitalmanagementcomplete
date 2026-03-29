# Hospital Management System

A comprehensive microservices-based hospital management system built with Spring Boot, React, and Docker.

## 🏥 Architecture Overview
                                                                                                         
```
┌─────────────────────────────────────────────────────────────┐
│                    Hospital Management System                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   React     │  │   API       │  │   Service   │        │
│  │   Frontend  │◄─┤   Gateway   │◄─┤   Registry  │        │
│  │   (3000)    │  │   (8080)    │  │   (8761)    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│         │                    │                    │        │
│         │                    │                    │        │
│  ┌──────▼──────────┬─────────▼─────────┬──────────▼──────┐│
│  │   Config        │   Kafka           │   PostgreSQL    ││
│  │   Server        │   Message         │   Databases     ││
│  │   (8888)        │   Broker          │   (5432)        ││
│  └─────────────────┴───────────────────┴─────────────────┘│
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   User      │  │   Patient   │  │   Doctor    │        │
│  │   Service   │  │   Service   │  │   Service   │        │
│  │   (8081)    │  │   (8082)    │  │   (8083)    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │ Appointment │  │   Billing   │  │ Notification│        │
│  │   Service   │  │   Service   │  │   Service   │        │
│  │   (8084)    │  │   (8085)    │  │   (8086)    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐                         │
│  │   Redis     │  │   Prometheus│                         │
│  │   (6379)    │  │   (9090)    │                         │
│  └─────────────┘  └─────────────┘                         │
└─────────────────────────────────────────────────────────────┘
```

## 📋 Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Node.js 18+** and npm
- **Docker 20+** and Docker Compose
- **Maven 3.8+**
- **PostgreSQL 14+** (optional, Docker will run it)
- **Git**

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd hospital

# Start all services
docker-compose up -d

# Check services status
docker-compose ps

# View logs
docker-compose logs -f
```

### Option 2: Local Development

```bash
# 1. Start infrastructure services
docker-compose up -d postgres redis kafka zookeeper

# 2. Build and run backend services
mvn clean package
java -jar target/*.jar

# 3. Start frontend
cd frontend
npm install
npm run dev
```

## 🔧 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Main entry point |
| Service Registry | 8761 | Eureka dashboard |
| Config Server | 8888 | Configuration service |
| User Service | 8081 | Authentication & users |
| Patient Service | 8082 | Patient management |
| Doctor Service | 8083 | Doctor management |
| Appointment Service | 8084 | Appointment scheduling |
| Billing Service | 8085 | Billing & payments |
| Notification Service | 8086 | Email & notifications |
| Frontend | 3000 | React application |
| PostgreSQL | 5432 | Database |
| Redis | 6379 | Caching |
| Kafka | 9092 | Message broker |
| Prometheus | 9090 | Metrics |
| Grafana | 3001 | Monitoring dashboard |

## 📚 API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Eureka Dashboard**: http://localhost:8761
- **Actuator Endpoints**: http://localhost:8080/actuator/health

## 🔐 Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin@hospital.com | admin123 |
| Doctor | doctor@hospital.com | doctor123 |
| Patient | patient@hospital.com | patient123 |
| Receptionist | reception@hospital.com | reception123 |

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🐳 Docker Commands

```bash
# Build all services
docker-compose build

# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f [service-name]

# Rebuild and restart
docker-compose up -d --build

# Check service health
curl http://localhost:8080/actuator/health
```

## 📊 Monitoring

### Prometheus & Grafana
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001 (admin/admin)

### Application Metrics
- Micrometer metrics available at `/actuator/metrics`
- Custom business metrics exposed
- JVM metrics monitoring

## 🔒 Security

- JWT-based authentication
- Role-based access control (RBAC)
- HTTPS enabled in production
- SQL injection prevention
- XSS protection
- CORS configuration
- Rate limiting

## 🗄️ Database

### Schema Management
- Flyway for database migrations
- Automatic schema creation
- Version-controlled migrations

### Connection Pool
- HikariCP connection pooling
- Optimized for high concurrency
- Connection timeout: 30s
- Maximum pool size: 20

## 📈 Performance Optimization

### Caching Strategy
- Redis for session caching
- Doctor availability caching
- Patient data caching
- 5-minute TTL for dynamic data

### Database Indexing
```sql
-- Patient service indexes
CREATE INDEX idx_patient_email ON patients(email);
CREATE INDEX idx_patient_phone ON patients(phone_number);
CREATE INDEX idx_patient_status ON patients(status);

-- Appointment service indexes  
CREATE INDEX idx_appointment_date ON appointments(appointment_date);
CREATE INDEX idx_appointment_status ON appointments(status);
CREATE INDEX idx_appointment_doctor ON appointments(doctor_id);
```

## 🚢 Deployment

### Kubernetes (Optional)
```bash
kubectl apply -f kubernetes/
```

### Production Deployment
1. Set environment variables
2. Configure SSL certificates
3. Set up database backups
4. Configure monitoring alerts
5. Enable logging aggregation

## 🐛 Troubleshooting

### Common Issues

1. **Port already in use**
   ```bash
   sudo lsof -i :8080
   sudo kill -9 <PID>
   ```

2. **Database connection issues**
   ```bash
   docker-compose restart postgres
   ```

3. **Kafka not starting**
   ```bash
   docker-compose restart kafka zookeeper
   ```

4. **Frontend not connecting to backend**
   - Check API Gateway is running
   - Verify CORS configuration
   - Check network connectivity

### Logs Location
- Docker logs: `docker-compose logs -f`
- Application logs: `logs/` directory
- System logs: `/var/log/` (Linux)

## 📁 Project Structure

```
hospital/
├── api-gateway/          # Spring Cloud Gateway
├── appointment-service/   # Appointment management
├── billing-service/      # Billing & payments
├── config-server/        # Centralized configuration
├── doctor-service/       # Doctor management
├── frontend/            # React frontend
├── notification-service/ # Email & notifications
├── patient-service/      # Patient management
├── service-registry/     # Eureka service registry
├── user-service/         # Authentication & users
├── docker-compose.yml    # Docker orchestration
├── pom.xml              # Parent Maven project
└── README.md            # This file
```

## 🔗 Dependencies

### Backend
- Spring Boot 3.x
- Spring Cloud 2023.x
- Spring Security
- Spring Data JPA
- Spring Kafka
- PostgreSQL Driver
- Redis
- JWT
- MapStruct
- Lombok

### Frontend
- React 19
- TypeScript
- Redux Toolkit
- React Router
- Tailwind CSS
- Axios
- React Hook Form
- Zod
- Recharts

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📞 Support

For support, please:
1. Check the troubleshooting section
2. Review existing issues
3. Create a new issue with detailed information

## 🎯 Features

- ✅ Patient registration and management
- ✅ Doctor scheduling and availability
- ✅ Appointment booking system
- ✅ Billing and payment processing
- ✅ Email notifications
- ✅ Role-based access control
- ✅ Real-time updates via Kafka
- ✅ Monitoring and metrics
- ✅ Docker containerization
- ✅ CI/CD ready

---

**Happy Coding! 🏥💻**