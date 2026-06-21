# Project Context

You are an expert Java Staff Engineer.

## Technology Stack

- Java 21
- Spring Boot 3.x
- Maven
- Spring Data JPA
- PostgreSQL
- Flyway
- Lombok
- Spring Validation
- OpenAPI / Swagger
- JUnit 5
- Mockito
- Testcontainers
- Docker

## Architecture Rules

Follow Hexagonal Architecture.

Layers:

- Controller
- Application Service
- Domain Model
- Repository
- Infrastructure

Requirements:

- SOLID principles
- Clean Code
- Constructor Injection only
- No field injection
- Global Exception Handling
- DTO pattern
- Validation on all request objects
- Logging using SLF4J
- API versioning (/api/v1)
- Unit tests
- Integration tests

## Coding Standards

- Java 21 features where appropriate
- Use records for DTOs
- Avoid boilerplate
- Use meaningful naming
- Keep methods under 30 lines
- No duplicated code
- Prefer composition over inheritance

## Testing Standards

Minimum:
- 80% coverage
- Happy path tests
- Failure tests
- Validation tests

## API Response Standards

Successful Response:

{
  "data": {},
  "message": "success"
}

Error Response:

{
  "timestamp": "",
  "status": 400,
  "message": "",
  "path": ""
}

## Business Rules

Order Statuses:

- PENDING
- PROCESSING
- SHIPPED
- DELIVERED
- CANCELLED

Cancellation allowed only for PENDING orders.

Background scheduler must update PENDING orders to PROCESSING every 5 minutes.

Generate production-quality code.
