# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Sistema de Acceso Unificado del Gobierno de Tabasco** - A complete microservices architecture for government employee authentication and user management built with Spring Boot 3.5.5 and Spring Cloud 2025.0.0.

## Microservices Architecture

### Services

1. **discovery-service** (Port 8761) - Eureka Server
   - Service discovery and registration
   - Health monitoring of all microservices

2. **config-server** (Port 8888) - Spring Cloud Config Server
   - Centralized configuration management
   - Profile-based configurations (dev/prod)
   - Git integration for configuration versioning

3. **gateway-service** (Port 8080) - Spring Cloud Gateway
   - API Gateway and routing
   - OAuth2 JWT token validation
   - Load balancing and circuit breaking

4. **auth-service** (Port 8081) - Authentication Service
   - Keycloak integration for authentication
   - JWT token generation and validation
   - User authentication and authorization
   - Profile management endpoints

5. **technical-concept-service** (Port 8083) - Technical Concept Management
   - CUBS technical concept CRUD operations
   - Area and chapter-based organization
   - Role-based access control by area
   - Audit trail for all changes

6. **validation-service** (Port 8085) - Business Rules Validation
   - Concept uniqueness validation by area
   - Area-chapter relationship validation
   - Format and specification validation
   - Comprehensive business rules engine

7. **user-management-service** (Port 8082) - User Management Service
   - Complete user lifecycle management
   - Role and permission administration
   - Organizational hierarchy management
   - User profile synchronization

### Technology Stack

- **Java**: 17
- **Spring Boot**: 3.5.5
- **Spring Cloud**: 2025.0.0
- **Database**: PostgreSQL (all environments)
- **Authentication**: Keycloak with OAuth2/JWT
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Testing**: JUnit 5 with Testcontainers

## Common Development Commands

### Multi-Module Build Commands
```bash
# Build all modules
./mvnw clean compile

# Package all services
./mvnw clean package

# Run specific service
cd [service-name]
../mvnw spring-boot:run

# Run with specific profile
../mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Service Startup Order
```bash
# 1. Start Discovery Service (required first)
cd discovery-service && ../mvnw spring-boot:run

# 2. Start Config Server (required second)  
cd config-server && ../mvnw spring-boot:run

# 3. Start other services in any order
cd gateway-service && ../mvnw spring-boot:run
cd auth-service && ../mvnw spring-boot:run
cd technical-concept-service && ../mvnw spring-boot:run
cd validation-service && ../mvnw spring-boot:run
cd user-management-service && ../mvnw spring-boot:run
```

### Testing
```bash
# Run all tests
./mvnw test

# Run tests for specific service
cd [service-name] && ../mvnw test

# Run integration tests with Testcontainers
./mvnw verify
```

## Configuration Management

### Environment Profiles
- **dev**: Development environment (localhost, debug logging)
- **prod**: Production environment (production URLs, optimized settings)

### Configuration Files Location
```
config-server/src/main/resources/config-repo/
├── application.yml                    # Global configuration
├── discovery-service.yml             # Eureka server config
├── gateway-service.yml               # Gateway routes and security
├── auth-service-dev.yml              # Auth service development
├── auth-service-prod.yml             # Auth service production  
├── user-management-service-dev.yml   # User management development
└── user-management-service-prod.yml  # User management production
```

## Database Setup

### Development Environment
```sql
-- Create databases
CREATE DATABASE acceso_tabasco_dev;

-- Create users
CREATE USER auth_dev WITH PASSWORD 'dev_password';
CREATE USER user_mgmt_dev WITH PASSWORD 'dev_password';

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE acceso_tabasco_dev TO auth_dev;
GRANT ALL PRIVILEGES ON DATABASE acceso_tabasco_dev TO user_mgmt_dev;
```

### Government Employee Data Structure
The system validates against government payroll with these key fields:
- **CURP**: Mexican citizen ID (18 characters)
- **Employee Status**: ACTIVO, INACTIVO, SUSPENDIDO
- **Dependency**: Government department
- **Position**: Job title
- **Manager Hierarchy**: Organizational structure

## API Endpoints

### Gateway Routes (Port 8080)
- `/auth/**` → auth-service (Port 8081)
- `/users/**` → user-management-service (Port 8082)
- `/actuator/**` → Service health endpoints

### Authentication API (Port 8081)
- `POST /api/auth/login` - Employee login with CURP
- `POST /api/auth/validate` - Token validation
- `GET /api/auth/profile` - User profile info
- `POST /api/auth/refresh` - Token refresh

### User Management API (Port 8082)
- `GET /api/users` - List users (paginated)
- `POST /api/users` - Create user
- `PUT /api/users/{curp}` - Update user
- `DELETE /api/users/{curp}` - Deactivate user
- `GET /api/roles` - List roles
- `POST /api/roles` - Create role
- `POST /api/users/{curp}/roles` - Assign roles

## Security Model

### JWT Token Structure
```json
{
  "sub": "AAAA800101HTABCD01",
  "preferred_username": "AAAA800101HTABCD01", 
  "authorities": ["USER_READ", "USER_CREATE"],
  "dependencia": "CGMAIG",
  "puesto": "Coordinador de TI"
}
```

### Permission System
- **Resource-based**: Permissions tied to specific resources
- **Action-based**: CREATE, READ, UPDATE, DELETE, ADMIN
- **System-scoped**: Permissions can be system-specific
- **Role-inherited**: Users get permissions through roles

### Authority Examples
- `USER_READ` - Read user information
- `USER_CREATE` - Create new users  
- `ROLE_ADMIN` - Administer roles
- `SYSTEM_ADMIN` - Full system administration

## Development Notes

### Key Patterns
- **Config-First**: All configuration in config-server
- **JWT Propagation**: Tokens passed through gateway to services
- **Audit Trail**: All user management actions logged
- **Government Validation**: CURP format and payroll status validation

### Testing Strategy
- **Unit Tests**: Service layer testing
- **Integration Tests**: Full Spring context with Testcontainers
- **Security Tests**: JWT token validation and authorization
- **Database Tests**: PostgreSQL with test data

### Deployment Considerations
- Services register with Eureka for discovery
- Config server must start before other services
- PostgreSQL required for all services
- Keycloak integration for production authentication
- Load balancer handles SSL termination in production

## Troubleshooting

### Common Issues
1. **Service Registration**: Check Eureka dashboard at http://localhost:8761
2. **Configuration Loading**: Verify config-server logs for property resolution
3. **Database Connection**: Ensure PostgreSQL is running and accessible
4. **JWT Validation**: Check Keycloak server availability and configuration
5. **CORS Issues**: Gateway handles CORS for frontend applications

### Health Checks
- Discovery: http://localhost:8761
- Config Server: http://localhost:8888/actuator/health
- Gateway: http://localhost:8080/actuator/health
- Auth Service: http://localhost:8081/actuator/health  
- User Management: http://localhost:8082/actuator/health