# Spring Boot JWT Authentication Example

This is a simple Spring Boot application that demonstrates how to implement JWT (JSON Web Token) based authentication and authorization using Spring Security. The application exposes two endpoints:
1. `/authenticate`: Authenticates a user and returns a JWT token.
2. `/hello`: A protected endpoint that can only be accessed with a valid JWT token.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Application Structure](#application-structure)
  - [Main Application](#main-application)
  - [Controller](#controller)
  - [Security Configuration](#security-configuration)
  - [User Details Service](#user-details-service)
  - [JWT Utility](#jwt-utility)
- [Running the Application](#running-the-application)
- [Testing the Endpoints](#testing-the-endpoints)
- [License](#license)

## Prerequisites
- Java 11 or higher
- Maven 3.6.0 or higher

## Getting Started
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/spring-security-jwt-example.git
   cd spring-security-jwt-example
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Application Structure

### Main Application

Explanation:
- Annotated with `@SpringBootApplication` to mark this class as a Spring Boot application.
- Contains the `main` method to launch the Spring Boot application.

### Controller

Explanation:
- Annotated with `@RestController` to mark this class as a controller where every method returns a domain object instead of a view.
- Defines two endpoints:
  - `/hello`: Returns a simple "Hello World" message.
  - `/authenticate`: Authenticates the user and returns a JWT token.
- Uses `AuthenticationManager` to authenticate the user.
- Uses `JwtUtil` to generate and return a JWT token upon successful authentication.
- Uses `MyUserDetailsService` to load user details from a hardcoded user for demonstration purposes.

### Security Configuration

Explanation:
- Annotated with `@EnableWebSecurity` to enable Spring Security’s web security support.
- Extends `WebSecurityConfigurerAdapter` to provide custom security configurations.
- Defines beans for `AuthenticationManager` and `PasswordEncoder`.
  - `PasswordEncoder` is configured as `NoOpPasswordEncoder` for simplicity (not recommended for production).
- Configures HTTP security to:
  - Disable CSRF protection.
  - Permit all requests to the `/authenticate` endpoint.
  - Require authentication for any other request.
  - Use stateless session management.
- Adds a custom filter `JwtRequestFilter` before the `UsernamePasswordAuthenticationFilter` to handle JWT validation.

### User Details Service

Explanation:
- Annotated with `@Service` to indicate that this class is a Spring service component.
- Implements `UserDetailsService` to provide user details for authentication.
- The `loadUserByUsername` method:
  - Takes a username as an argument and returns a hardcoded `User` object with:
    - Username: `"foo"`
    - Password: `"foo"`
    - Authorities: an empty list (no roles or permissions assigned).
  - In a real application, this method should query a user repository (e.g., database) to retrieve user information.

### JWT Utility

Explanation:
- Annotated with `@Service` to indicate that this class is a Spring service component.
- Manages JWT token generation, extraction, and validation.

#### Fields:
- `SECRET_KEY`: The secret key used for signing and verifying JWT tokens. It should be kept secure and not hardcoded in a real application.

#### Token Extraction Methods:
- `extractUsername`: Extracts the username (subject) from the token.
- `extractExpiration`: Extracts the expiration date from the token.
- `extractClaim`: General method to extract any claim using a provided function.
- `extractAllClaims`: Parses the token to extract all claims.

#### Token Validation Methods:
- `isTokenExpired`: Checks if the token has expired by comparing its expiration date with the current date.
- `validateToken`: Validates the token by checking the username and ensuring it hasn't expired.

#### Token Generation Methods:
- `generateToken`: Generates a new JWT token for the given `UserDetails`.
- `createToken`: Creates the token with the provided claims and subject, sets the issuance and expiration times, and signs it using the HS256 algorithm and the secret key.

This class provides the essential utilities for handling JWT tokens within the Spring Security framework, facilitating the creation of tokens upon successful authentication and the validation of tokens in subsequent requests.

## Running the Application

1. Build the project:
   ```bash
   mvn clean install
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Testing the Endpoints

1. **Authenticate to get a JWT Token**:
   - Send a POST request to `/authenticate` with a JSON body containing the username and password:
     ```json
     {
       "username": "foo",
       "password": "foo"
     }
     ```
   - If the credentials are correct, you will receive a response containing the JWT token:
     ```json
     {
       "jwt": "your-jwt-token"
     }
     ```

2. **Access the Protected Endpoint**:
   - Send a GET request to `/hello` with the JWT token in the Authorization header:
     ```
     Authorization: Bearer your-jwt-token
     ```
   - If the token is valid, you will receive a "Hello World" response.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

This README file provides a detailed overview of the application's functionality, structure, and how to run and test it.