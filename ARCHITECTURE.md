# Architecture Documentation

## Overview

The User Service follows **Clean Architecture** principles, ensuring separation of concerns and maintainability. The architecture is designed to be testable, independent of frameworks, and focused on business logic.

## Layer Structure

### 1. Application Layer (`application/`)

**Responsibility**: Handles external communication and orchestrates domain services.

#### Components:
- **REST Controllers**: Handle HTTP requests and responses
- **DTOs/Models**: Data transfer objects for API communication
- **Security Configuration**: OAuth2 and JWT security setup
- **Swagger Configuration**: API documentation setup

#### Key Files:
- `UserIamController.java` - Core user operations
- `AdminIamController.java` - Administrative operations
- `ClientRestController.java` - Client management
- `SecurityConfig.java` - Security configuration

### 2. Domain Layer (`domain/`)

**Responsibility**: Contains business logic and domain rules.

#### Structure:
```
domain/iam/
├── users/              # User domain services
│   ├── UserRegistrationService
│   ├── UserLoginService
│   ├── UserPasswordService
│   ├── UserProfileService
│   └── ...
├── clients/            # Client domain services
│   ├── ClientRegistrationService
│   ├── ClientAuthService
│   └── ...
└── TokenIntrospectionService
```

#### Characteristics:
- Pure business logic
- No external dependencies
- Framework-independent
- Highly testable

### 3. Infrastructure Layer (`infrastructure/`)

**Responsibility**: Handles external integrations and technical concerns.

#### Components:
- **Configuration**: External service configurations
- **IAM Integration**: Keycloak client implementations
- **Cache**: Caching mechanisms (future)
- **Health Checks**: Application health monitoring (future)

## Design Patterns

### 1. Dependency Injection
- Constructor injection using `@RequiredArgsConstructor`
- Interface-based dependencies
- Spring IoC container management

### 2. Service Layer Pattern
- Domain services encapsulate business logic
- Single responsibility principle
- Clear service boundaries

### 3. DTO Pattern
- Request/Response objects for API communication
- Java records for immutable data structures
- Clear API contracts

### 4. Configuration Pattern
- Externalized configuration using `@ConfigurationProperties`
- Environment-specific settings
- Type-safe configuration binding

## Security Architecture

### OAuth2 Resource Server
```
Client Request → JWT Token → Spring Security → Controller → Domain Service
```

### Security Flow:
1. Client obtains JWT token from Keycloak
2. Token included in Authorization header
3. Spring Security validates token
4. Request processed if valid
5. Response returned to client

### Protected Resources:
- Admin operations require authentication
- User profile operations require valid JWT
- Public endpoints for registration/login

## Integration Architecture

### Keycloak Integration
```
User Service ←→ Keycloak Admin API
     ↓
Domain Services ←→ Infrastructure Layer ←→ Keycloak REST API
```

### Service Discovery
```
User Service → Eureka Client → Eureka Server
```

## Data Flow

### User Registration Flow:
```
REST Controller → UserRegistrationService → Keycloak API
```

### Authentication Flow:
```
REST Controller → UserLoginService → Keycloak Token Endpoint → JWT Response
```

### Admin Operations Flow:
```
Admin Controller → Domain Service → Keycloak Admin API
```

## Error Handling Strategy

### Layers:
1. **Controller Level**: HTTP status codes and error responses
2. **Service Level**: Business logic validation
3. **Infrastructure Level**: External service error handling

### Approach:
- Spring Boot default error handling
- Custom exception classes (future enhancement)
- Proper HTTP status codes
- Meaningful error messages

## Testing Strategy

### Unit Tests:
- Domain services testing
- Mock external dependencies
- Business logic validation

### Integration Tests:
- REST endpoint testing
- Spring Boot test slices
- Test containers for external services (future)

### Test Structure:
```
src/test/java/
└── com/project/user/
    ├── application/rest/    # Controller tests
    ├── domain/iam/         # Service tests
    └── infrastructure/     # Integration tests
```

## Performance Considerations

### Current:
- Stateless design for scalability
- JWT tokens for distributed authentication
- Service discovery for load balancing

### Future Enhancements:
- Caching for frequently accessed data
- Connection pooling for Keycloak API
- Async processing for non-critical operations
- Database integration for audit logs

## Scalability Design

### Horizontal Scaling:
- Stateless application design
- External session management (JWT)
- Service discovery integration

### Vertical Scaling:
- Efficient resource utilization
- Minimal memory footprint
- Fast startup times

## Monitoring and Observability

### Current:
- Spring Boot Actuator endpoints
- Application logging
- Eureka health checks

### Future:
- Metrics collection (Micrometer)
- Distributed tracing (Sleuth/Zipkin)
- Custom health indicators
- Performance monitoring

## Configuration Management

### Externalized Configuration:
- `application.yml` for default settings
- Environment variables for deployment
- Spring profiles for different environments

### Configuration Binding:
- Type-safe configuration with `@ConfigurationProperties`
- Validation of configuration values
- Default values and fallbacks

## Security Best Practices

### Implementation:
- OAuth2 standard compliance
- JWT token validation
- Secure communication with Keycloak
- Role-based access control

### Recommendations:
- HTTPS in production
- Token expiration policies
- Rate limiting (future)
- Audit logging (future)

## Future Architecture Enhancements

### Database Integration:
- User profile persistence
- Audit trail storage
- Configuration management

### Event-Driven Architecture:
- User registration events
- Password change notifications
- Admin action logging

### API Gateway Integration:
- Centralized routing
- Rate limiting
- Request/response transformation

### Microservices Communication:
- Service mesh integration
- Circuit breaker pattern
- Distributed configuration