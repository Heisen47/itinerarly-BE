# Itinerarly Backend

A production-ready Spring Boot REST API backend service for travel itinerary management with comprehensive OAuth2 authentication, JWT token management, containerized deployment, and robust testing framework.

## 🚀 Features

- **Multi-Provider OAuth2 Authentication**: Google and GitHub OAuth2 support with automatic user registration
- **JWT Token Authentication**: Secure token-based authentication with HTTP-only cookies
- **Daily Token Rate Limiting**: User-based daily API usage limits with automatic midnight refresh
- **User Profile Management**: Complete user profile system with provider-specific data and avatar URLs
- **Production-Ready Containerization**: Multi-stage Docker builds with health checks and security best practices
- **Comprehensive Testing Suite**: Unit, integration, and repository tests with H2 in-memory database
- **Security Best Practices**: Non-root containers, CORS configuration, secure secrets management
- **Monitoring & Health Checks**: Built-in health endpoints and container monitoring
- **Multi-Environment Support**: Separate configurations for development, testing, and production
- **Automatic Token Refresh**: Scheduled daily token refresh service using Spring's @Scheduled annotation

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.4.5 with Java 21
- **Security**: Spring Security OAuth2 with JWT (jjwt library)
- **Database**: MySQL 8.0 (production), H2 (testing)
- **Containerization**: Docker & Docker Compose with multi-stage builds
- **Authentication**: JWT with HTTP-only cookies and secure token validation
- **Documentation**: Swagger/OpenAPI 3.0 with custom configuration
- **Testing**: JUnit 5, Mockito, Spring Boot Test, TestContainers
- **Build Tool**: Maven 3.9+ with dependency management
- **Scheduling**: Spring Task Scheduling for automated token refresh

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker & Docker Compose (or OrbStack)
- OAuth2 credentials for Google and GitHub
- MySQL 8.0 (for production) or H2 (for testing)

## 🔧 Environment Setup

### 1. Clone and Setup Environment

```bash
git clone <repository-url>
cd itinerarly-BE
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```bash
# Database Configuration
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
DB_ROOT_PASSWORD=your_root_password
DB_NAME=itinerarly
DB_PORT=3306

# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_minimum_32_characters

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Application Configuration
FRONTEND_URL=https://itinerarly-fe.vercel.app
APP_DAILY_TOKEN_LIMIT=6
BACKEND_PORT=8080

# Spring Configuration
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/itinerarly
SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false
```

### 3. OAuth2 Provider Setup

#### Google OAuth2 Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth2 credentials
5. Add authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google` (development)
   - `https://your-domain.com/login/oauth2/code/google` (production)

#### GitHub OAuth2 Setup
1. Go to GitHub Settings > Developer settings > OAuth Apps
2. Create a new OAuth App
3. Set authorization callback URL:
   - `http://localhost:8080/login/oauth2/code/github` (development)
   - `https://your-domain.com/login/oauth2/code/github` (production)

## 🏃‍♂️ Quick Start

### Local Development with OrbStack/Docker

```bash
# Make script executable (if not already)
chmod +x scripts/local-docker.sh

# Start development environment
./scripts/local-docker.sh start

# View logs
./scripts/local-docker.sh logs

# Stop services
./scripts/local-docker.sh stop

# Clean up everything
./scripts/local-docker.sh clean
```

### Manual Setup (Without Docker)

```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Start application (development)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Start application (production)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 🧪 Testing

### Run All Tests
```bash
# Run all tests including unit, integration, and repository tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=UserControllerTest

# Run tests in specific package
mvn test -Dtest="com.example.itinerarly_BE.service.*"
```

### Test Database Configuration
- Tests use H2 in-memory database automatically
- Test configuration in `src/test/resources/application-test.properties`
- No additional setup required for testing

## 🐳 Docker & Containerization

### Build and Run with Docker Compose

#### Development Environment
```bash
# Using the convenience script
./scripts/local-docker.sh start

# Or manually
docker-compose -f docker-compose.dev.yml up -d
```

#### Production Environment
```bash
# Set environment variables first
export $(cat .env | xargs)

# Start production services
docker-compose -f docker-compose.prod.yml up -d
```

### Manual Docker Build
```bash
# Build the application image
docker build -t itinerarly-backend:latest .

# Run the container
docker run -d \
  --name itinerarly-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/itinerarly \
  --env-file .env \
  itinerarly-backend:latest
```

## 📊 API Documentation

### Swagger UI
- **Development**: http://localhost:8080/swagger-ui/index.html
- **Production**: https://your-domain.com/swagger-ui/index.html

### Main Endpoints

#### Authentication
- `GET /oauth2/authorization/google` - Google OAuth2 login
- `GET /oauth2/authorization/github` - GitHub OAuth2 login
- `POST /api/v1/logout` - Logout user

#### User Management
- `GET /api/user/profile/{id}` - Get user profile
- `PUT /api/user/profile/{id}` - Update user profile
- `GET /api/user/tokens/{id}` - Get user token count

#### Token Management
- `POST /api/token/validate` - Validate JWT token
- `GET /api/token/refresh/{userId}` - Refresh user tokens

#### Health & Monitoring
- `GET /` - Welcome message
- `GET /api/v1/start` - Health check endpoint

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/com/example/itinerarly_BE/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── model/          # Entity models
│   │   ├── repository/     # JPA repositories
│   │   ├── security/       # Security handlers
│   │   ├── service/        # Business logic
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── application.properties         # Main configuration
│       ├── application-dev.properties     # Development config
│       └── application-prod.properties    # Production config
├── test/
│   ├── java/               # Test classes
│   └── resources/
│       └── application-test.properties    # Test configuration
├── docker/
│   └── mysql/              # MySQL configuration
├── scripts/
│   └── local-docker.sh     # Local development script
└── docker-compose.*.yml    # Container orchestration
```

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **HTTP-Only Cookies**: Prevents XSS attacks
- **CORS Configuration**: Configurable cross-origin requests
- **OAuth2 Integration**: Secure third-party authentication
- **Non-Root Containers**: Security best practices for Docker
- **Environment Variable Secrets**: No hardcoded credentials
- **Rate Limiting**: Daily token usage limits per user

## 🌐 Deployment

### Production Deployment Checklist

1. **Environment Variables**: Set all required environment variables
2. **Database Setup**: Configure production MySQL database
3. **OAuth2 Configuration**: Update redirect URIs for production domain
4. **SSL/TLS**: Enable HTTPS for production
5. **Monitoring**: Set up application monitoring and logging
6. **Backup Strategy**: Implement database backup solution

### Deployment to Cloud Platforms

#### Render.com (Current)
- Build Command: `mvn clean package -DskipTests`
- Start Command: `java -jar target/itinerarly-BE-0.0.1-SNAPSHOT.jar`
- Environment Variables: Set via Render dashboard

#### Docker Registry
```bash
# Tag and push to registry
docker tag itinerarly-backend:latest your-registry/itinerarly-backend:latest
docker push your-registry/itinerarly-backend:latest
```

## 🛠 Development

### Adding New Features
1. Create feature branch: `git checkout -b feature/new-feature`
2. Write tests first (TDD approach)
3. Implement feature
4. Update documentation
5. Submit pull request

### Code Quality
- Follow Spring Boot best practices
- Write comprehensive tests (aim for >80% coverage)
- Use meaningful commit messages
- Document public APIs with Swagger annotations

## 🐛 Troubleshooting

### Common Issues

#### DataSource Configuration Error
```
Failed to configure a DataSource: 'url' attribute is not specified
```
**Solution**: Ensure `SPRING_DATASOURCE_URL` is set in environment variables

#### OAuth2 Redirect Mismatch
```
redirect_uri_mismatch
```
**Solution**: Verify OAuth2 redirect URIs match in provider settings

#### JWT Token Issues
```
JWT signature does not match locally computed signature
```
**Solution**: Ensure `JWT_SECRET` is consistent across deployments

#### Database Connection Failed
```
Communications link failure
```
**Solution**: Verify database is running and connection details are correct

### Debugging
```bash
# Enable debug logging
export LOGGING_LEVEL_COM_EXAMPLE_ITINERARLY_BE=DEBUG

# View application logs
docker-compose logs -f backend

# Access database directly
docker-compose exec db mysql -u root -p
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section above

---

**Current Status**: ✅ Production Ready
**Last Updated**: August 2025
**Version**: 1.0.0
