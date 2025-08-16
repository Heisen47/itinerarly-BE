# Itinerarly Backend

A production-ready Spring Boot REST API backend service for travel itinerary management with comprehensive OAuth2 authentication, containerized deployment, and robust testing framework.

##  Features

- **Multi-Provider OAuth2 Authentication**: Google, GitHub, Facebook, and Twitter support
- **JWT Token Authentication**: Secure token-based authentication with HTTP-only cookies
- **Rate Limiting**: Daily token-based API usage limits with automatic refresh
- **User Management**: Complete user profile management with provider-specific data
- **Production-Ready Containerization**: Docker and Docker Compose setup with health checks
- **Comprehensive Testing**: Unit and integration tests with proper test database setup
- **Security Best Practices**: Non-root containers, proper CORS configuration, secure secrets management
- **Monitoring & Health Checks**: Built-in health endpoints and container monitoring
- **Multi-Environment Support**: Separate configurations for development, testing, and production

##  Tech Stack

- **Framework**: Spring Boot 3.x with Java 21
- **Security**: Spring Security OAuth2 with JWT
- **Database**: MySQL 8.0 (production), H2 (testing)
- **Containerization**: Docker & Docker Compose
- **Authentication**: JWT with HTTP-only cookies
- **Documentation**: Swagger/OpenAPI 3.0
- **Testing**: JUnit 5, Mockito, TestContainers
- **Build Tool**: Maven 3.9+

##  Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker & Docker Compose
- OAuth2 credentials for supported providers

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
```

### 3. OAuth2 Provider Setup

#### Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth2 credentials
5. Add redirect URI: `http://localhost:8080/login/oauth2/code/google`

#### GitHub OAuth2
1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create new OAuth App
3. Set Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`

#### Facebook OAuth2
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create new app
3. Add Facebook Login product
4. Set Valid OAuth Redirect URI: `http://localhost:8080/login/oauth2/code/facebook`

#### Twitter OAuth2
1. Go to [Twitter Developer Portal](https://developer.twitter.com/)
2. Create new app
3. Enable OAuth 2.0
4. Set Callback URI: `http://localhost:8080/login/oauth2/code/twitter`

## 🐳 Docker Deployment

### Production Deployment

```bash
# Quick production deployment
./scripts/deploy-prod.sh

# Manual production deployment
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d
```

### Development Deployment

```bash
# Development with hot reload
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build

# Local development
mvn spring-boot:run
```

### Docker Features

- **Multi-stage builds** for optimized image sizes
- **Health checks** for both backend and database
- **Non-root containers** for enhanced security
- **Resource limits** for production stability
- **Persistent volumes** for database data
- **Custom networks** for service isolation

##  Testing

### Run All Tests

```bash
# Unit and integration tests
mvn test

# With coverage report
mvn test jacoco:report
```

### Test Structure

```
src/test/java/
├── controller/          # Controller layer tests
├── service/            # Service layer tests
├── repository/         # Repository layer tests
├── integration/        # Integration tests
└── config/            # Configuration tests
```

### Testing Database

The application uses H2 in-memory database for testing to avoid conflicts with the main MySQL database.

##  API Documentation

### Swagger UI
- **Development**: http://localhost:8080/swagger-ui/index.html
- **Production**: http://your-domain.com/swagger-ui/index.html

### Main Endpoints

#### Authentication
- `GET /api/v1/start` - Health check endpoint
- `POST /api/v1/logout` - User logout
- `GET /api/v1/validate` - Token validation
- `GET /oauth2/authorization/{provider}` - OAuth2 login (google, github, facebook, twitter)

#### Token Management
- `GET /api/v1/tokens/remaining` - Get remaining daily tokens
- `POST /api/v1/tokens/consume` - Consume a token

#### User Management
- `GET /api/v1/users/profile` - Get user profile
- `PUT /api/v1/users/profile` - Update user profile

##  Security Features

- **JWT with HTTP-only cookies** for secure token storage
- **CORS configuration** for cross-origin requests
- **Non-root Docker containers** for container security
- **Environment variable secrets** - no hardcoded credentials
- **Rate limiting** with daily token refresh
- **Secure OAuth2 flows** with proper state management

##  Monitoring & Health

### Health Check Endpoints
- `GET /api/v1/start` - Application health
- `GET /actuator/health` - Detailed health information
- `GET /actuator/metrics` - Application metrics

### Docker Health Checks
- Backend: HTTP health check on `/api/v1/start`
- Database: MySQL ping command
- Automatic restart on failure

##  Production Deployment

### Environment Requirements
- **CPU**: Minimum 1 core, recommended 2 cores
- **Memory**: Minimum 1GB RAM, recommended 2GB
- **Storage**: SSD recommended for database performance
- **Network**: HTTPS enabled for production OAuth2

### Deployment Checklist
- [ ] Environment variables configured
- [ ] OAuth2 credentials setup for production domains
- [ ] Database backup strategy in place
- [ ] HTTPS/SSL certificates configured
- [ ] Monitoring and logging setup
- [ ] Resource limits configured

### Production Optimizations
- JVM memory settings optimized for containers
- Connection pooling configured
- Database indexes for performance
- Logging configured for production
- Health checks and restart policies

##  Troubleshooting

### Common Issues

1. **OAuth2 Login Failed**
   - Check OAuth2 credentials in `.env`
   - Verify redirect URIs in provider settings
   - Ensure correct provider configuration

2. **Database Connection Issues**
   - Verify database credentials
   - Check if database container is healthy
   - Review database logs: `docker-compose logs db`

3. **JWT Token Issues**
   - Ensure JWT_SECRET is properly set
   - Check token expiration settings
   - Verify cookie settings for HTTPS

4. **Docker Build Issues**
   - Clean Maven cache: `mvn clean`
   - Rebuild without cache: `docker-compose build --no-cache`
   - Check Docker logs: `docker-compose logs backend`

### Logs and Debugging

```bash
# View application logs
docker-compose logs -f backend

# View database logs
docker-compose logs -f db

# Debug mode (development)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

##  Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with proper tests
4. Ensure all tests pass
5. Submit a pull request

##  License

This project is licensed under the MIT License - see the LICENSE file for details.

##  Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review Docker logs for error details
