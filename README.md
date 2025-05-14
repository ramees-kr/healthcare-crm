# Healthcare CRM - J2EE Business Components Project

**Project Goal:** A CRM system for healthcare centers to manage customer records, insurance details, and employee task assignments using Spring Boot, Thymeleaf, Spring Security, REST APIs, Docker deployment, and AWS RDS for database hosting.

**Group Members:**

- John Shaize
- Darshan Kiran Upadhyay
- Gaurang Hareshkumar Dhameliya
- Ramees Karolil Rasheed

---

## Technology Stack

- **Backend:** Java 21, Spring Boot 3.4.2, Spring MVC, Spring Data JPA, Spring Security, REST Controllers
- **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
- **Database:** **AWS RDS (MySQL 8)**
- **API Documentation:** Springdoc OpenAPI (Swagger UI)
- **Containerization:** Docker, Docker Compose
- **Build Tool:** Maven
- **ORM:** Hibernate (via Spring Data JPA)
- **Validation:** Jakarta Bean Validation
- **Configuration Management:** `.env` files, `dotenv-java` library
- **Other:** Lombok

---

## Features Implemented

- **User Roles & Security:** Secure login for Admins and Employees with distinct permissions enforced by Spring Security.
- **Admin Functionality (Web UI):**
  - Dashboard overview (Customer/Employee/Pending Task counts).
  - Full CRUD for Customers (including Insurance).
  - Full CRUD for Employees.
  - Full CRUD for Tasks (assigning to Customer/Employee).
  - Task Follow-Up Center (Overdue/Due Soon tasks).
- **Employee Functionality (Web UI):**
  - View assigned tasks.
  - Update assigned task status.
- **Data Validation:** Server-side validation on forms with user feedback.
- **REST APIs:**
  - CRUD endpoints for Customers, Employees, and Tasks (`/api/**`).
  - Interactive API documentation via Swagger UI.
- **Cloud Database Integration:** Application connects to a managed **AWS RDS for MySQL** instance.
- **Containerized Application Deployment:** Spring Boot application runnable via Docker Compose, connecting to AWS RDS.
- **Secure Credential Management:** Database credentials managed via an external `.env` file for both local and Dockerized execution, keeping secrets out of version control.
- **Data Initialization:** Automatic creation of default admin user and mock data on first run against a new database schema.

---

## Prerequisites

- **AWS Account:** Required for creating and using the AWS RDS database instance (Free Tier eligible options can be used).
- **Java Development Kit (JDK):** Version 21 or later.
- **Apache Maven:** Version 3.6+ (for building the project JAR).
- **Docker & Docker Compose:** Docker Desktop (recommended) or Docker Engine with Compose plugin/command (supporting `docker compose` or `docker-compose`). Required for containerized deployment of the application.
- **Git:** For cloning the repository.

---

## Environment Configuration (`.env` file)

Before running the application (either locally or via Docker Compose), you need to configure the database connection by creating a `.env` file in the `Health-Care-CRM/backend/` directory.

1.  **Create the `.env` file:**
    In the `Health-Care-CRM/backend/` directory, create a file named `.env`.

2.  **Add your AWS RDS credentials and database details:**

    ```env
    # Health-Care-CRM/backend/.env
    # Replace with your actual AWS RDS details

    RDS_ENDPOINT=your_actual_rds_instance_endpoint.rds.amazonaws.com
    RDS_USERNAME=your_rds_master_username
    RDS_PASSWORD=your_rds_master_password
    DB_NAME=healthcarecrm
    RDS_PORT=3306 # Usually 3306 for MySQL
    ```

3.  **Important Security Note:**
    Ensure the `.env` file is listed in your `Health-Care-CRM/backend/.gitignore` file to prevent committing secrets to version control:
    ```
    .env
    ```

---

## Setup and Running Instructions

### Method 1: Using Docker Compose (Recommended for Application Container)

This method runs the Spring Boot application inside a Docker container, which then connects to your AWS RDS database.

1.  **Clone the Repository:**
    ```bash
    git clone <your-repository-url>
    cd Health-Care-CRM # Or your project's root directory
    ```
2.  **Navigate to Backend Directory:**
    ```bash
    cd backend
    ```
3.  **Configure Environment:**
    Create and populate the `.env` file in the `backend/` directory as described in the "Environment Configuration" section above with your AWS RDS details.
4.  **Build the Application JAR:**
    ```bash
    # Use -DskipTests if you want to avoid running tests that might require specific local setup
    mvn clean package -DskipTests
    ```
5.  **Build the Application Docker Image:**
    ```bash
    # Use ONE of the following commands:
    docker compose build app
    # OR
    # docker-compose build app
    ```
6.  **Start the Application Container:**
    ```bash
    # Use ONE of the following commands:
    docker compose up app
    # OR (to run detached/in background)
    # docker compose up -d app
    # OR (if using older docker-compose)
    # docker-compose up app
    ```
    The application container will start and connect to the AWS RDS database specified in your `.env` file.
7.  **Access the Application:**
    - Web UI: `http://localhost:8080`
    - API Docs (Swagger UI): `http://localhost:8080/swagger-ui.html`
8.  **Stopping:** Press `Ctrl+C` in the terminal where compose is running, or use `docker compose down app` (or `docker-compose down app`) if running detached.

### Method 2: Running Locally (Spring Boot Application direct, connecting to AWS RDS)

This method runs the Spring Boot application directly on your machine (e.g., via IDE or `java -jar`) and connects to your AWS RDS database.

1.  **Clone the Repository** (if not already done).
2.  **Navigate to Backend Directory:**
    ```bash
    cd Health-Care-CRM/backend
    ```
3.  **Configure Environment:**
    Create and populate the `.env` file in the `backend/` directory as described in the "Environment Configuration" section above with your AWS RDS details. The `dotenv-java` library included in the project will load this file.
4.  **Build the Project:**
    ```bash
    mvn clean package -DskipTests
    ```
5.  **Run the Application:**
    ```bash
    java -jar target/healthcarecrm-0.0.1-SNAPSHOT.jar
    ```
    _(Alternatively, run the `HealthcarecrmApplication` main method directly from your IDE)_.
    The application will load credentials from the `.env` file and connect to AWS RDS.
6.  **Access the Application:**
    - Web UI: `http://localhost:8080`
    - API Docs (Swagger UI): `http://localhost:8080/swagger-ui.html`

---

## Default Login Credentials

- The application initializes a default **Admin** user if no admins exist in the database:
  - **Email:** `admin@clinic.com`
  - **Password:** `password123`
- If the database is empty, it also creates mock **Employee** users:
  - **Emails:** `alice.j@clinic.com`, `bob.w@clinic.com`, etc.
  - **Password (for all mock employees):** `password123`

---

## Project Structure

- `backend/`: Contains the Spring Boot application source and build files.
  - `src/main/java`: Main Java source code (Controllers, Services, Repositories, Models, Config).
  - `src/main/resources`: Configuration (`application.properties`), static assets (`static/`), templates (`templates/`).
  - `src/test/java`: Unit tests.
  - `.env`: (Gitignored) Environment variables for database credentials, etc. **Create this file locally.**
  - `pom.xml`: Maven project configuration.
  - `Dockerfile`: Instructions to build the application's Docker image.
  - `docker-compose.yml`: Defines the application service for Docker Compose (connects to external RDS).
- `design-docs/`: Contains design documents and screenshots.
  - `screenshots/`: Contains application screenshots.
  - `phase3-milestone.md`: Final project report document.
- `README.md`: This file.

---

## Screenshots

![Login Page](design-docs/screenshots/001_login.png "Login Page")
_Figure 1: Secure login page._

![Admin Dashboard](design-docs/screenshots/002_Admin_Dashboard.png "Admin Dashboard")
_Figure 2: Admin dashboard overview._

![Admin Customer List & Details View](design-docs/screenshots/003_Admin_Customer_List_Detailes_View.png "Admin Customer List & Details View")
_Figure 3: Customer management interface._

![Admin Task Management](design-docs/screenshots/004_Admin_Task_Management.png "Admin Task Management")
_Figure 4: Task management interface._

![Admin Follow-Up Center](design-docs/screenshots/005_Admin_Followup.png "Admin Follow-Up Center")
_Figure 5: Admin view for overdue/due soon tasks._

![Employee Task List & Status Update](design-docs/screenshots/006_Employee_Task_List.png "Employee Task List & Status Update")
_Figure 6: Employee view for managing assigned tasks._

![Example Validation Error Display](design-docs/screenshots/007_Server_Side_Validations.png "Example Validation Error Display")
_Figure 7: Form validation feedback._

![Swagger UI Documentation Page](design-docs/screenshots/008_Swagger_UI_Rest_API.png "Swagger UI Documentation Page")
_Figure 8: Interactive REST API documentation._

---
