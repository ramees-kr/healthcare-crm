workspace "Healthcare CRM Application" "Architecture diagram for Healthcare CRM Spring Boot app with Docker deployment." {

  model {
    webUser   = person "Web User"   "Admin or Employee using a browser"
    apiClient = person "API Client" "Tool or developer accessing REST APIs"

    healthcareCRM = softwareSystem "Healthcare CRM" "Spring Boot‑based CRM for healthcare centers" {

      app = container "Spring Boot App" {
        description "Embedded Tomcat running the Spring Boot application"
        technology  "Java 21, Spring Boot 3.3.0"
        tags        "Application"

        webController       = component "Spring MVC Controllers"      "Handles web routes & Thymeleaf rendering"      "Spring MVC"
        restController      = component "REST Controllers"            "Handles /api/** endpoints"                   "Spring MVC REST"
        thymeleafEngine     = component "Thymeleaf Template Engine"   "Renders server‑side HTML views"            "Thymeleaf"
        staticAssets        = component "Static Assets"               "Serves CSS, JS, and images"                "Web Resources"
        swaggerUI           = component "Swagger UI"                  "Interactive API docs at /swagger-ui.html"  "Springdoc OpenAPI"
        securityFilterChain = component "Security Filter Chain"       "Secures HTTP requests"                     "Spring Security"
        userDetailsService  = component "UserDetailsServiceImpl"      "Loads users for authentication"            "Spring Security"
        passwordEncoder     = component "BCryptPasswordEncoder"       "Hashes passwords"                          "Spring Security"
        serviceLayer        = component "Service Layer"               "Business logic (CustomerService, TaskService)" "Java Classes"
        repoLayer           = component "Repository Layer"            "Data access via JPA/Hibernate"             "Spring Data JPA"
        domainModel         = component "Domain Model"                "JPA entities: Customer, Employee, Task…"     "JPA Entities"
        dataInitializer     = component "DataInitializer"             "Seeds mock data on startup"                "Spring Boot Runner"
      }

      db = container "MySQL Database" {
        description "Stores the healthcarecrm schema data"
        technology  "MySQL 8"
        tags        "Database"
      }
    }

    webUser    -> app             "Uses via browser"          "HTTPS"
    apiClient  -> restController  "Calls REST APIs"           "HTTPS, JSON"

    webController     -> thymeleafEngine     "Renders HTML views"
    webController     -> serviceLayer        "Invokes business logic"
    restController    -> serviceLayer        "Invokes business logic"
    serviceLayer      -> repoLayer           "Performs CRUD operations"
    securityFilterChain -> userDetailsService  "Authenticates users"
    userDetailsService  -> repoLayer           "Loads user data"
    securityFilterChain -> webController       "Secures web UI routes"
    securityFilterChain -> restController      "Secures API endpoints"
    dataInitializer     -> serviceLayer        "Initializes mock data on startup"

    repoLayer -> db            "Reads/Writes data"     "JDBC"
    app       -> db            "Connects to database"  "JDBC"


    dockerCompose = deploymentEnvironment "Docker Compose" {

      dockerHost = deploymentNode "Docker Host" "Your laptop or desktop running Docker Engine" {
        tags "Container Orchestration"

        appInstance = containerInstance app {
          tags "Docker Container"
          healthCheck url "http://localhost:8080/actuator/health" 60 10
        }
        dbInstance = containerInstance db {
          tags "Docker Container"
        }
      }
    }
  }

  views {
    systemContext healthcareCRM "SystemContext" {
      include *
      autolayout lr
      title "System Context: Healthcare CRM interacting with Users"
      description "Shows the Healthcare CRM system and its interactions with external users and API clients."
    }

    container healthcareCRM "Containers" {
      include *
      include webUser
      include apiClient
      autolayout lr
      title "Container Diagram: Spring Boot App & MySQL Database"
      description "High-level containers and how they interact."
    }

    component app "Components" {
      include *
      include webUser
      include apiClient
      include db
      autolayout lr
      title "Component Diagram: Inside the Spring Boot Application"
      description "Key components and their relationships."
    }

    deployment healthcareCRM dockerCompose "DockerDeployment" {
      include *
      autolayout lr
      title "Deployment View: Docker Compose"
      description "Shows how containers run on your Docker host."
    }

    styles {
      element "Person" {
        shape Person
        background #08427b
        color #ffffff
      }
      element "Software System" {
        shape RoundedBox
        background #1168bd
        color #ffffff
      }
      element "Container" {
        shape RoundedBox
        background #438dd5
        color #ffffff
      }
      element "Component" {
        shape Component
        background #85bbf0
        color #000000
      }
      element "Database" {
        shape Cylinder
        background #ff7700
        color #ffffff
      }
      element "Application" {
        background #22aa22
        color #ffffff
      }
      element "Deployment Node" {
        shape RoundedBox
        background #ffffff
        color #000000
        stroke #aaaaaa
      }
      element "Container Instance" {
        shape Hexagon
        background #cccccc
        color #000000
      }
      relationship "Relationship" {
        routing Orthogonal
      }
    }
  }
}