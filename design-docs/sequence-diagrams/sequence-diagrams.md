### **Possible User Flows**

1. **User Authentication Flow (Login)**

   - A user (Admin or Employee) submits login credentials.
   - The system validates the credentials via the AuthController, AuthService, and UserRepository.
   - The system returns either a success (with a session/token) or an error response.

2. **Customer Management Flow (Creation, Update, Deletion)**

   - **Customer Creation Flow (Admin):**
     - An Admin submits a new customer's details.
     - The CustomerController passes the details to CustomerService.
     - CustomerService calls CustomerRepository to persist the customer.
     - The system returns a confirmation.
   - **Customer Update & Deletion Flows** follow similar patterns with different endpoints.

3. **Task Management Flow**
   - **Task Assignment Flow (Admin):**
     - An Admin assigns a new task to an employee.
     - The TaskController sends the request to TaskService.
     - TaskService calls TaskRepository to persist the task.
     - A confirmation is returned.
   - **Task Update Flow (Employee):**
     - An Employee updates the status of an assigned task.
     - The TaskController receives the update and delegates it to TaskService.
     - TaskService updates the task status in TaskRepository.
     - The updated task information is returned.
   - **Fetch Tasks Flow (Employee):**
     - An Employee requests a list of their assigned tasks.
     - The TaskController retrieves tasks via TaskService and TaskRepository.

---

### **PlantUML Sequence Diagrams**

#### **1. User Authentication Flow**

```plantuml
@startuml
actor User
participant "AuthController" as AC
participant "AuthService" as AS
participant "UserRepository" as UR

User -> AC: POST /api/auth/login {username, password}
AC -> AS: authenticate(username, password)
AS -> UR: findByUsername(username)
UR --> AS: user object (or null)
alt Valid credentials
  AS --> AC: Authenticated user object (with token/session)
  AC -> User: 200 OK (Login Successful)
else Invalid credentials
  AS --> AC: Authentication Failed
  AC -> User: 401 Unauthorized (Error message)
end
@enduml
```

## ![user authentication flow.png](auth-flow.png "User Authentication Flow")

#### **2. Customer Creation Flow (Admin)**

```plantuml
@startuml
actor Admin
participant "CustomerController" as CC
participant "CustomerService" as CS
participant "CustomerRepository" as CR

Admin -> CC: POST /api/customers {customer details}
CC -> CS: createCustomer(customer)
CS -> CR: save(customer)
CR --> CS: persisted Customer object
CS --> CC: success response (customer saved)
CC -> Admin: 200 OK (Customer Created)
@enduml
```

## ![customer creation flow.png](customer-create-flow.png "Customer Creation Flow ")

#### **3. Task Assignment Flow (Admin assigns a Task)**

```plantuml
@startuml
actor Admin
participant "TaskController" as TC
participant "TaskService" as TS
participant "TaskRepository" as TR

Admin -> TC: POST /api/tasks {task details, assignedTo}
TC -> TS: createTask(task)
TS -> TR: save(task)
TR --> TS: persisted Task object
TS --> TC: success response (task saved)
TC -> Admin: 200 OK (Task Assigned)
@enduml
```

## ![Task Assignment Flow.png](task-assign-flow.png "Task Assignment Flow")

---

#### **4. Task Update Flow (Employee updates Task Status)**

```plantuml
@startuml
actor Employee
participant "TaskController" as TC
participant "TaskService" as TS
participant "TaskRepository" as TR

Employee -> TC: PUT /api/tasks/{id} {new status}
TC -> TS: updateTaskStatus(id, newStatus)
TS -> TR: update(task status in DB)
TR --> TS: updated Task object
TS --> TC: success response (updated task)
TC -> Employee: 200 OK (Task Updated)
@enduml
```

## ![Task Update Flow.png](task-update-flow.png "Task Update Flow")

---
