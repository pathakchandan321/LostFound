# FindX - Lost & Found Platform

FindX is a secure, web-based lost-and-found platform for colleges and campuses. Users can report lost belongings, register found items, search their reports, submit claims, and receive notifications when potentially matching reports are detected. Administrators can review claims and manage platform data from a dedicated dashboard.

## Highlights

- Student registration and secure login
- Lost-item and found-item reporting
- Optional image uploads for items and user profiles
- Automatic matching by item name and location
- Notifications for potentially matching reports
- Claim submission with verification messages
- Admin approval and rejection workflow
- Returned-item success stories with optional public photos
- Item, claim, and user status tracking
- Rule-based FAQ chatbot
- Server-rendered web interface with Thymeleaf
- REST API documented through OpenAPI and Swagger UI
- Global exception handling and request validation

## Technology Stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.0.2 |
| Web | Spring MVC, Thymeleaf |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Security | Spring Security, BCrypt |
| API documentation | Springdoc OpenAPI / Swagger UI |
| Build tool | Maven |
| File storage | Local `uploads/` directory |

## Requirements

- Java 21 or a compatible newer JDK
- MySQL 8 or later
- Git
- Internet access on the first build so Maven can download dependencies

The project includes the Maven Wrapper, so a separate Maven installation is not required.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/pathakchandan321/LostFound.git
cd LostFound
```

### 2. Create the database

Create the application database in MySQL:

```sql
CREATE DATABASE lost_found_db;
```

The application uses Hibernate with `ddl-auto=update`, so the required tables are created or updated automatically when the application starts. A reference schema is available at [`src/main/resources/schema.sql`](src/main/resources/schema.sql).

### 3. Configure database credentials

Update [`src/main/resources/application.properties`](src/main/resources/application.properties) with your local MySQL username and password.

For local development, the password can also be supplied through the `DB_PASSWORD` environment variable:

PowerShell:

```powershell
$env:DB_PASSWORD = "your_mysql_password"
```

Command Prompt:

```bat
set DB_PASSWORD=your_mysql_password
```

Do not commit real credentials to the repository.

### 4. Start the application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux or macOS:

```bash
./mvnw spring-boot:run
```

The application starts on port `8080`.

### 5. Open the application

- Web application: [http://localhost:8080/home](http://localhost:8080/home)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI document: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Demo Accounts

The application seeds demo accounts on first startup if they do not already exist:

| Role | Username | Password |
| --- | --- | --- |
| Administrator | `admin` | `admin123` |
| Student | `student` | `student123` |

Change or remove these credentials before deploying to a shared or production environment.

## Application Workflow

### Reporting an item

1. A signed-in user submits a lost or found item report.
2. The report is saved with the current date and the appropriate initial status.
3. An optional image is stored in the local `uploads/` directory.
4. Existing reports are searched for matching item names or locations.
5. Potential matches receive notifications and the report status becomes `MATCHED`.

### Claiming an item

1. The owner of a lost report selects a corresponding found report.
2. The user submits a claim with an optional verification message.
3. Both item records move to `CLAIMED` and the claim is created as `PENDING`.
4. An administrator reviews the claim.
5. Approved claims move both items to `RETURNED`; rejected claims return them to `MATCHED`.
6. After approval, the claimant collects the item from the admin; the admin marks the claim as handed over, which removes the item from matching and unlocks the found-item reporter's optional photo and story for the landing page.

## Statuses

### Item statuses

`LOST` | `FOUND` | `MATCHED` | `CLAIMED` | `RETURNED`

### Claim statuses

`PENDING` | `APPROVED` | `REJECTED`

## REST API

The REST API is rooted at `/api`. Protected endpoints require an authenticated session unless stated otherwise.

### Authentication and registration

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/login` | Show the login page |
| `POST` | `/api/login` | Authenticate a user |
| `POST` | `/api/logout` | End the current session |
| `POST` | `/api/auth/register` | Register a student account |

### Items

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/lost-items` | List the signed-in user's lost reports |
| `POST` | `/api/lost-items` | Create a lost report |
| `GET` | `/api/found-items` | List the signed-in user's found reports |
| `POST` | `/api/found-items` | Create a found report |

The item creation endpoints consume `multipart/form-data` with these fields:

- `itemName`
- `description`
- `location`
- `image` (optional)

### Claims and administration

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/claims` | List the current user's claims |
| `POST` | `/api/claims` | Submit a claim |
| `POST` | `/api/admin/claims/{id}/approve` | Approve a claim; admin role required |
| `POST` | `/api/admin/claims/{id}/reject` | Reject a claim; admin role required |
| `GET` | `/api/admin/users` | List users; admin role required |

### Chatbot

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/chatbot` | Get an answer to a supported FAQ |

Example request:

```json
{
  "message": "How do I claim an item?"
}
```

## Web Routes

| Route | Purpose |
| --- | --- |
| `/home` | Landing page and chatbot |
| `/my-reports` | Current user's reports, claims, and notifications |
| `/profile` | Profile details and profile-image upload |
| `/User/report-lost` | Lost-item report form |
| `/User/report-found` | Found-item report form |
| `/claims` | Claim area |
| `/admin` | Administrator dashboard |

## Security

- Passwords are stored using BCrypt hashing.
- Administrative routes under `/admin/**` and `/api/admin/**` require the `ADMIN` role.
- Authenticated sessions are required for user data and reporting operations.
- Uploaded file names are normalized before storage.
- Database credentials should be provided through local configuration or environment variables, never committed to source control.

## File Uploads

Uploaded item and profile images are stored in the local `uploads/` directory. The configured limits are:

- Maximum file size: `20 MB`
- Maximum request size: `20 MB`

The upload directory is controlled by the `file.upload-dir` property. Ensure the application process has permission to create and write to this directory.

## Project Structure

```text
src/
├── main/
│   ├── java/com/lostFound/lostFound/
│   │   ├── configuration/   # Security, startup data, web and upload configuration
│   │   ├── controller/      # MVC controllers and REST API controllers
│   │   ├── dto/             # API request and response records
│   │   ├── Entity/          # JPA entities
│   │   ├── exception/       # Domain exceptions and global handlers
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Business logic and file/matching services
│   └── resources/
│       ├── static/           # CSS and JavaScript assets
│       ├── templates/        # Thymeleaf pages
│       ├── application.properties
│       └── schema.sql
└── test/
    └── java/                 # Application tests
```

## Build and Test

Compile the project:

```powershell
.\mvnw.cmd clean package
```

Run the test suite:

```powershell
.\mvnw.cmd test
```

Run the application without packaging:

```powershell
.\mvnw.cmd spring-boot:run
```

## Troubleshooting

### Cannot connect to MySQL

Verify that:

1. MySQL is running.
2. The `lost_found_db` database exists.
3. The username and password in [`application.properties`](src/main/resources/application.properties) are correct.
4. The configured MySQL port is reachable.

### Port 8080 is already in use

Stop the process using port `8080`, or configure another port:

```properties
server.port=8081
```

### Uploaded images are not displayed

Check that:

1. The `uploads/` directory exists or can be created by the application.
2. The application process has read and write permissions.
3. `file.upload-dir` points to the intended directory.

## Contributing

1. Create a feature branch.
2. Make focused changes following the existing package and naming conventions.
3. Run the relevant tests and build checks.
4. Open a pull request with a clear description of the change.

## License

No license has been declared for this repository yet. Add an explicit license before distributing or reusing the project outside its intended development context.
