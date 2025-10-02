# User Service - Identity & Access Management (IAM)

A comprehensive Spring Boot microservice for user identity and access management, built with Keycloak integration and following clean architecture principles.

## 🚀 Features

- **User Management**: Registration, login, logout, password management
- **Admin Operations**: User administration, role management, account control
- **Client Management**: OAuth2 client registration and management
- **Token Operations**: JWT token refresh, introspection, client credentials
- **Profile Management**: User profile updates and email verification
- **Security**: OAuth2 Resource Server with Keycloak integration
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Service Discovery**: Eureka client integration
- **Clean Architecture**: Domain-driven design with clear separation of concerns

## 🏗️ Architecture

The project follows **Clean Architecture** principles with clear separation between layers:

```
src/main/java/com/project/user/
├── application/          # Application layer (REST controllers, DTOs)
│   ├── rest/            # REST controllers
│   ├── model/           # Request/Response DTOs
│   ├── security/        # Security configuration
│   └── swagger/         # API documentation config
├── domain/              # Domain layer (business logic)
│   └── iam/            # Identity & Access Management domain
│       ├── users/      # User-related services
│       └── clients/    # Client-related services
└── infrastructure/      # Infrastructure layer (external integrations)
    ├── config/         # Configuration classes
    └── iam/           # Keycloak integrations
```

## 🛠️ Technology Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.3.0** - Application framework
- **Spring Security** - OAuth2 Resource Server
- **Spring Cloud** - Netflix Eureka for service discovery
- **Keycloak** - Identity and access management
- **OpenAPI 3** - API documentation
- **Maven** - Build tool
- **Lombok** - Code generation

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Keycloak server running on `localhost:8080`
- Eureka server running on `localhost:8761` (optional)

## ⚙️ Configuration

### Application Configuration (`application.yml`)

```yaml
server:
  port: 8082

spring:
  application:
    name: user-service
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/demo-realm

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

keycloak:
  server-url: http://localhost:8080
  realm: demo-realm
  client-id: gateway-client
  client-secret: leuDC81GnBf7githks8Swl5IaxWT2Y66
  admin-username: admin
  admin-password: admin
```

### Keycloak Setup

1. Start Keycloak server
2. Create realm: `demo-realm`
3. Create client: `gateway-client`
4. Configure client settings and obtain client secret
5. Create admin user with appropriate roles

## 🚀 Getting Started

### 1. Clone and Build

```bash
git clone <repository-url>
cd user-service
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/user-0.0.1-SNAPSHOT.jar
```

### 3. Access the Application

- **Application**: http://localhost:8082
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8082/v3/api-docs

## 📚 API Documentation

The service provides comprehensive REST APIs organized into several controllers:

### 🔐 User IAM Controller (`/api/iam/users`)
- `POST /register` - User registration
- `POST /login` - User authentication
- `POST /refresh-token` - Token refresh
- `POST /logout` - User logout
- `POST /forgot-password` - Password reset
- `POST /client-credentials` - Client credentials token

### 👤 Extended User Controller (`/api/iam/users`)
- `GET /me` - Get current user profile
- `PUT /{username}/profile` - Update user profile
- `GET /{username}/profile` - Get user profile
- `POST /{username}/change-password` - Change password
- `POST /introspect` - Token introspection
- `POST /{username}/verify-email` - Email verification

### 👨‍💼 Admin Controller (`/api/iam/admin`)
- `POST /{username}/roles/{role}` - Assign role
- `DELETE /{username}/roles/{role}` - Remove role
- `POST /{username}/disable` - Disable user
- `POST /{username}/enable` - Enable user
- `DELETE /{username}` - Delete user
- `GET /{username}` - Get user details
- `GET /` - List all users

### 🏢 Client Management (`/api/iam/clients`)
- `POST /` - Create client
- `PUT /{id}` - Update client
- `GET /{id}` - Get client
- `GET /` - List clients
- `DELETE /{id}` - Delete client
- `POST /{id}/secret` - Generate client secret
- `GET /{id}/secret` - Get client secret

### 🎫 Token Controller (`/api/iam`)
- `POST /token` - Service authentication with client credentials

## 🔒 Security

### Authentication & Authorization
- OAuth2 Resource Server with JWT tokens
- Keycloak as identity provider
- Role-based access control
- Secure endpoints with proper authentication

### Public Endpoints
- User registration and login
- Password reset
- Token refresh
- Swagger documentation

### Protected Endpoints
- All admin operations require authentication
- User profile operations require valid JWT
- Client management requires appropriate roles

## 🧪 Testing

Run tests with:

```bash
mvn test
```

The project includes:
- Unit tests for business logic
- Integration tests for REST endpoints
- Spring Boot test configuration

## 📦 Deployment

### Docker (Future Enhancement)
```dockerfile
FROM openjdk:21-jre-slim
COPY target/user-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `KEYCLOAK_SERVER_URL` - Keycloak server URL
- `KEYCLOAK_REALM` - Keycloak realm name
- `KEYCLOAK_CLIENT_ID` - OAuth2 client ID
- `KEYCLOAK_CLIENT_SECRET` - OAuth2 client secret
- `EUREKA_URL` - Eureka server URL

## 🔧 Development

### Code Style
- Follow Java naming conventions
- Use Lombok for boilerplate code reduction
- Implement proper error handling
- Write comprehensive tests

### Adding New Features
1. Create domain services in `domain/iam/`
2. Add REST controllers in `application/rest/`
3. Define DTOs in `application/model/`
4. Configure security in `SecurityConfig`
5. Update Swagger documentation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Check the API documentation at `/swagger-ui.html`
- Review the application logs
- Verify Keycloak configuration
- Ensure all dependencies are properly configured