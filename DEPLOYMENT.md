# Deployment Guide

## Overview

This guide covers deployment strategies and configurations for the User Service in different environments.

## Prerequisites

### System Requirements
- Java 21 or higher
- Maven 3.6+
- Minimum 512MB RAM
- 1GB disk space

### External Dependencies
- **Keycloak Server**: Identity provider
- **Eureka Server**: Service discovery (optional)
- **Database**: For future persistence needs

## Environment Configuration

### Development Environment

#### Local Setup
```bash
# 1. Start Keycloak (Docker)
docker run -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev

# 2. Start Eureka Server (optional)
# Download and run Spring Cloud Eureka server

# 3. Build and run the application
mvn clean install
mvn spring-boot:run
```

#### Configuration (`application-dev.yml`)
```yaml
server:
  port: 8082

spring:
  profiles:
    active: dev
  application:
    name: user-service
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/demo-realm

keycloak:
  server-url: http://localhost:8080
  realm: demo-realm
  client-id: gateway-client
  client-secret: dev-client-secret
  admin-username: admin
  admin-password: admin

logging:
  level:
    root: DEBUG
    com.project.user: TRACE
```

### Staging Environment

#### Configuration (`application-staging.yml`)
```yaml
server:
  port: 8082

spring:
  profiles:
    active: staging
  application:
    name: user-service
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://keycloak-staging.company.com/realms/demo-realm

keycloak:
  server-url: https://keycloak-staging.company.com
  realm: demo-realm
  client-id: ${KEYCLOAK_CLIENT_ID}
  client-secret: ${KEYCLOAK_CLIENT_SECRET}
  admin-username: ${KEYCLOAK_ADMIN_USERNAME}
  admin-password: ${KEYCLOAK_ADMIN_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: https://eureka-staging.company.com/eureka/

logging:
  level:
    root: INFO
    com.project.user: DEBUG
```

### Production Environment

#### Configuration (`application-prod.yml`)
```yaml
server:
  port: 8082
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12

spring:
  profiles:
    active: prod
  application:
    name: user-service
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://keycloak.company.com/realms/production-realm

keycloak:
  server-url: https://keycloak.company.com
  realm: production-realm
  client-id: ${KEYCLOAK_CLIENT_ID}
  client-secret: ${KEYCLOAK_CLIENT_SECRET}
  admin-username: ${KEYCLOAK_ADMIN_USERNAME}
  admin-password: ${KEYCLOAK_ADMIN_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: https://eureka.company.com/eureka/

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    root: WARN
    com.project.user: INFO
  file:
    name: /var/log/user-service/application.log
```

## Deployment Methods

### 1. Traditional JAR Deployment

#### Build Application
```bash
mvn clean package -Pprod
```

#### Deploy JAR
```bash
# Copy JAR to server
scp target/user-0.0.1-SNAPSHOT.jar user@server:/opt/user-service/

# Create systemd service
sudo tee /etc/systemd/system/user-service.service > /dev/null <<EOF
[Unit]
Description=User Service
After=network.target

[Service]
Type=simple
User=user-service
WorkingDirectory=/opt/user-service
ExecStart=/usr/bin/java -jar user-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

Environment=SPRING_PROFILES_ACTIVE=prod
Environment=KEYCLOAK_CLIENT_ID=production-client
Environment=KEYCLOAK_CLIENT_SECRET=prod-secret
Environment=KEYCLOAK_ADMIN_USERNAME=admin
Environment=KEYCLOAK_ADMIN_PASSWORD=secure-admin-password

[Install]
WantedBy=multi-user.target
EOF

# Start service
sudo systemctl daemon-reload
sudo systemctl enable user-service
sudo systemctl start user-service
```

### 2. Docker Deployment

#### Dockerfile
```dockerfile
FROM openjdk:21-jre-slim

# Create application user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/user-0.0.1-SNAPSHOT.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Build and Run Docker Container
```bash
# Build image
docker build -t user-service:latest .

# Run container
docker run -d \
  --name user-service \
  -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e KEYCLOAK_CLIENT_ID=production-client \
  -e KEYCLOAK_CLIENT_SECRET=prod-secret \
  -e KEYCLOAK_ADMIN_USERNAME=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=secure-admin-password \
  user-service:latest
```

#### Docker Compose
```yaml
version: '3.8'

services:
  user-service:
    build: .
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - KEYCLOAK_CLIENT_ID=production-client
      - KEYCLOAK_CLIENT_SECRET=prod-secret
      - KEYCLOAK_ADMIN_USERNAME=admin
      - KEYCLOAK_ADMIN_PASSWORD=secure-admin-password
    depends_on:
      - keycloak
    networks:
      - app-network

  keycloak:
    image: quay.io/keycloak/keycloak:latest
    ports:
      - "8080:8080"
    environment:
      - KEYCLOAK_ADMIN=admin
      - KEYCLOAK_ADMIN_PASSWORD=admin
    command: start-dev
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
```

### 3. Kubernetes Deployment

#### Deployment YAML
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
  labels:
    app: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: user-service:latest
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: KEYCLOAK_CLIENT_ID
          valueFrom:
            secretKeyRef:
              name: keycloak-secret
              key: client-id
        - name: KEYCLOAK_CLIENT_SECRET
          valueFrom:
            secretKeyRef:
              name: keycloak-secret
              key: client-secret
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8082
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8082
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8082
  type: ClusterIP
---
apiVersion: v1
kind: Secret
metadata:
  name: keycloak-secret
type: Opaque
data:
  client-id: <base64-encoded-client-id>
  client-secret: <base64-encoded-client-secret>
```

#### Deploy to Kubernetes
```bash
# Apply deployment
kubectl apply -f k8s-deployment.yaml

# Check deployment status
kubectl get deployments
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/user-service
```

## Environment Variables

### Required Variables
```bash
# Keycloak Configuration
KEYCLOAK_CLIENT_ID=your-client-id
KEYCLOAK_CLIENT_SECRET=your-client-secret
KEYCLOAK_ADMIN_USERNAME=admin-username
KEYCLOAK_ADMIN_PASSWORD=admin-password

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# SSL Configuration (if enabled)
SSL_KEYSTORE_PASSWORD=keystore-password
```

### Optional Variables
```bash
# Server Configuration
SERVER_PORT=8082

# Eureka Configuration
EUREKA_URL=http://eureka-server:8761/eureka/

# Logging Configuration
LOGGING_LEVEL_ROOT=INFO
LOGGING_FILE_NAME=/var/log/user-service/application.log

# JVM Options
JAVA_OPTS="-Xmx1g -Xms512m"
```

## Monitoring and Health Checks

### Health Endpoints
```bash
# Application health
curl http://localhost:8082/actuator/health

# Application info
curl http://localhost:8082/actuator/info

# Metrics
curl http://localhost:8082/actuator/metrics
```

### Monitoring Setup

#### Prometheus Configuration
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'user-service'
    static_configs:
      - targets: ['user-service:8082']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
```

#### Grafana Dashboard
- Import Spring Boot dashboard
- Configure data source to Prometheus
- Monitor key metrics:
  - HTTP request rates
  - Response times
  - JVM memory usage
  - Active sessions

## Security Considerations

### Production Security Checklist
- [ ] Enable HTTPS/TLS
- [ ] Use strong passwords and secrets
- [ ] Implement proper firewall rules
- [ ] Enable security headers
- [ ] Configure CORS properly
- [ ] Use secure session management
- [ ] Implement rate limiting
- [ ] Enable audit logging
- [ ] Regular security updates
- [ ] Vulnerability scanning

### Network Security
```bash
# Firewall rules (iptables example)
# Allow HTTP/HTTPS traffic
iptables -A INPUT -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -p tcp --dport 443 -j ACCEPT

# Allow application port (internal only)
iptables -A INPUT -p tcp --dport 8082 -s 10.0.0.0/8 -j ACCEPT

# Block all other traffic
iptables -A INPUT -j DROP
```

## Backup and Recovery

### Configuration Backup
```bash
# Backup configuration files
tar -czf user-service-config-$(date +%Y%m%d).tar.gz \
  /opt/user-service/application*.yml \
  /etc/systemd/system/user-service.service
```

### Application Backup
```bash
# Backup application JAR and logs
tar -czf user-service-backup-$(date +%Y%m%d).tar.gz \
  /opt/user-service/ \
  /var/log/user-service/
```

### Recovery Procedure
1. Stop the service
2. Restore configuration files
3. Restore application files
4. Start the service
5. Verify functionality

## Troubleshooting

### Common Issues

#### Service Won't Start
```bash
# Check logs
journalctl -u user-service -f

# Check Java version
java -version

# Check port availability
netstat -tlnp | grep 8082
```

#### Keycloak Connection Issues
```bash
# Test Keycloak connectivity
curl -I http://keycloak-server:8080/realms/demo-realm

# Check DNS resolution
nslookup keycloak-server

# Verify certificates (HTTPS)
openssl s_client -connect keycloak-server:443
```

#### Memory Issues
```bash
# Monitor memory usage
free -h
ps aux | grep java

# Adjust JVM settings
export JAVA_OPTS="-Xmx2g -Xms1g"
```

### Performance Tuning

#### JVM Tuning
```bash
# Production JVM options
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### Connection Pool Tuning
```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

## Scaling Strategies

### Horizontal Scaling
- Deploy multiple instances
- Use load balancer (Nginx, HAProxy)
- Configure session affinity if needed
- Monitor resource usage

### Vertical Scaling
- Increase memory allocation
- Add more CPU cores
- Optimize JVM settings
- Monitor performance metrics

### Auto-scaling (Kubernetes)
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: user-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: user-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```