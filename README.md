# Itinerarly Backend

A production-ready Spring Boot REST API backend service for travel itinerary management with comprehensive OAuth2 authentication, JWT token management, containerized deployment, and robust testing framework.

##  Features

- **Multi-Provider OAuth2 Authentication**: Google, GitHub, Facebook, and Twitter support with automatic user registration
- **JWT Token Authentication**: Secure token-based authentication with HTTP-only cookies
- **Daily Token Rate Limiting**: User-based daily API usage limits with automatic midnight refresh
- **User Profile Management**: Complete user profile system with provider-specific data and avatar URLs
- **Production-Ready Containerization**: Multi-stage Docker builds with health checks and security best practices
- **Comprehensive Testing Suite**: Unit, integration, and repository tests with H2 in-memory database
- **Security Best Practices**: Non-root containers, CORS configuration, secure secrets management
- **Monitoring & Health Checks**: Built-in health endpoints and container monitoring
- **Multi-Environment Support**: Separate configurations for development, testing, and production
- **Automatic Token Refresh**: Scheduled daily token refresh service using Spring's @Scheduled annotation

##  Tech Stack

- **Framework**: Spring Boot 3.x with Java 21
- **Security**: Spring Security OAuth2 with JWT (jjwt library)
- **Database**: MySQL 8.0 (production), H2 (testing)
- **Containerization**: Docker & Docker Compose with multi-stage builds
- **Authentication**: JWT with HTTP-only cookies and secure token validation
- **Documentation**: Swagger/OpenAPI 3.0 with custom configuration
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven 3.9+ with dependency management
- **Scheduling**: Spring Task Scheduling for automated token refresh

##  Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker & Docker Compose
- OAuth2 credentials for supported providers
- MySQL 8.0 (for production) or H2 (for testing)

##  Environment Setup

### 1. Clone and Setup Environment

```bash
git clone <repository-url>
cd itinerarly-BE

# Copy environment template
cp .env.template .env
```

### 2. Configure Environment Variables

Edit `.env` file with your credentials:

```env
# Database Configuration
DB_NAME=itinerarly
DB_USERNAME=root
DB_PASSWORD=your_secure_database_password
DB_APP_USER=appuser
DB_APP_PASSWORD=your_app_user_password

# JWT Configuration
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_here

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
FACEBOOK_CLIENT_ID=your_facebook_client_id
FACEBOOK_CLIENT_SECRET=your_facebook_client_secret
TWITTER_CLIENT_ID=your_twitter_client_id
TWITTER_CLIENT_SECRET=your_twitter_client_secret

# Application Settings
DAILY_TOKEN_LIMIT=6
BACKEND_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

### 3. OAuth2 Provider Setup

#### Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth2 credentials
5. Add redirect URI: `http://localhost:8080/login/oauth2/code/google`
6. Configure authorized domains

#### GitHub OAuth2
1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create new OAuth App
3. Set Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Note: GitHub provides user data including `login`, `avatar_url`, and `id`

#### Facebook OAuth2
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create new app
3. Add Facebook Login product
4. Set Valid OAuth Redirect URI: `http://localhost:8080/login/oauth2/code/facebook`
5. Configure scopes: `email`, `public_profile`

#### Twitter OAuth2
1. Go to [Twitter Developer Portal](https://developer.twitter.com/)
2. Create new app with OAuth 2.0 enabled
3. Set Callback URI: `http://localhost:8080/login/oauth2/code/twitter`
4. Configure scopes: `tweet.read`, `users.read`
5. Note: Uses Twitter API v2 for user information

##  Docker Deployment

### Production Deployment

```bash
# Quick production deployment with validation
./scripts/deploy-prod.sh

# Manual production deployment
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d

# Check service health
docker-compose ps
docker-compose logs -f backend
```

### Development Deployment

```bash
# Development with hot reload and debug port
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build

# Local development without Docker
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker Architecture

- **Multi-stage builds**: Separate build and runtime environments for optimization
- **Health checks**: Automated health monitoring with restart policies
- **Non-root containers**: Enhanced security with dedicated application user
- **Resource limits**: CPU and memory constraints for production stability
- **Persistent volumes**: Database data persistence with backup-ready structure
- **Custom networks**: Service isolation and secure communication
- **Environment-specific configs**: Separate compose files for dev/prod environments

##  Testing

### Run All Tests

```bash
# Unit and integration tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

### Test Structure

```
src/test/java/
├── controller/          # REST API endpoint tests
│   └── AuthControllerTest.java
├── service/            # Business logic tests
│   └── TokenRefreshServiceTest.java
├── repository/         # Data layer tests
│   └── UserRepositoryTest.java
├── model/              # Entity model tests
│   └── UserModelTest.java
└── integration/        # End-to-end integration tests
    └── OAuth2IntegrationTest.java
```

### Testing Configuration

- **H2 Database**: In-memory database for isolated testing
- **Test Profiles**: Separate `application-test.properties` configuration
- **Mock Security**: OAuth2 authentication mocking for unit tests
- **TestContainers**: Optional Docker-based integration testing

##  API Documentation

### Swagger UI
- **Development**: http://localhost:8080/swagger-ui/index.html
- **Production**: http://your-domain.com/swagger-ui/index.html

### Authentication Endpoints

#### OAuth2 Login
- `GET /oauth2/authorization/google` - Google OAuth2 login
- `GET /oauth2/authorization/github` - GitHub OAuth2 login
- `GET /oauth2/authorization/facebook` - Facebook OAuth2 login
- `GET /oauth2/authorization/twitter` - Twitter OAuth2 login

#### Authentication Management
- `GET /api/v1/start` - Health check endpoint (public)
- `POST /api/v1/logout` - User logout with cookie invalidation
- `GET /api/v1/validate` - JWT token validation
- `GET /` - Basic health check (public)

### Token Management Endpoints

#### Daily Token System
- `GET /api/v1/tokens/remaining` - Get user's remaining daily tokens
- `POST /api/v1/tokens/consume` - Consume one token (decrements count)

**Token Features:**
- Daily limit of 6 tokens per user (configurable)
- Automatic refresh at midnight (IST timezone)
- Token consumption tracking per user
- Scheduled service for automatic token refresh

### User Management

#### Profile Management
- `GET /api/v1/users/profile` - Get authenticated user profile
- `PUT /api/v1/users/profile` - Update user profile information

**User Data Structure:**
```json
{
  "id": "user_id",
  "oauthId": "provider_user_id",
  "email": "user@example.com",
  "name": "User Name",
  "username": "username",
  "avatarUrl": "https://avatar-url.com/image.jpg",
  "provider": "google|github|facebook|twitter",
  "dailyTokens": 6,
  "lastTokenRefresh": "2025-08-17",
  "loginTime": "2025-08-17T10:30:00Z[Asia/Kolkata]"
}
```

##  Security Implementation

### JWT Token Security
- **HTTP-Only Cookies**: Secure token storage preventing XSS attacks
- **Token Validation**: Comprehensive JWT signature and expiration validation
- **Secure Headers**: Proper cookie configuration with security flags
- **Token Extraction**: Support for both Authorization header and cookie-based tokens

### OAuth2 Security Flow
1. User initiates OAuth2 login with provider
2. Provider redirects to application with authorization code
3. Application exchanges code for user information
4. User data is processed and stored in database
5. JWT token is generated and set as HTTP-only cookie
6. User is redirected to frontend application

### CORS Configuration
```java
// Configured origins and methods
.allowedOrigins("http://localhost:3000")
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
.allowCredentials(true)
.exposedHeaders("Set-Cookie")
```

### Container Security
- Non-root user execution in containers
- Minimal Alpine-based runtime images
- Security scanning compatible base images
- Proper file permissions and ownership

##  Monitoring & Health Checks

### Application Health Endpoints
- `GET /api/v1/start` - Custom application health check
- `GET /actuator/health` - Spring Boot Actuator health information
- `GET /actuator/metrics` - Application performance metrics

### Docker Health Monitoring
- **Backend Container**: HTTP health check on `/api/v1/start` endpoint
- **Database Container**: MySQL ping command validation
- **Automatic Recovery**: Container restart on health check failure
- **Health Dependencies**: Backend waits for database health before starting

### Logging Configuration
- **Security Logging**: OAuth2 authentication flow tracing
- **Error Tracking**: Comprehensive error logging with stack traces
- **Performance Monitoring**: SQL query logging (development only)
- **Container Logs**: Structured logging for Docker environments

##  Production Deployment

### System Requirements
- **CPU**: Minimum 1 core, recommended 2+ cores
- **Memory**: Minimum 1GB RAM, recommended 2GB+
- **Storage**: SSD recommended, 10GB+ available space
- **Network**: HTTPS/SSL configuration for OAuth2 security

### Production Configuration
- **Database Connection Pooling**: HikariCP with optimized settings
- **JVM Optimization**: Container-aware memory settings
- **Security Headers**: HTTPS-only cookie configuration
- **Resource Limits**: Container memory and CPU constraints
- **Health Check Intervals**: Production-optimized monitoring

### Deployment Checklist
- [ ] Environment variables configured and validated
- [ ] OAuth2 credentials setup for production domains
- [ ] Database backup and recovery strategy implemented
- [ ] HTTPS/SSL certificates configured and tested
- [ ] Monitoring and alerting systems in place
- [ ] Resource limits and scaling policies defined
- [ ] Security audit completed
- [ ] Performance testing validated

## 🔧 Configuration Files

### Application Properties Structure
```
src/main/resources/
├── application.properties          # Base configuration
├── application-dev.properties      # Development settings
├── application-prod.properties     # Production settings
└── application-test.properties     # Testing configuration
```

### Key Configuration Properties
```properties
# JWT Configuration
JWT_SECRET=${JWT-Secret}

# Token Management
app.daily-token-limit=6

# OAuth2 Providers
spring.security.oauth2.client.registration.google.client-id=${google-client-id}
spring.security.oauth2.client.registration.github.client-id=${github-client-id}
spring.security.oauth2.client.registration.facebook.client-id=${facebook-client-id}
spring.security.oauth2.client.registration.twitter.client-id=${twitter-client-id}

# Database Configuration
spring.datasource.url=${db-url}
spring.jpa.hibernate.ddl-auto=update
```

## 🔧 Troubleshooting

### Common Issues and Solutions

#### OAuth2 Authentication Issues
```bash
# Check OAuth2 credentials
echo $GOOGLE_CLIENT_ID
echo $GITHUB_CLIENT_ID

# Verify provider configuration in logs
docker-compose logs -f backend | grep OAuth2

# Test OAuth2 endpoints
curl -X GET http://localhost:8080/oauth2/authorization/google
```

#### JWT Token Problems
```bash
# Verify JWT secret configuration
echo $JWT_SECRET

# Check token validation in logs
docker-compose logs -f backend | grep JWT

# Test token endpoint
curl -X GET http://localhost:8080/api/v1/validate \
  -H "Cookie: auth-token=YOUR_TOKEN"
```

#### Database Connection Issues
```bash
# Check database container health
docker-compose ps
docker-compose logs -f db

# Verify database connectivity
docker exec -it itinerarly-db mysql -u root -p

# Check connection pool status
docker-compose logs -f backend | grep HikariPool
```

#### Token Limit Issues
```bash
# Check remaining tokens
curl -X GET http://localhost:8080/api/v1/tokens/remaining \
  -H "Cookie: auth-token=YOUR_TOKEN"

# Force token refresh (for testing)
curl -X POST http://localhost:8080/api/v1/tokens/consume \
  -H "Cookie: auth-token=YOUR_TOKEN"
```

### Debug Mode Setup
```bash
# Run with debug logging
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Enable SQL logging
# Set in application-dev.properties:
spring.jpa.show-sql=true
logging.level.org.springframework.security=DEBUG
```

### Performance Monitoring
```bash
# Monitor container resources
docker stats itinerarly-backend itinerarly-db

# Check application metrics
curl http://localhost:8080/actuator/metrics

# Database performance
docker exec -it itinerarly-db mysqladmin processlist
```

##  Development Workflow

### Setting Up Development Environment
1. Clone repository and setup environment variables
2. Start development containers: `docker-compose -f docker-compose.yml -f docker-compose.dev.yml up`
3. Access Swagger UI at http://localhost:8080/swagger-ui/index.html
4. Test OAuth2 flows with configured providers
5. Monitor logs for debugging: `docker-compose logs -f backend`

### Making Changes
1. Create feature branch from main
2. Implement changes with appropriate tests
3. Run test suite: `mvn test`
4. Update documentation if needed
5. Test with Docker containers
6. Submit pull request with detailed description

### Testing Strategy
- Unit tests for individual components
- Integration tests for OAuth2 flows
- Repository tests for data layer
- End-to-end tests for complete workflows
- Security testing for authentication flows

##  License

This project is licensed under the MIT License - see the LICENSE file for details.

##  Support and Contributing

### Getting Help
- **Issues**: Create detailed GitHub issues with logs and steps to reproduce
- **Documentation**: Check this README and inline code documentation
- **Logs**: Review Docker container logs for debugging information
- **Community**: Contribute to discussions and improvements

### Contributing Guidelines
1. Fork the repository and create feature branches
2. Follow existing code style and conventions
3. Add comprehensive tests for new features
4. Update documentation for API changes
5. Ensure all tests pass before submitting PR
6. Include detailed PR description with testing instructions

### Code Quality Standards
- Comprehensive test coverage (aim for 80%+)
- Proper error handling and logging
- Security best practices implementation
- Documentation for public APIs
- Docker compatibility and optimization
