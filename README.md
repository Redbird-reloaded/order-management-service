# Order Management Service

Spring Boot application implementing order management workflows.

## Features

- Create Order
- Retrieve Order By Id
- List Orders
- Filter Orders By Status
- Update Order Status
- Cancel Order (only when PENDING)
- Flyway Database Migrations
- PostgreSQL
- OpenAPI / Swagger
- Scheduler for automated status transitions

## Tech Stack

- Java 21
- Spring Boot 3.3
- Spring Data JPA
- PostgreSQL
- Flyway
- OpenAPI
- Maven

## Run

docker run --name postgres \
-e POSTGRES_DB=order_processing \
-e POSTGRES_USER=order_user \
-e POSTGRES_PASSWORD=order_password \
-p 5432:5432 \
-d postgres:17

mvn spring-boot:run

Swagger:
http://localhost:8080/swagger-ui.html