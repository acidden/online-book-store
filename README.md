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

### 🧪 Role-Based API Testing (USER / ADMIN)

The attached automated Postman collection file (**`online-book-store.postman_collection.json`**) is located in the root directory. To maintain security best practices, no default administrative credentials or pre-loaded database passwords are leaked within the source code or migrations.

#### 1. Testing as a standard USER:
* Open Postman and expand the `users` folder inside the imported collection.
* Execute the **`Register User`** request to dynamically create a fresh user account in your local database.
* Execute the **`Login as User`** request using those credentials. The post-response script will automatically intercept the fresh token and update all user-facing requests (e.g., browsing books, managing the shopping cart).

#### 2. Testing protected ADMIN endpoints:
* Any newly registered account receives the default `ROLE_USER` by architecture.
* To test restricted endpoints (inside the `Categories` or `book` folders), **manually assign the administrative role ID** (`role_id: 2` for `ROLE_ADMIN`) to your registered user within your database using IntelliJ Database Tool or any MySQL CLI.
* Configure the corresponding email/password variables in your Postman collection environment, and click **Send** on the **`Login as Admin`** request. The script will securely refresh the authorization scope to let you run full CRUD actions.

*All JWT tokens are dynamically injected into their respective folders. There is no need to manually copy-paste authorization headers.*

## 💡 Challenges Faced & Key Takeaways

- **JWT Security Configuration:** Setting up robust stateless authentication and correctly handling endpoint path matches for different roles (`USER` vs `ADMIN`) required a deep dive into Spring Security filters.
- **Database Consistency:** Implementing Liquibase helped avoid schema mismatch issues across different machines during development, cementing the importance of database migration tools.
- **DTO Mappings:** Utilizing MapStruct drastically reduced boilerplate code while converting domain entities to data transfer objects, improving overall code maintainability.
