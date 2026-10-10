# Defense Logistics Management System

A comprehensive 3-microservice backend for a Defense Logistics and Non-Combat Resource Management System.

Built with **Java 21**, **Spring Boot 3.2**, **Spring Data JPA**, **Flyway**, and **JWT Security**.

## Architecture

This project is divided into three distinct Spring Boot microservices, each with its own database schema:

1. **Resource Service (`:9191`)**: Manages personnel, units, assets, vehicles, and maintenance workflows.
2. **Inventory Service (`:9192`)**: Manages supply catalogue, stock levels, stock movements, and issue records with optimistic locking.
3. **Request Service (`:9193`)**: Handles logistics requests, approvals, allocations, and coordinates cross-service data via `RestClient`.

## Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8.x

## Database Setup

By default, the applications are configured to connect to `localhost:3306` with the username `root` and password `Divyansh@12`. 

You do **not** need to manually create the databases. The JDBC URLs have `createDatabaseIfNotExist=true`, meaning the following schemas will be auto-created on startup:
- `resource_db`
- `inventory_db`
- `request_db`

**Flyway** is integrated into every service and will automatically generate the required tables during boot.

## How to Run Locally

You must start the services from their respective module directories. You can run them in three separate terminal windows.

### Terminal 1: Resource Service
```bash
cd resource-service
mvn spring-boot:run
```

### Terminal 2: Inventory Service
```bash
cd inventory-service
mvn spring-boot:run
```

### Terminal 3: Request Service
```bash
cd request-service
mvn spring-boot:run
```

## API Documentation (Swagger)

Each service has its own Swagger UI page for easy endpoint testing.

- Resource Service APIs: [http://localhost:9191/swagger-ui.html](http://localhost:9191/swagger-ui.html)
- Inventory Service APIs: [http://localhost:9192/swagger-ui.html](http://localhost:9192/swagger-ui.html)
- Request Service APIs: [http://localhost:9193/swagger-ui.html](http://localhost:9193/swagger-ui.html)

## Security & Authentication

Endpoints are secured via JWT and require a Bearer token in the `Authorization` header.
For local testing, the `JwtAuthFilter` is configured in "mock mode".
- Passing a token containing the word `admin` (e.g., `Bearer my_admin_token`) will grant you `ROLE_ADMIN`.
- Passing any other mock token (e.g., `Bearer user_token`) will grant the module's officer role (e.g., `ROLE_LOGISTICS_OFFICER` or `ROLE_INVENTORY_OFFICER`).

## Key Features Implemented

*   **Concurrency Control**: The Inventory Service uses `@Version` for pessimistic/optimistic locking, preventing double-spending stock.
*   **Cross-Service Validation**: The Request Service uses `RestClient` to synchronously validate `unitId` from the Resource Service and to lock/issue supplies from the Inventory Service during allocation.
*   **Centralized Exception Handling**: `@RestControllerAdvice` provides clean, standardized JSON errors for bad inputs, constraints, and 404s.
