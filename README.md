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

### Local Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com
   cd online-book-store
   ```

2. **Configure Environment Variables:**
    * Create a file named `.env` in the root directory of the project.
    * Copy the content from `.env.template` into your newly created `.env` file. (Make sure ports like `SPRING_LOCAL_PORT=8080` and `MYSQLDB_LOCAL_PORT=3306` are defined there).

3. **Run with Docker Compose (Recommended):**
    * Make sure **Docker Desktop** is running on your machine.
    * Execute the following command in your terminal:
   ```bash
   docker compose up --build
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

### 🧪 Automated Testing via Postman (Recommended)

For a more comprehensive evaluation of all endpoints, roles, and complex business workflows (like cart management and order checkout), a pre-configured automated Postman collection file is attached in the repository root: **`online-book-store.postman_collection.json`**.

#### How to utilize the collection:
1. Open your Postman app, click **Import**, and select the `online-book-store.postman_collection.json` file from the project directory.
2. The collection leverages dynamic variables (`{{jwt_token_user}}` and `{{jwt_token_admin}}`) to cleanly isolate user permissions. **You do not need to manually copy-paste authorization headers.**

#### Testing User vs Admin flows in one click:
- **Testing standard `USER` endpoints:**
    - Locate and expand the `users` folder inside the collection, select the **`Login as User`** request, and click **Send**.
    - This request authenticates default user credentials pre-loaded via Liquibase migrations. A Post-response script will automatically intercept the fresh token and update it globally. You can now immediately run any standard endpoints (e.g., viewing books, interacting with the Shopping Cart).
- **Testing protected `ADMIN` endpoints:**
    - Select the **`Login as Admin`** request in the same folder and click **Send**.
    - The script will securely update the administrator's token variable. Now, you can instantly test privileged endpoints inside the `Categories` or `book` folders (like creating/deleting books or categories) without encountering unauthorized blocks.

### Sample API Workflows:
1. `POST /api/auth/registration` — Register a new account.
2. `POST /api/auth/login` — Authenticate and receive a Bearer JWT Token.
3. Include the JWT Token in the `Authorization` header (`Bearer <token>`) for subsequent requests to protected endpoints like managing the shopping cart or placing an order.

## 💡 Challenges Faced & Key Takeaways

- **JWT Security Configuration:** Setting up robust stateless authentication and correctly handling endpoint path matches for different roles (`USER` vs `ADMIN`) required a deep dive into Spring Security filters.
- **Database Consistency:** Implementing Liquibase helped avoid schema mismatch issues across different machines during development, cementing the importance of database migration tools.
- **DTO Mappings:** Utilizing MapStruct drastically reduced boilerplate code while converting domain entities to data transfer objects, improving overall code maintainability.
