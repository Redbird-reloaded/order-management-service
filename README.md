# Order Management Service

## Overview

Order Management Service is a Spring Boot 3 application that provides APIs for creating, retrieving, updating, listing, and cancelling customer orders.

The application follows Hexagonal Architecture (Ports and Adapters) to ensure separation of concerns, maintainability, and testability.

---

## Technology Stack

* Java 21
* Spring Boot 3.3.x
* Maven
* Spring Data JPA
* PostgreSQL
* Flyway
* Lombok
* Spring Validation
* OpenAPI / Swagger
* JUnit 5
* Mockito
* Testcontainers
* Docker

---

## Features

### Create Order

Customers can create orders containing one or more order items.

### Retrieve Order Details

Retrieve complete order details using Order ID.

### List Orders

Retrieve all orders.

Optional filtering supported by status.

Example:

GET /api/v1/orders?status=PENDING

### Update Order Status

Order status can be updated using a dedicated API.

Supported statuses:

* PENDING
* PROCESSING
* SHIPPED
* DELIVERED
* CANCELLED

### Cancel Order

Orders can be cancelled only when they are in PENDING status.

Cancellation is implemented as a status transition to CANCELLED rather than physical deletion.

This preserves auditability and operational traceability.

### Background Scheduler

A scheduled job automatically updates PENDING orders to PROCESSING every 5 minutes.

---

# Architecture

The application follows Hexagonal Architecture.

```text
                    +------------------+
                    |     Controller   |
                    +------------------+
                              |
                              v
                    +------------------+
                    | Application      |
                    | Services         |
                    +------------------+
                              |
                              v
                    +------------------+
                    | Domain Model     |
                    | Business Rules   |
                    +------------------+
                              |
                              v
                    +------------------+
                    | Repository Port  |
                    +------------------+
                              |
                              v
                    +------------------+
                    | Infrastructure   |
                    | JPA / PostgreSQL |
                    +------------------+
```

---

# Package Structure

```text
org.vivek.orderprocessing

├── controller
│
├── domain
│   ├── model
│   └── repository
│
├── service
│   ├── impl
│   └── exception
│
├── infrastructure
│   ├── config
│   └── persistence
│
├── scheduler
│
└── resources
```

---

# High Level Design

## Create Order Flow

```text
Client
   |
POST /orders
   |
Controller
   |
CreateOrderService
   |
Order Domain
   |
Repository Port
   |
Persistence Adapter
   |
PostgreSQL
```

---

## Cancel Order Flow

```text
Client
   |
PATCH /orders/{id}/cancel
   |
Controller
   |
CancelOrderService
   |
Order.cancel()
   |
Business Validation
   |
Repository
   |
Database
```

---

## Scheduler Flow

```text
Spring Scheduler
        |
        v
UpdatePendingOrdersService
        |
Find PENDING Orders
        |
Transition to PROCESSING
        |
Save Orders
```

---

# Domain Rules

## Order Status Lifecycle

```text
PENDING
   |
   v
PROCESSING
   |
   v
SHIPPED
   |
   v
DELIVERED
```

Cancellation:

```text
PENDING
   |
   v
CANCELLED
```

Rules:

* Only PENDING orders can be cancelled.
* CANCELLED orders cannot be updated.
* DELIVERED orders are immutable.
* Scheduler updates only PENDING orders.

---

# Database Design

## orders

| Column         | Type      |
| -------------- | --------- |
| id             | UUID      |
| customer_name  | VARCHAR   |
| customer_email | VARCHAR   |
| status         | VARCHAR   |
| created_at     | TIMESTAMP |
| updated_at     | TIMESTAMP |

---

## order_items

| Column       | Type    |
| ------------ | ------- |
| id           | UUID    |
| order_id     | UUID    |
| product_id   | VARCHAR |
| product_name | VARCHAR |
| quantity     | INTEGER |
| price        | NUMERIC |

Relationship:

```text
Order
  |
  | 1
  |
  | N
OrderItem
```

---

# API Endpoints

## Create Order

POST

```text
/api/v1/orders
```

---

## Get Order

GET

```text
/api/v1/orders/{orderId}
```

---

## List Orders

GET

```text
/api/v1/orders
```

Optional:

```text
/api/v1/orders?status=PENDING
```

---

## Update Status

PATCH

```text
/api/v1/orders/{orderId}/status
```

Request:

```json
{
  "status": "SHIPPED"
}
```

---

## Cancel Order

PATCH

```text
/api/v1/orders/{orderId}/cancel
```

---

# OpenAPI Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

---

# Running Locally

## Start PostgreSQL

```bash
docker compose up -d
```

---

## Run Application

```bash
mvn spring-boot:run
```

---

## Execute Tests

```bash
mvn test
```

---

# Design Decisions

## Why Hexagonal Architecture?

Benefits:

* Clear separation between business logic and infrastructure.
* Easy to test.
* Database can be replaced without impacting domain logic.
* Improves maintainability.

---

## Why Soft Delete?

The assignment requires order cancellation.

Instead of physically deleting records, cancellation is represented through a state transition to CANCELLED.

Advantages:

* Preserves audit history.
* Supports future reporting.
* Enables operational investigations.
* Aligns with real-world order management systems.

---

# AI-Assisted Development

This assignment was developed using AI-assisted engineering workflows (Cursor/Codex and ChatGPT) alongside traditional software engineering practices.

The objective was not to delegate engineering decisions to AI, but to accelerate development while maintaining full ownership of architecture, business rules, testing, code quality, and implementation decisions.

## How AI Was Used

### Solution Design & Architecture Review

AI was used as an architectural sounding board to evaluate and refine:

* Hexagonal Architecture (Ports & Adapters)
* Domain model boundaries
* Service responsibilities
* Repository abstractions
* API design approaches
* Database migration strategy

### Development Productivity

AI was leveraged to accelerate implementation of repetitive and boilerplate-heavy tasks, including:

* Project scaffolding
* Maven and dependency setup
* DTO generation
* Entity mappings
* Repository adapters
* Test scaffolding
* OpenAPI documentation
* Docker configuration

This reduced implementation time and allowed greater focus on business logic, domain modeling, and overall solution quality.

### Code Review & Refactoring

AI was used iteratively throughout development to review implementation choices, identify potential design concerns, and suggest refactoring opportunities.

Generated code was treated as a starting point rather than production-ready output and was manually reviewed before being incorporated into the solution.

---

## Engineering Ownership

While AI accelerated implementation and documentation, all architectural decisions, business-rule enforcement, API design choices, testing strategy, and final code reviews remained human-driven.

Key engineering decisions included:

* Keeping business rules within the domain model rather than controllers or infrastructure layers
* Designing explicit order lifecycle transitions and validation rules
* Implementing centralized exception handling using domain-specific exceptions
* Preserving auditability through status-based order cancellation
* Maintaining clear separation of concerns through Hexagonal Architecture

AI was used as an engineering productivity tool rather than a replacement for technical judgment.

---

## Outcome

AI significantly improved development velocity, documentation quality, and test creation while allowing engineering effort to remain focused on system design, maintainability, correctness, and business-rule enforcement.

The final solution represents a human-reviewed and manually validated implementation where AI served as a productivity accelerator rather than a replacement for engineering judgment.

---

# Future Improvements

* Authentication & Authorization
* Event-driven processing using Kafka
* Optimistic locking
* Audit history table
* Order status transition engine
* Metrics and observability
* CI/CD pipeline
* Kubernetes deployment
* Redis caching
* API rate limiting

---

# Assumptions

* Product catalog validation is out of scope.
* Inventory validation is out of scope.
* Payment processing is out of scope.
* Single service deployment model.
* PostgreSQL is the source of truth.
