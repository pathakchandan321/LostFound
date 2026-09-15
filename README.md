# Lost and Found System for Students

A Spring Boot + Thymeleaf application for students to report lost items, post found items with images, search campus reports, claim items, and let an admin approve or reject claims.

## Tech Stack

- Java 25 or newer, Spring Boot 4
- Spring MVC REST APIs and Thymeleaf UI
- Spring Security with BCrypt password hashing
- Spring Data JPA + Hibernate
- H2 by default, with an optional MySQL profile
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

## Run

Open a new PowerShell terminal after setting up Java, then run:

```powershell
.\mvnw.cmd spring-boot:run
```

The default configuration uses an in-memory H2 database, so no database server or credentials are required. Open `http://localhost:8080/home` after startup.

To build an executable JAR:

```powershell
.\mvnw.cmd package
java -jar target\lostFound-0.0.1-SNAPSHOT.jar
```

## MySQL (optional)

Create the database, supply credentials, and enable the profile:

```sql
CREATE DATABASE lost_found_db;
```

```powershell
$env:DB_PASSWORD="your_mysql_password"
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Sample Accounts

The app seeds these accounts on first startup:

- Admin: `admin` / `admin123`
- Student: `student` / `student123`

## Main REST APIs

- `POST /api/auth/register`
- `GET /api/lost-items`
- `POST /api/lost-items` with multipart form fields `itemName`, `description`, `location`, optional `image`
- `GET /api/found-items`
- `POST /api/found-items` with multipart form fields `itemName`, `description`, `location`, optional `image`
- `POST /api/claims`
- `POST /api/admin/claims/{id}/approve`
- `POST /api/admin/claims/{id}/reject`
- `POST /api/chatbot`

## Uploads

Images are stored in the local `uploads/` folder and served from `/uploads/{fileName}`.