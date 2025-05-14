# Presentation Content: Healthcare CRM

---

**Slide 1: Title Slide**

- **Title:** Healthcare CRM: J2EE Business Components Project
- **Team Members:** John Shaize, Darshan Kiran Upadhyay, Gaurang Hareshkumar Dhameliya, Ramees Karolil Rasheed
- **Course Name/Code** (Add if applicable)
- **Date:** April 22, 2025
- **(Optional) University/Program Logo**

---

**Slide 2: Introduction & Project Goal**

- **Title:** Project Overview
- **Goal:** Develop a functional MVP Healthcare CRM using Spring Boot & J2EE principles.
- **Core Purpose:** Enable healthcare centers to manage Customer records, Insurance details, and Employee task assignments.
- **Key Objectives:** Implement core CRUD, role-based access, task management, and demonstrate containerized deployment.
- **Team Introduction:** Briefly re-introduce team members.

---

**Slide 3: Technology Stack**

- **Title:** Technology Stack
- **Backend:**
  - Java 21, Spring Boot 3.4.2
  - Spring MVC (Model-View-Controller pattern)
  - Spring Data JPA / Hibernate (Object-Relational Mapping)
  - Spring Security (Authentication & Authorization)
- **Frontend:**
  - Thymeleaf (Server-side Template Engine)
  - Bootstrap 5, HTML5, CSS3 (Styling & Layout)
- **Database:** MySQL 8
- **APIs & Docs:** Spring Web (REST Controllers), Springdoc OpenAPI (Swagger UI)
- **Deployment:** Docker, Docker Compose
- **Build:** Apache Maven

---

**Slide 4: System Architecture**

- **Title:** Architecture Overview
- **Diagram:** (Recommended) Simple diagram showing User -> Browser -> Spring Boot App (Controller -> Service -> Repository) -> MySQL DB; Docker containers for App & DB.
- **Layers:**
  - **Presentation:** Thymeleaf Views, Spring MVC Controllers, REST Controllers.
  - **Service:** Business Logic Layer (`*Service` classes).
  - **Data Access:** Spring Data JPA Repositories (`*Repository` interfaces).
  - **Database:** MySQL persistence layer.
- **Data Flow:** Brief explanation of request handling for both MVC (server-rendered HTML) and API (JSON) requests.

---

**Slide 5: Security Implementation**

- **Title:** Security Features (Spring Security)
- **Authentication:**
  - Form-based login (`/login`).
  - Custom `UserDetailsService` (`UserDetailsServiceImpl`) checks Admin/Employee data.
  - Password Hashing: BCrypt (`BCryptPasswordEncoder`).
- **Authorization:**
  - Role-Based Access Control (RBAC): `ROLE_ADMIN`, `ROLE_EMPLOYEE`.
  - Configuration: `SecurityFilterChain` in `SecurityConfig`.
  - Path Protection: `/admin/**` (Admin only), `/employee/**` (Employee only).
- **API Security:**
  - Reuses web session authentication.
  - CSRF disabled for `/api/**` for easier testing.
- **Secure Defaults:** Default admin user creation.

---

**Slide 6: Backend Features & Database**

- **Title:** Core Backend & Database
- **Entity Modeling (JPA):**
  - Defined `Customer`, `Employee`, `Task`, `Insurance`, `Admin` entities.
  - Established relationships (e.g., Customer-Insurance `@OneToOne`, Task-Customer/Employee `@ManyToOne`).
  - Used Lombok for reduced boilerplate.
- **Data Operations:**
  - Service layer encapsulates business logic for CRUD operations.
  - Spring Data JPA repositories handle DB interaction (e.g., `save()`, `findById()`, `findAll()`, custom queries like `findOverdueTasks`).
- **Database Schema:**
  - MySQL (`healthcarecrm` database).
  - Schema managed by Hibernate (`ddl-auto=update`).
- **Data Initialization:** `CommandLineRunner` populates mock data for demonstration.

---

**Slide 7: Frontend UI/UX & Validation**

- **Title:** Web Interface (UI/UX) & Validation
- **Templating:** Thymeleaf for server-side rendering dynamic HTML.
- **Layout:** Thymeleaf Layout Dialect (`layout:decorate`) for consistent Admin/Employee views. Addressed path resolution for JAR deployment.
- **Styling:** Responsive design using Bootstrap 5. Centralized custom CSS (`custom.css`). Standardized navigation.
- **Key UI Screenshots:** (Show screenshots from report Fig 1-7).
- **Validation:** Server-side using Jakarta Bean Validation annotations (`@NotEmpty`, `@Valid`, `@Size`, etc.). Errors displayed using Thymeleaf integration. Used Validation Groups (`@Validated(OnCreate.class)`).

---

**Slide 8: REST API Implementation**

- **Title:** REST API & Documentation
- **Purpose:** Enable programmatic access to CRM data.
- **Implementation:**
  - Spring MVC `@RestController` classes for Customer, Employee, Task.
  - Mapped to `/api/**` base path.
  - Standard HTTP methods (GET, POST, PUT, DELETE) for CRUD.
  - Reused Service layer logic.
- **Data Format:** JSON request/response bodies.
- **Documentation (Swagger):**
  - Integrated `springdoc-openapi`.
  - Used annotations (`@Tag`, `@Operation`, `@ApiResponse`) for documentation generation.
  - Interactive Swagger UI at `/swagger-ui.html`. (Show screenshot Fig 8).

---

**Slide 9: Deployment with Docker**

- **Title:** Containerized Deployment (Docker Compose)
- **Goal:** Package app & database for easy, consistent deployment.
- **`Dockerfile`:**
  - Based on Java 21 (`eclipse-temurin:21-jdk-jammy`).
  - Copies built JAR (`mvn clean package`).
  - Exposes port 8080, defines entry point.
- **`docker-compose.yml`:**
  - Defines `app` and `db` (MySQL 8) services.
  - Configures network, environment variables (DB connection string for `app` service pointing to `db` service).
  - Includes `healthcheck` & `depends_on.condition: service_healthy` for reliable startup order.
  - Uses named volume for MySQL data persistence.
- **Benefits:** Environment consistency, simplified setup, dependency management.

---

**Slide 10: Live Demonstration**

- **Title:** Application Demo
- **(Prepare for live demo covering key features for Admin and Employee roles, plus optional Swagger UI view)**

---

**Slide 11: Challenges & Limitations**

- **Title:** Challenges & Limitations
- **Scope Management:** Descoping of comprehensive testing (Unit, API, Performance) due to time constraints.
- **Technical Challenges:**
  - Docker Compose DB startup timing (solved with healthcheck).
  - Thymeleaf layout path resolution in JAR (solved by adjusting path).
  - Docker command variations (`compose` vs `docker-compose`).
  - Maven build dependency on database for tests.
- **Limitations:**
  - Minimal test coverage.
  - Advanced features (Microservices, Kafka, etc.) not implemented.

---

**Slide 12: Conclusion & Future Work**

- **Title:** Conclusion & Future Work
- **Summary:** Delivered a functional Healthcare CRM MVP demonstrating core Spring Boot features, security, web UI, REST APIs, and Docker deployment.
- **Key Learnings:** Practical experience with Spring Boot ecosystem, Thymeleaf, REST, Docker/Compose, and troubleshooting deployment issues.
- **Future Work Ideas:** Comprehensive testing, enhanced search/filtering, production hardening, advanced feature exploration.

---

**Slide 13: Q & A**

- **Title:** Questions?
- **(Optional: Repository Link)**
- Thank You.
