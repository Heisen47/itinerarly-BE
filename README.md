# Itinerarly Backend

A Spring Boot REST API backend service that provides OAuth2 authentication with multiple providers and token-based access control for travel itinerary management.

## Features

- **Multi-Provider OAuth2 Authentication**: Support for Google, GitHub, Facebook, and Twitter
- **JWT Token Authentication**: Secure token-based authentication system
- **Rate Limiting**: Daily token-based API usage limits per user
- **User Management**: Complete user profile management with provider-specific data
- **Time Zone Support**: IST (Indian Standard Time) timezone support for user activities
- **RESTful API**: Clean REST endpoints for itinerary management

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security OAuth2
- **Database**: JPA/Hibernate with configurable database
- **Authentication**: JWT (JSON Web Tokens)
- **Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven
- **Java Version**: 17+

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Database (PostgreSQL/MySQL/H2)
- OAuth2 credentials for supported providers

## Configuration

### OAuth2 Provider Setup

Create OAuth2 applications for each provider:

1. **Google**: [Google Cloud Console](https://console.cloud.google.com/)
2. **GitHub**: [GitHub Developer Settings](https://github.com/settings/developers)
3. **Facebook**: [Facebook Developers](https://developers.facebook.com/)
4. **Twitter**: [Twitter Developer Portal](https://developer.twitter.com/)

### Application Properties

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: profile,email
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: user:email
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID}
            client-secret: ${FACEBOOK_CLIENT_SECRET}
            scope: email,public_profile
          twitter:
            client-id: ${TWITTER_CLIENT_ID}
            client-secret: ${TWITTER_CLIENT_SECRET}
            scope: users.read,tweet.read

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

token:
  daily-limit: 6
```

## Installation & Setup 🛠️
To get started, follow these steps:

Clone the repository
Clone the repository using Git and navigate into the project directory.

```git clone https://github.com/Heisen47/itinerarly-BE.git```

```cd itinerarly-BE```

## Set environment variables
Before running the application, you'll need to set up the necessary environment variables for authentication and security.

```export GOOGLE_CLIENT_ID=your_google_client_id```

```export GOOGLE_CLIENT_SECRET=your_google_client_secret```

```export GITHUB_CLIENT_ID=your_github_client_id```

```export GITHUB_CLIENT_SECRET=your_github_client_secret```

```export FACEBOOK_CLIENT_ID=your_facebook_client_id```

```export FACEBOOK_CLIENT_SECRET=your_facebook_client_secret```

```export TWITTER_CLIENT_ID=your_twitter_client_id```

```export TWITTER_CLIENT_SECRET=your_twitter_client_secret```

```export JWT_SECRET=your_jwt_secret_key```


## Build and run
Use Maven to build and run the application.

`mvn clean install`
`mvn spring-boot:run`

## API Endpoints 
Here's a breakdown of the available API endpoints:

**Authentication**
```GET /oauth2/authorization/{provider}: Initiates OAuth2 login for Google, GitHub, Facebook, or Twitter.

POST /api/v1/logout: Logs out the current user.
```

**User Management**
- GET /api/v1/user/profile: Retrieves the profile of the currently authenticated user.

- GET /api/v1/user/tokens: Gets the number of remaining daily tokens for the user.

**Application**
- GET /api/v1/start: The application's start endpoint.

- GET /swagger-ui/: Access the API documentation.

## Authentication Flow 
The authentication process works as follows:

- A user initiates OAuth2 login via /oauth2/authorization/{provider}.

- The user authenticates with their chosen provider (e.g., Google, GitHub).

- The application receives an OAuth2 callback with the user's information.

- A JWT token is generated and set as an HTTP-only cookie.

- The user can then access protected endpoints using the JWT token.

## User Model 
This section seems to be a heading with no content provided in the original text.
You can add more details about the User Model here if you have them.

```User {
id: Long
oauthId: String
email: String
name: String
username: String
avatarUrl: String
provider: String (google/github/facebook/twitter)
dailyTokens: Integer
lastTokenRefresh: LocalDate
loginTime: ZonedDateTime (IST)
}
```

## Token System 🪙
Each new user receives a daily token limit (configurable in application.yml, default is 6).

- Tokens are intended to be used for rate-limiting certain API calls.

- The token count for a user resets daily at midnight IST.

## Development 💻
1. Running Tests
2. Execute the test suite using Maven:

```mvn test```


# API Documentation
With the application running, you can access the Swagger UI for interactive API documentation at: http://localhost:8080/swagger-ui/index.html

# Contributing 
Contributions are welcome! Please follow these steps:

1. Fork the repository.

2. Create a new feature branch (git checkout -b feature/your-feature-name).

3. Make your changes and commit them (git commit -m 'Add some feature').

4. Push to the branch (git push origin feature/your-feature-name).

5. Open a Pull Request.

## License 
This project is licensed under the MIT License. See the LICENSE file for details.

# Support 
For support or to report an issue, please open an issue on the GitHub repository.