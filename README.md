# Online Book Store 📚

A robust and secure RESTful e-commerce web application for an online book store, built using the Spring Boot ecosystem. This project simulates a real-world production-ready backend system with role-based access control, relational database management, and automated API documentation.

## ✨ Features

- **User Authentication & Authorization**: Secure registration and login flows using JWT tokens.
- **Role-Based Access Control (RBAC)**: 
  - `USER`: Can browse books, search by criteria, manage their shopping cart, and place orders.
  - `ADMIN`: Full CRUD capabilities over the book catalog and category management.
- **Shopping Cart Management**: Add, update, and remove items dynamically.
- **Order Processing**: Place orders with automatic total cost calculation and status tracking.
- **Validation & Error Handling**: Comprehensive request validation and global exception handling with meaningful HTTP responses.

## 🛠️ Technologies & Tools Used

- **Java 21**
- **Spring Boot 3.x**
- **Spring Security** (JWT authentication)
- **Spring Data JPA** (Data persistence layer)
- **Liquibase** (Database schema migration and version control)
- **MySQL** (Relational database management system)
- **MapStruct** (Efficient Object-to-Object mapping DTO <-> Entity)
- **Swagger UI / OpenAPI 3** (Interactive API documentation)
- **Docker** (Containerization for seamless deployment)
- **Maven** (Project build and dependency management)

## 🗄️ Architecture & Database Structure

The project follows a clean **N-tier architecture layer pattern**:
`Controller ➡️ Service ➡️ Repository ➡️ Database`

Database migrations are managed via **Liquibase**, ensuring reproducible and safe database states across development and production environments.

## 🚀 Getting Started

### Prerequisites
- JDK 21 or higher
- Maven 3.x
- Docker & Docker Compose (optional, for fast startup)

### Local Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/acidden/online-book-store
   cd online-book-store
   ```

2. **Configure Environment Variables:**
   Create or modify the `src/main/resources/application.properties` file to set up your database connection strings, credentials, and JWT secret keys.

3. **Run with Docker Compose (Recommended):**
   ```bash
   docker-compose up --build
   ```

4. **Or Build and Run Locally via Maven:**
   ```bash
   mvn clean package
   java -jar target/online-book-store-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080`.

## 📖 API Documentation & Testing

Once the application is running, you can explore, test, and interact with all the available endpoints via the interactive Swagger UI interface:

🔗 **Swagger UI URL:** [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)

### Sample API Workflows:
1. `POST /api/auth/registration` — Register a new account.
2. `POST /api/auth/login` — Authenticate and receive a Bearer JWT Token.
3. Include the JWT Token in the `Authorization` header (`Bearer <token>`) for subsequent requests to protected endpoints like managing the shopping cart or placing an order.

## 💡 Challenges Faced & Key Takeaways

- **JWT Security Configuration:** Setting up robust stateless authentication and correctly handling endpoint path matches for different roles (`USER` vs `ADMIN`) required a deep dive into Spring Security filters.
- **Database Consistency:** Implementing Liquibase helped avoid schema mismatch issues across different machines during development, cementing the importance of database migration tools.
- **DTO Mappings:** Utilizing MapStruct drastically reduced boilerplate code while converting domain entities to data transfer objects, improving overall code maintainability.
