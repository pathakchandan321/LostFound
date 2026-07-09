# Lost and Found System for Students

A Spring Boot + Thymeleaf application for students to report lost items, post found items with images, search campus reports, claim items, and let an admin approve or reject claims.

## Tech Stack

- Java 21, Spring Boot 4
- Spring MVC REST APIs and Thymeleaf UI
- Spring Security with BCrypt password hashing
- Spring Data JPA + Hibernate
- MySQL
- Swagger UI through springdoc-openapi

## Features

- Student registration and login
- Post lost items
- Post found items with image upload
- View and search lost/found items by title, description, or location
- Claim item workflow
- Admin dashboard for claims, users, and posts
- Rule-based AI chatbot for FAQs
- Similar item auto-suggestion by title/location match
- Status tracking: `LOST`, `FOUND`, `MATCHED`, `CLAIMED`, `RETURNED`
- Global REST exception handling and validation DTOs

## Setup

1. Create the database:

```sql
CREATE DATABASE lost_found_db;
```

2. Configure database credentials in `src/main/resources/application.properties`, or set `DB_PASSWORD`:

```powershell
$env:DB_PASSWORD="your_mysql_password"
```

3. Run the app:

```powershell
.\mvnw.cmd spring-boot:run
```

4. Open:

- UI: `http://localhost:8080/home`
- Swagger: `http://localhost:8080/swagger-ui.html`

## Sample Accounts

The app seeds these accounts on first startup:

- Admin: `admin` / `admin123`
- Student: `student` / `student123`

## Main REST APIs

- `POST /api/auth/register`
- `GET /api/lost-items`
- `POST /api/lost-items` with multipart form fields `itemName`, `description`, `location`, optional `image`
- `GET /api/found-items`
- `POST /api/found-items` with multipart form fields `itemName`, `description`, `location`, `image`
- `POST /api/claims`
- `POST /api/admin/claims/{id}/approve`
- `POST /api/admin/claims/{id}/reject`
- `POST /api/chatbot`

## Uploads

Images are stored in the local `uploads/` folder and served from `/uploads/{fileName}`.

## Database Schema

Hibernate can create/update tables automatically through `spring.jpa.hibernate.ddl-auto=update`. A reference schema is also provided in `src/main/resources/schema.sql`.
