# Itinerarly Backend

A Spring Boot REST API backend for the Itinerarly travel planning application with OAuth2 authentication and JWT token management. I have a cron-job that deletes your data from my table after every 3 days , i won't store your data.

## Features

- **OAuth2 Authentication**: Google and GitHub login integration
- **JWT Token Management**: Secure authentication with daily token limits
- **RESTful APIs**: User management and token tracking endpoints
- **MySQL Database**: Production-ready database with connection pooling
- **Docker Support**: Containerized deployment with Docker Compose
- **Comprehensive Testing**: Unit tests, integration tests, and test containers
- **Production Ready**: Health checks, monitoring, and security configurations

## Tech Stack

- **Framework**: Spring Boot 3.4.5
- **Java Version**: 21
- **Database**: MySQL 8.0 (H2 for testing)
- **Security**: Spring Security with OAuth2
- **Authentication**: JWT tokens
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, Mockito, TestContainers
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker & OrbStack (for containerization)
- MySQL 8.0 (for local development)

## Environment Variables

### Required for Production
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/itinerarly
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password

# JWT Security
JWT_SECRET=your-super-secret-jwt-key

# Frontend URL
FRONTEND_URL=https://itinerarly-fe.vercel.app

# OAuth2 - GitHub
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

# OAuth2 - Google
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Optional
APP_DAILY_TOKEN_LIMIT=6
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false
```

### Optional for Development
```bash
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=itinerarly_dev
MYSQL_USER=devuser
MYSQL_PASSWORD=devpassword
```

## Quick Start

### Local Development (without Docker)
```bash
# Clone the repository
git clone <repository-url>
cd itinerarly-BE

# Set up environment variables
cp .env.example .env
# Edit .env with your values

# Run with Maven
mvn spring-boot:run
```

### Local Development (with Docker)
```bash
# Use the convenient script for OrbStack/Docker
./scripts/local-docker.sh

# Or manually:
docker-compose -f docker-compose.dev.yml up -d
```

### Production Deployment
```bash
# Set environment variables first
export SPRING_DATASOURCE_URL="your-database-url"
export JWT_SECRET="your-jwt-secret"
# ... other variables

# Deploy using script
./scripts/deploy-prod.sh

# Or manually:
docker-compose -f docker-compose.prod.yml up -d
```

## Docker Configuration

The project includes three Docker Compose configurations:

### 1. Development (`docker-compose.dev.yml`)
- Includes local MySQL database
- Uses port 8081 to avoid conflicts
- Development-friendly settings
- Source code mounting for hot reload

### 2. Production (`docker-compose.prod.yml`)
- Uses external/remote database
- Optimized for production
- Resource limits and health checks
- Secure configuration

### 3. Full Stack (`docker-compose.yml`)
- Complete setup with local MySQL
- Suitable for staging or full local testing
- All services in one stack

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories
```bash
# Unit tests only
mvn test -Dtest="**/*Test"

# Integration tests only
mvn test -Dtest="**/*IntegrationTest"

# Repository tests only
mvn test -Dtest="**/*RepositoryTest"
```

### Test Structure
```
src/test/java/
├── controller/           # Controller layer tests
├── service/             # Service layer tests
├── repository/          # Repository tests
├── integration/         # Integration tests
└── UserModelTest.java   # Model tests
```

## 📡 API Endpoints

### Authentication
- `GET /oauth2/authorization/google` - Google OAuth login
- `GET /oauth2/authorization/github` - GitHub OAuth login

### User Management
- `GET /api/v1/user/profile` - Get user profile
- `GET /api/v1/user/tokens` - Get token status

### Token Management
- `POST /api/v1/tokens/use` - Use a token
- `GET /api/v1/tokens/available` - Check token availability

### Public Endpoints
- `GET /api/v1/start` - Application start endpoint
- `GET /test` - Health test endpoint
- `GET /swagger-ui.html` - API documentation

## 🔧 Configuration Profiles

### Development (`application.properties`)
- H2/MySQL database
- Debug logging enabled
- CORS for localhost:3000

### Production (`application-prod.properties`)
- Remote MySQL database
- Optimized connection pooling
- Security hardening
- Performance logging

### Testing (`application-test.properties`)
- H2 in-memory database
- Debug logging for tests
- Mock OAuth2 configuration

## Security Features

- **OAuth2 Integration**: Google and GitHub providers
- **JWT Authentication**: Secure token-based auth
- **CORS Configuration**: Proper cross-origin setup
- **CSRF Protection**: Enabled for forms, disabled for APIs
- **Secure Cookies**: HttpOnly, Secure, SameSite attributes
- **Input Validation**: Request validation and sanitization

## Monitoring & Health Checks

- **Actuator Endpoints**: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
- **Database Health**: Automatic DB connection monitoring
- **Container Health**: Docker health checks configured
- **Application Metrics**: Performance and usage tracking

## Troubleshooting

### Database Connection Issues
1. **Remote MySQL timeouts**: Check firewall and network connectivity
2. **Connection pool exhaustion**: Increase `maximum-pool-size` in properties
3. **SSL issues**: Verify SSL certificates and `useSSL` parameter

### OAuth2 Issues
1. **Redirect URI mismatch**: Ensure OAuth app settings match your domain
2. **CORS errors**: Verify frontend URL in CORS configuration
3. **Provider errors**: Check client ID/secret configuration

### Docker Issues
1. **OrbStack not running**: Ensure OrbStack is started
2. **Port conflicts**: Check if ports 8080/3306 are already in use
3. **Environment variables**: Verify all required vars are set

### Common Commands
```bash
# View application logs
docker-compose logs -f backend

# Connect to MySQL container
docker exec -it itinerarly-mysql mysql -u root -p

# Rebuild containers
docker-compose down && docker-compose up --build -d

# Clean Docker system
docker system prune -a
```

## Performance Optimization

- **Connection Pooling**: HikariCP with optimized settings
- **JVM Tuning**: Container-aware memory settings
- **Database Indexing**: Optimized queries and indexes
- **Caching**: Application-level caching for frequently accessed data

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Related Projects

- [Frontend Repository](https://github.com/your-username/itinerarly-frontend)
- [Deployment Scripts](https://github.com/your-username/itinerarly-deployment)

## Support

- **Issues**: [GitHub Issues](https://github.com/your-username/itinerarly-BE/issues)
- **Live Demo**: [Frontend](https://itinerarly-fe.vercel.app)
