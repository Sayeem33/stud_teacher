# Student Teacher Management System

A Spring Boot REST API for managing students, teachers, courses, and departments with JWT authentication and role-based access control.

## Features

- **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (ADMIN, TEACHER, STUDENT)
  
- **Entities & Relationships**
  - Department ↔ Student (One-to-Many)
  - Department ↔ Teacher (One-to-Many)
  - Department ↔ Course (One-to-Many)
  - Teacher ↔ Course (One-to-Many)
  - Student ↔ Course (Many-to-Many)

- **CRUD Operations** with role-based permissions
- **Database Migration** using Flyway
- **Docker** containerization

## Tech Stack

- Java 17
- Spring Boot 3.2.2
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker & Docker Compose
- Lombok

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for containerized deployment)

### Running with Docker (Recommended)

```bash
# Start the application with PostgreSQL
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop the application
docker-compose down
```

### Running Locally (IntelliJ IDEA)

1. Start PostgreSQL (using Docker):
```bash
docker-compose up -d postgres
```

2. Open the project in IntelliJ IDEA
3. Run `StudTeacherApplication.java`

### Running with Maven

```bash
# Start PostgreSQL first
docker-compose up -d postgres

# Run the application
./mvnw spring-boot:run
```

## API Endpoints

### Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login | Public |

### Departments

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/departments` | Get all departments | Authenticated |
| GET | `/api/departments/{id}` | Get department by ID | Authenticated |
| POST | `/api/departments` | Create department | ADMIN, TEACHER |
| PUT | `/api/departments/{id}` | Update department | ADMIN, TEACHER |
| DELETE | `/api/departments/{id}` | Delete department | ADMIN, TEACHER |

### Courses

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/courses` | Get all courses | Authenticated |
| GET | `/api/courses/{id}` | Get course by ID | Authenticated |
| POST | `/api/courses` | Create course | ADMIN, TEACHER |
| PUT | `/api/courses/{id}` | Update course | ADMIN, TEACHER |
| DELETE | `/api/courses/{id}` | Delete course | ADMIN |


### Teachers

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/teachers/me` | Get own profile | TEACHER |
| PUT | `/api/teachers/me` | Update own profile | TEACHER |
| GET | `/api/teachers/me/courses` | Get assigned courses | TEACHER |
| GET | `/api/teachers` | Get all teachers | ADMIN, TEACHER |
| GET | `/api/teachers/{id}` | Get teacher by ID | ADMIN, TEACHER |
| PUT | `/api/teachers/{id}` | Update teacher | ADMIN |
| DELETE | `/api/teachers/{id}` | Delete teacher | ADMIN |

## Sample API Requests

### Register a Student

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "STUDENT",
    "departmentId": 1
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

### Get Courses (with JWT token)

```bash
curl -X GET http://localhost:8080/api/courses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Testing

The project includes a comprehensive test suite with 60+ tests covering:

- **Service Layer Tests** - Business logic validation (5 test classes)
- **Controller Tests** - REST endpoint testing (5 test classes)
- **Repository Tests** - Data access layer testing (5 test classes)
- **Security Tests** - JWT authentication validation (1 test class)

Run tests with:

```bash
./mvnw test -Dspring.profiles.active=test
```

### CI/CD Pipeline

GitHub Actions workflow automatically runs on every push to `main` and pull requests:

- Compiles the project
- Runs all unit tests
- Packages the application

Check the workflow status in the **Actions** tab on GitHub.

## Sample Test Credentials

After running the migrations, you can use these credentials:

| Email | Password | Role |
|-------|----------|------|
| admin@school.com | admin123 | ADMIN |
| john.doe@school.com | admin123 | TEACHER |
| jane.smith@school.com | admin123 | TEACHER |
| alice.johnson@school.com | admin123 | STUDENT |
| bob.wilson@school.com | admin123 | STUDENT |
| charlie.brown@school.com | admin123 | STUDENT |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| DB_HOST | localhost | Database host |
| DB_PORT | 5432 | Database port |
| DB_NAME | student_teacher_db | Database name |
| DB_USER | postgres | Database username |
| DB_PASSWORD | postgres | Database password |
| JWT_SECRET | (base64 encoded) | JWT signing key |
| JWT_EXPIRATION | 86400000 | Token expiration (24h) |
| SERVER_PORT | 8080 | Application port |

## Project Structure

```
src/main/java/com/example/stud_teacher/
├── config/              # Security and app configuration
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA entities
├── exception/           # Custom exceptions
├── repository/          # JPA repositories
├── security/            # JWT authentication
└── service/             # Business logic

src/main/resources/
├── db/migration/        # Flyway migrations
└── application.yaml     # App configuration
```

## License

This project is for educational purposes.
