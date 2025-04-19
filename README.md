# Healthcare CRM - J2EE Business Components Project

**Project Goal:** A CRM system for healthcare centers to manage customer records, insurance details, and employee task assignments using Spring Boot, Thymeleaf, and Spring Security.

**Group Members:**

- John Shaize
- Darshan Kiran Upadhyay
- Gaurang Hareshkumar Dhameliya
- Ramees Karolil Rasheed

---

## Technology Stack

- **Backend:** Java 21, Spring Boot 3.4.2, Spring MVC, Spring Data JPA, Spring Security
- **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
- **Database:** MySQL
- **Build Tool:** Maven
- **ORM:** Hibernate (via Spring Data JPA)
- **Validation:** Jakarta Bean Validation
- **Other:** Lombok

---

## Prerequisites

- **Java Development Kit (JDK):** Version 21 or later.
- **Apache Maven:** Version 3.6+ (for building the project).
- **MySQL Server:** Running instance (tested with MySQL 8).
- **IDE:** An IDE that supports Spring Boot development (e.g., IntelliJ IDEA, Eclipse with STS, VS Code with Java extensions).

---

## Setup and Running Instructions

1.  **Clone the Repository:**

    ```bash
    git clone <your-repository-url>
    cd healthcarecrm # Or your project's root directory
    ```

2.  **Database Setup:**

    - Ensure your MySQL server is running.
    - Create a database named `healthcarecrm`. You can use a MySQL client or command line:
      ```sql
      CREATE DATABASE healthcarecrm;
      ```
    - Configure database credentials:
      - Open the `src/main/resources/application.properties` file.
      - Verify/Update the following properties to match your MySQL setup:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/healthcarecrm
        spring.datasource.username=root  # Replace with your MySQL username
        spring.datasource.password=root  # Replace with your MySQL password
        ```
      - The property `spring.jpa.hibernate.ddl-auto=update` allows Hibernate to automatically create/update tables based on your entity classes the first time the application runs or when entities change.

3.  **Build the Project (Optional - IDE often handles this):**

    - Navigate to the project's root directory (containing `pom.xml`) in your terminal.
    - Run Maven build:
      ```bash
      mvn clean install
      ```

4.  **Run the Application:**

    - **Using an IDE:** Most IDEs (like IntelliJ, STS) allow you to right-click the `HealthcarecrmApplication.java` file and select "Run" or "Debug".
    - **Using Maven:** In the terminal (from the project root):
      ```bash
      mvn spring-boot:run
      ```
    - **Using the JAR (after building):**
      ```bash
      java -jar target/healthcarecrm-0.0.1-SNAPSHOT.jar
      ```

5.  **Access the Application:**

    - Open your web browser and navigate to `http://localhost:8080`.
    - You should see the login page.

6.  **Default Login Credentials:**
    - The application initializes a default **Admin** user if no admins exist in the database:
      - **Email:** `admin@clinic.com`
      - **Password:** `password123`
    - If the database is empty, it also creates mock **Employee** users:
      - **Emails:** `alice.j@clinic.com`, `bob.w@clinic.com`, etc.
      - **Password (for all mock employees):** `password123`

---

## Project Structure

- `src/main/java`: Contains the main Java source code (Controllers, Services, Repositories, Models, Config).
- `src/main/resources`: Contains configuration files (`application.properties`), static assets (`static/css`, `static/images`), and Thymeleaf templates (`templates/`).
- `pom.xml`: Maven project configuration file.

---
