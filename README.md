# Library Management System

A full-featured **Library Management System REST API** built with **Spring Boot**, **Spring Security (JWT)**, and **PostgreSQL**. It supports role-based access for Admins, Librarians, and Members — covering book cataloging, physical copy tracking, borrowing/returns, reservations, and an admin dashboard.

## Features

- **JWT-based authentication & authorization** with stateless sessions
- **Role-based access control** — `ADMIN`, `LIBRARIAN`, `MEMBER`
- **Book & Category management** — full CRUD with search
- **Book Copy tracking** — barcode-based inventory with status (`AVAILABLE`, `BORROWED`, `RESERVED`, `LOST`, `DAMAGED`, `UNDER_MAINTENANCE`)
- **Borrow / Return workflow** with due dates, overdue detection, and fine calculation
- **Reservations** — members can reserve unavailable books
- **Admin dashboard** — real-time library statistics
- **Centralized exception handling** with clean, consistent error responses
- **Auto-seeded roles and default admin account** on first run

## Tech Stack

| Layer          | Technology                              |
|----------------|------------------------------------------|
| Language       | Java 21                                   |
| Framework      | Spring Boot 3.5.16                        |
| Security       | Spring Security + JWT (`jjwt` 0.12.7)     |
| Persistence    | Spring Data JPA (Hibernate)               |
| Database       | PostgreSQL                                |
| Validation     | Jakarta Bean Validation                   |
| Build Tool     | Maven                                     |
| Boilerplate    | Lombok                                    |

## Security

- Passwords encrypted using **BCrypt**
- All protected endpoints require a valid **JWT token**
- Token secret and expiry are configurable via environment variables
- Role based access control restricts endpoints by user role (`ADMIN`, `LIBRARIAN`, `MEMBER`)
- Stateless sessions — no server-side session storage
- Custom `401`/`403` JSON responses for unauthorized/forbidden requests

## Architecture

```
src/main/java/com/devdiyora/library
├── config/          # Security config, role & admin data loaders
├── controller/       # REST controllers
├── dto/
│   ├── request/       # Incoming payloads
│   └── response/      # Outgoing payloads
├── entity/            # JPA entities
├── enums/             # Domain enums (roles, statuses)
├── exception/         # Custom exceptions + global handler
├── repository/        # Spring Data JPA repositories
├── security/          # JWT filter, user details, entry points
├── service/           # Service interfaces
│   └── impl/          # Service implementations
└── util/              # Helper utilities
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+ (or use the included `mvnw` wrapper)
- PostgreSQL 14+

### 1. Clone the repository

```bash
git clone https://github.com/devdiyora/library-management-system.git
cd library-management-system
```

### 2. Create the database

```sql
CREATE DATABASE library_db;
```

### 3. Configure environment variables

The app reads its configuration from environment variables (see `application.properties`):

| Variable          | Description                              | Example                                            |
|-------------------|--------------------------------------------|-----------------------------------------------------|
| `DB_URL`          | JDBC connection URL                      | `jdbc:postgresql://localhost:5432/library_db`      |
| `DB_USERNAME`     | Database username                        | `postgres`                                          |
| `DB_PASSWORD`     | Database password                        | `yourpassword`                                      |
| `JWT_SECRET`      | Secret key used to sign JWT tokens       | a long, random Base64-encoded string               |
| `JWT_EXPIRATION`  | Token validity in milliseconds           | `86400000` (24 hours)                              |

You can export them directly or use a `.env` file with a tool like `direnv`, or set them as run configuration variables in your IDE.

```bash
export DB_URL=jdbc:postgresql://localhost:5432/library_db
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-super-secret-jwt-key
export JWT_EXPIRATION=86400000
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will start on `http://localhost:8080`.

On first startup, the app automatically seeds the three roles (`ADMIN`, `LIBRARIAN`, `MEMBER`) and creates a default admin account:

| Field    | Value               |
|----------|---------------------|
| Email    | `admin@gmail.com`   |
| Password | `admin123`          |

> **Change this password immediately in any non-local environment.**

## Authentication

All endpoints except `/users/register` and `/users/login` require a valid JWT sent in the `Authorization` header:

```
Authorization: Bearer <your_token>
```

### Register (Member)

`POST /users/register`

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "9876543210",
  "password": "SecurePass123"
}
```

**Response**

```json
{
  "userId": 2,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "role": "MEMBER"
}
```

### Login

`POST /users/login`

```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123"
}
```

**Response**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```
---
## API Endpoints

### Users — `/users`

| Method | Endpoint                    | Access          | Description                     |
|--------|------------------------------|-----------------|----------------------------------|
| POST   | `/users/register`            | Public          | Register a new member           |
| POST   | `/users/login`                | Public          | Authenticate and get a JWT       |
| POST   | `/users/librarians`           | ADMIN           | Create a librarian account       |
| GET    | `/users/me`                   | Authenticated   | Get current user profile         |
| GET    | `/users`                       | ADMIN           | List all users                   |
| PATCH  | `/users/{id}/toggle-status`    | ADMIN           | Enable/disable a user account    |
---
### Books — `/books`

| Method | Endpoint         | Access                | Description               |
|--------|-------------------|------------------------|-----------------------------|
| POST   | `/books`           | ADMIN, LIBRARIAN       | Add a new book              |
| GET    | `/books`           | Authenticated          | List all books (paginated)  |
| GET    | `/books/{id}`       | Authenticated          | Get book details            |
| GET    | `/books/search`     | Authenticated          | Search books                |
| PUT    | `/books/{id}`       | ADMIN, LIBRARIAN       | Update a book                |
| DELETE | `/books/{id}`       | ADMIN                  | Delete a book                |

**Sample request** — `POST /books`

```json
{
  "title": "Clean Code",
  "isbn": "9780132350884",
  "authorName": "Robert C. Martin",
  "publisherName": "Prentice Hall",
  "language": "English",
  "publicationYear": 2008,
  "edition": "1st",
  "categoryId": 1
}
```

**Sample response**

```json
{
  "id": 5,
  "title": "Clean Code",
  "isbn": "9780132350884",
  "authorName": "Robert C. Martin",
  "publisherName": "Prentice Hall",
  "language": "English",
  "publicationYear": 2008,
  "edition": "1st",
  "category": "Software Engineering"
}
```
---
### Categories — `/categories`

| Method | Endpoint            | Access             | Description         |
|--------|-----------------------|---------------------|-----------------------|
| POST   | `/categories`          | ADMIN, LIBRARIAN    | Create a category    |
| GET    | `/categories`          | Authenticated       | List all categories  |
| GET    | `/categories/{id}`      | Authenticated       | Get category details |
| PUT    | `/categories/{id}`      | ADMIN, LIBRARIAN    | Update a category     |
| DELETE | `/categories/{id}`      | ADMIN, LIBRARIAN    | Delete a category     |
---
### Book Copies — `/book-copies`

| Method | Endpoint                      | Access              | Description                    |
|--------|----------------------------------|----------------------|-----------------------------------|
| POST   | `/book-copies`                    | ADMIN, LIBRARIAN     | Register a new physical copy    |
| GET    | `/book-copies`                    | Authenticated        | List all copies                  |
| GET    | `/book-copies/{id}`                | Authenticated        | Get copy details                 |
| GET    | `/book-copies/barcode/{barcode}`    | Authenticated        | Look up a copy by barcode        |
| PUT    | `/book-copies/{id}`                | ADMIN, LIBRARIAN     | Update a copy                     |
| PATCH  | `/book-copies/{id}/status`          | ADMIN, LIBRARIAN     | Mark a copy as LOST, DAMAGED, UNDER_MAINTENANCE, or AVAILABLE |
| DELETE | `/book-copies/{id}`                | ADMIN                | Delete a copy                     |

**Sample request** — `PATCH /book-copies/5/status`

```json
{
  "status": "DAMAGED"
}
```

> Note: A copy's status cannot be set to `BORROWED` manually — that only happens through the borrow transaction flow. Similarly, a currently borrowed copy must be returned before its status can be changed.

**Sample request** — `POST /book-copies`

```json
{
  "bookId": 5,
  "barcode": "LMS-BC-00123"
}
```
---
### Borrow Transactions — `/borrow-transactions`

| Method | Endpoint                              | Access              | Description                          |
|--------|------------------------------------------|----------------------|------------------------------------------|
| POST   | `/borrow-transactions/issue`               | ADMIN, LIBRARIAN     | Issue a book to a member                 |
| POST   | `/borrow-transactions/return`              | ADMIN, LIBRARIAN     | Process a book return                     |
| GET    | `/borrow-transactions`                     | ADMIN, LIBRARIAN     | List all transactions                     |
| GET    | `/borrow-transactions/{id}`                 | ADMIN, LIBRARIAN     | Get transaction details                   |
| GET    | `/borrow-transactions/history/{memberId}`    | ADMIN, LIBRARIAN     | Full history for a specific member        |
| GET    | `/borrow-transactions/active/{memberId}`     | ADMIN, LIBRARIAN     | Active loans for a specific member        |
| GET    | `/borrow-transactions/my-history`            | MEMBER               | Logged-in member's borrow history         |
| GET    | `/borrow-transactions/my-active-books`        | MEMBER               | Logged-in member's currently borrowed books |

**Sample request** — `POST /borrow-transactions/issue`

```json
{
  "memberId": 2,
  "barcode": "LMS-BC-00123"
}
```

**Sample response**

```json
{
  "id": 10,
  "memberName": "John Doe",
  "bookTitle": "Clean Code",
  "barcode": "LMS-BC-00123",
  "issueDate": "2026-07-05",
  "dueDate": "2026-07-19",
  "returnDate": null,
  "status": "BORROWED",
  "fineAmount": 0
}
```
---
### Reservations — `/reservations`

| Method | Endpoint                       | Access              | Description                        |
|--------|------------------------------------|----------------------|-----------------------------------------|
| POST   | `/reservations`                     | MEMBER               | Reserve a book                        |
| GET    | `/reservations/my-reservations`      | MEMBER               | View logged-in member's reservations  |
| GET    | `/reservations`                     | ADMIN, LIBRARIAN     | List all reservations                  |
| GET    | `/reservations/pending`              | ADMIN, LIBRARIAN     | List pending reservations               |
| DELETE | `/reservations/{reservationId}`      | MEMBER, ADMIN        | Cancel a reservation                    |

**Sample request** — `POST /reservations`

```json
{
  "bookId": 5
}
```
---
### Dashboard — `/dashboard`

| Method | Endpoint     | Access              | Description              |
|--------|----------------|----------------------|-----------------------------|
| GET    | `/dashboard`    | ADMIN, LIBRARIAN     | Library-wide statistics   |

**Sample response**

```json
{
  "totalBooks": 128,
  "totalBookCopies": 340,
  "availableBookCopies": 210,
  "borrowedBookCopies": 115,
  "totalMembers": 87,
  "activeBorrowTransactions": 115,
  "pendingReservations": 6
}
```
---
## Error Format

All errors follow a consistent shape via the global exception handler:

```json
{
  "timestamp": "2026-07-05T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found with id: 99"
}
```
---
## Project Structure

```
library-management-system/
└── src/
    └── main/
        └── java/
            └── com.devdiyora.library/
                │
                ├── LibraryManagementSystemApplication.java   # Entry point
                │
                ├── config/
                │   ├── AdminDataLoader.java          # Seeds default admin on startup
                │   ├── RoleDataLoader.java           # Seeds ADMIN/LIBRARIAN/MEMBER roles
                │   ├── SecurityBeansConfig.java       # Password encoder & related beans
                │   └── SecurityConfig.java           # Spring Security filter chain
                │
                ├── controller/
                │   ├── BookController.java
                │   ├── BookCopyController.java
                │   ├── BorrowTransactionController.java
                │   ├── CategoryController.java
                │   ├── DashboardController.java
                │   ├── ReservationController.java
                │   └── UserController.java
                │
                ├── dto/
                │   ├── request/
                │   │   ├── BookCopyRequest.java
                │   │   ├── BookRequest.java
                │   │   ├── CategoryRequest.java
                │   │   ├── IssueBookRequest.java
                │   │   ├── LoginRequest.java
                │   │   ├── RegisterRequest.java
                │   │   ├── ReserveBookRequest.java
                │   │   ├── ReturnBookRequest.java
                │   │   └── UpdateBookCopyStatusRequest.java
                │   └── response/
                │       ├── BookCopyResponse.java
                │       ├── BookResponse.java
                │       ├── BorrowTransactionResponse.java
                │       ├── CategoryResponse.java
                │       ├── CurrentUserResponse.java
                │       ├── DashboardResponse.java
                │       ├── ErrorResponse.java
                │       ├── LoginResponse.java
                │       ├── PageResponse.java
                │       ├── RegisterResponse.java
                │       ├── ReservationResponse.java
                │       └── UserResponse.java
                │
                ├── entity/
                │   ├── Book.java
                │   ├── BookCopy.java
                │   ├── BorrowTransaction.java
                │   ├── Category.java
                │   ├── Reservation.java
                │   ├── Role.java
                │   └── User.java
                │
                ├── enums/
                │   ├── BookCopyStatus.java           # Enum
                │   ├── BorrowStatus.java             # Enum
                │   ├── ReservationStatus.java        # Enum
                │   └── RoleType.java                 # Enum
                │
                ├── exception/
                │   ├── BusinessException.java
                │   ├── DuplicateResourceException.java
                │   ├── GlobalExceptionHandler.java
                │   └── ResourceNotFoundException.java
                │
                ├── repository/
                │   ├── BookCopyRepository.java
                │   ├── BookRepository.java
                │   ├── BorrowTransactionRepository.java
                │   ├── CategoryRepository.java
                │   ├── ReservationRepository.java
                │   ├── RoleRepository.java
                │   └── UserRepository.java
                │
                ├── security/
                │   ├── CustomAccessDeniedHandler.java
                │   ├── CustomAuthenticationEntryPoint.java
                │   ├── CustomUserDetails.java
                │   ├── CustomUserDetailsService.java
                │   └── jwt/
                │       ├── JwtAuthenticationFilter.java   # JWT request filter
                │       └── JwtService.java                # Token generate/validate
                │
                ├── service/
                │   ├── BookCopyService.java
                │   ├── BookService.java
                │   ├── BorrowTransactionService.java
                │   ├── CategoryService.java
                │   ├── DashboardService.java
                │   ├── ReservationService.java
                │   ├── UserService.java
                │   └── impl/
                │       ├── BookCopyServiceImpl.java
                │       ├── BookServiceImpl.java
                │       ├── BorrowTransactionServiceImpl.java
                │       ├── CategoryServiceImpl.java
                │       ├── DashboardServiceImpl.java
                │       ├── ReservationServiceImpl.java
                │       └── UserServiceImpl.java
                │
                └── util/
                    ├── CurrentUserProvider.java
                    └── SortFieldValidator.java
```
---
## Author

**Diyora**
GitHub: [@devdiyora](https://github.com/devdiyora)
Repository: [library-management-system](https://github.com/devdiyora/library-management-system)