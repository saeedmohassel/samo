# Wallet Application

This is a Wallet Application built using Spring Boot 3 and Java 21. It uses PostgreSQL as the database and Spring Security with JWT for authentication. The application provides APIs for user management and wallet operations such as creating wallets, depositing, withdrawing, and transferring money. Swagger is integrated for API documentation.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)

## Features

- User signup, profile, login, and retrieval
- Wallet creation and retrieval
- Deposit, withdraw, and transfer money between wallets
- JWT-based authentication and authorization
- Swagger integration for API documentation

## Requirements

- Java 21
- Spring Boot 3
- PostgreSQL
- Maven

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/ramin-karimkhani/wallet-app.git
   cd wallet-app
   ```

2. **Run Docker Compose in project's root directory where the "Dockerfile" located**
   ```
   docker-compose up -d --build
   ```

3. **Use Swagger API link for API documentation**
   ```html
   http://localhost:8080/swagger-ui/index.html
   ```

## Configuration

### JWT Configuration

- `application.security.jwt.private-key.path`: The secret key for signing JWT tokens. Change this using this command:
   ```
   openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
   ```
- `application.security.jwt.public-key.path`: The public secret key for verify JWT tokens. Create this using this command:
   ```
   openssl rsa -in private_key.pem -pubout -out public_key.pem
   ```
- `application.security.jwt.token.expiration.minutes`: The expiration time for JWT tokens in minutes.


## API Documentation

Swagger is integrated for API documentation. After running the application, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any changes.
