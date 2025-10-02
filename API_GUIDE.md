# API Usage Guide

## Overview

This guide provides comprehensive examples and usage patterns for the User Service IAM API. All endpoints are documented with OpenAPI 3.0 and accessible via Swagger UI at `/swagger-ui.html`.

## Base URL

```
http://localhost:8082/api/iam
```

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <jwt-token>
```

## API Endpoints by Category

### 🔐 User Authentication & Registration

#### Register New User

```bash
POST /api/iam/users/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "emailVerified": false,
  "firstName": "John",
  "lastName": "Doe",
  "enabled": true
}
```

**Response**: `200 OK` (Empty body)

#### User Login

```bash
POST /api/iam/users/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "SecurePass123!"
}
```

**Response**: `200 OK`
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "openid profile email"
}
```

#### Refresh Token

```bash
POST /api/iam/users/refresh-token
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### User Logout

```bash
POST /api/iam/users/logout
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response**: `204 No Content`

#### Reset Password

```bash
POST /api/iam/users/forgot-password
Content-Type: application/json

{
  "username": "johndoe",
  "newPassword": "NewSecurePass456!"
}
```

### 👤 User Profile Management

#### Get Current User Profile

```bash
GET /api/iam/users/me
Authorization: Bearer <jwt-token>
```

**Response**: `200 OK`
```json
{
  "id": "user-uuid",
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "emailVerified": true,
  "enabled": true
}
```

#### Update User Profile

```bash
PUT /api/iam/users/johndoe/profile
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "email": "john.newemail@example.com",
  "firstName": "John",
  "lastName": "Smith"
}
```

#### Get User Profile

```bash
GET /api/iam/users/johndoe/profile
Authorization: Bearer <jwt-token>
```

#### Change Password (Self-Service)

```bash
POST /api/iam/users/johndoe/change-password
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "oldPassword": "CurrentPass123!",
  "newPassword": "NewSecurePass456!"
}
```

#### Verify Email

```bash
POST /api/iam/users/johndoe/verify-email
Authorization: Bearer <jwt-token>
```

### 👨‍💼 Administrative Operations

#### Assign Role to User

```bash
POST /api/iam/admin/johndoe/roles/admin
Authorization: Bearer <admin-jwt-token>
```

#### Remove Role from User

```bash
DELETE /api/iam/admin/johndoe/roles/admin
Authorization: Bearer <admin-jwt-token>
```

#### Disable User Account

```bash
POST /api/iam/admin/johndoe/disable
Authorization: Bearer <admin-jwt-token>
```

#### Enable User Account

```bash
POST /api/iam/admin/johndoe/enable
Authorization: Bearer <admin-jwt-token>
```

#### Delete User Account

```bash
DELETE /api/iam/admin/johndoe
Authorization: Bearer <admin-jwt-token>
```

**Response**: `204 No Content`

#### Get User Details

```bash
GET /api/iam/admin/johndoe
Authorization: Bearer <admin-jwt-token>
```

#### List All Users (Paginated)

```bash
GET /api/iam/admin?first=0&max=20
Authorization: Bearer <admin-jwt-token>
```

**Response**: `200 OK`
```json
[
  {
    "id": "user-uuid-1",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "enabled": true,
    "emailVerified": true
  },
  {
    "id": "user-uuid-2",
    "username": "janedoe",
    "email": "jane.doe@example.com",
    "enabled": true,
    "emailVerified": false
  }
]
```

### 🏢 Client Management

#### Create OAuth2 Client

```bash
POST /api/iam/clients
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "clientId": "my-app-client",
  "name": "My Application",
  "description": "Client for my application",
  "enabled": true,
  "publicClient": false,
  "redirectUris": ["http://localhost:3000/callback"],
  "webOrigins": ["http://localhost:3000"]
}
```

#### Get Client Details

```bash
GET /api/iam/clients/{client-uuid}
Authorization: Bearer <admin-jwt-token>
```

#### List All Clients

```bash
GET /api/iam/clients
Authorization: Bearer <admin-jwt-token>
```

#### Update Client

```bash
PUT /api/iam/clients/{client-uuid}
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "name": "Updated Application Name",
  "description": "Updated description",
  "enabled": true
}
```

#### Delete Client

```bash
DELETE /api/iam/clients/{client-uuid}
Authorization: Bearer <admin-jwt-token>
```

#### Generate Client Secret

```bash
POST /api/iam/clients/{client-uuid}/secret
Authorization: Bearer <admin-jwt-token>
```

**Response**: `200 OK`
```json
{
  "secret": "new-generated-secret-key"
}
```

#### Get Client Secret

```bash
GET /api/iam/clients/{client-uuid}/secret
Authorization: Bearer <admin-jwt-token>
```

### 🎫 Token Operations

#### Client Credentials Authentication

```bash
POST /api/iam/token
Content-Type: application/json

{
  "clientId": "my-service-client",
  "clientSecret": "client-secret-key"
}
```

**Response**: `200 OK`
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "service-scope"
}
```

#### Token Introspection

```bash
POST /api/iam/users/introspect?token=<jwt-token>
Authorization: Bearer <jwt-token>
```

**Response**: `200 OK`
```json
{
  "active": true,
  "sub": "user-uuid",
  "username": "johndoe",
  "email": "john.doe@example.com",
  "exp": 1640995200,
  "iat": 1640991600,
  "scope": "openid profile email"
}
```

## Error Responses

### Common HTTP Status Codes

- `200 OK` - Successful operation
- `204 No Content` - Successful operation with no response body
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required or failed
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `500 Internal Server Error` - Server-side error

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid username or password",
  "path": "/api/iam/users/login"
}
```

## Usage Examples

### Complete User Registration Flow

```bash
# 1. Register user
curl -X POST http://localhost:8082/api/iam/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "SecurePass123!",
    "emailVerified": false,
    "firstName": "New",
    "lastName": "User",
    "enabled": true
  }'

# 2. Login user
curl -X POST http://localhost:8082/api/iam/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "SecurePass123!"
  }'

# 3. Use returned token for authenticated requests
curl -X GET http://localhost:8082/api/iam/users/me \
  -H "Authorization: Bearer <access-token>"
```

### Admin User Management

```bash
# 1. Get admin token (assume admin user exists)
ADMIN_TOKEN=$(curl -X POST http://localhost:8082/api/iam/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin"}' \
  | jq -r '.access_token')

# 2. List all users
curl -X GET "http://localhost:8082/api/iam/admin?first=0&max=10" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 3. Assign role to user
curl -X POST http://localhost:8082/api/iam/admin/newuser/roles/user \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 4. Disable user
curl -X POST http://localhost:8082/api/iam/admin/newuser/disable \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### Service-to-Service Authentication

```bash
# 1. Get service token using client credentials
SERVICE_TOKEN=$(curl -X POST http://localhost:8082/api/iam/token \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "service-client",
    "clientSecret": "service-secret"
  }' \
  | jq -r '.access_token')

# 2. Use service token for API calls
curl -X GET http://localhost:8082/api/iam/admin \
  -H "Authorization: Bearer $SERVICE_TOKEN"
```

## Best Practices

### Security
1. Always use HTTPS in production
2. Store tokens securely (not in localStorage for web apps)
3. Implement proper token refresh logic
4. Use appropriate token expiration times
5. Validate all input data

### Performance
1. Cache tokens until expiration
2. Use connection pooling for high-volume applications
3. Implement retry logic with exponential backoff
4. Monitor API rate limits

### Error Handling
1. Always check HTTP status codes
2. Implement proper error handling for all scenarios
3. Log errors for debugging
4. Provide meaningful error messages to users

### Development
1. Use the Swagger UI for API exploration
2. Test with different user roles and permissions
3. Validate API responses match expected schemas
4. Use proper HTTP methods for different operations