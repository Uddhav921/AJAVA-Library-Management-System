# 📚 AJAVA Library Management System

A full-stack **Library Book Issue Management System** built with **Spring Boot 3.3.4** backend and **Next.js 16** frontend, featuring **Razorpay** fine payment integration.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend Framework | Spring Boot 3.3.4 |
| ORM | Hibernate (via Spring Data JPA) |
| Database | MySQL 8 |
| Security | Spring Security + BCrypt |
| Boilerplate | Lombok |
| Payment Gateway | Razorpay Java SDK 1.4.6 |
| Frontend | Next.js 16, React 19, TypeScript, Tailwind CSS |
| View Layer (JSP) | Apache Tomcat Jasper + JSTL |
| Async | Spring `@Async` + `ThreadPoolTaskExecutor` |

---

## 📁 Project Structure

```
AJAVA-Library-Management-System/
├── src/
│   └── main/
│       ├── java/com/image/ajlibrary/
│       │   ├── AJavaLibrary1Application.java   ← @SpringBootApplication + @EnableAsync
│       │   ├── config/
│       │   │   ├── AsyncConfig.java            ← Thread pool (CO2)
│       │   │   ├── CorsConfig.java
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   └── SecurityConfig.java         ← Spring Security (CO3)
│       │   ├── controller/
│       │   │   ├── AdminViewController.java    ← JSP MVC controller (CO1)
│       │   │   ├── BookController.java
│       │   │   ├── BorrowController.java
│       │   │   ├── HomeController.java
│       │   │   ├── PaymentController.java      ← Razorpay (CO3, CO5)
│       │   │   └── UserController.java
│       │   ├── dto/
│       │   │   ├── BookRequest.java
│       │   │   ├── BorrowResponse.java
│       │   │   ├── LibraryStatsDto.java        ← JSP stats DTO (CO2 OOP)
│       │   │   ├── LoginRequest.java
│       │   │   └── RegisterRequest.java
│       │   ├── entity/
│       │   │   ├── Book.java                   ← @Entity, Lombok (CO2, CO3, CO4)
│       │   │   ├── BorrowRecord.java           ← FK relationships (CO4)
│       │   │   └── User.java                   ← @Entity with Role enum (CO2, CO3)
│       │   ├── filter/
│       │   │   └── RequestLoggingFilter.java   ← Servlet Filter (CO1)
│       │   ├── repository/
│       │   │   ├── BookRepository.java         ← JPA repo (CO4)
│       │   │   ├── BorrowRecordRepository.java ← Custom @Query (CO4)
│       │   │   └── UserRepository.java
│       │   └── service/
│       │       ├── BookService.java
│       │       ├── BorrowService.java          ← @Transactional + @Async calls (CO2)
│       │       ├── FineNotificationService.java← @Async on thread pool (CO2)
│       │       └── UserService.java
│       ├── resources/
│       │   └── application.properties
│       └── webapp/
│           └── WEB-INF/views/
│               ├── admin/
│               │   └── report.jsp              ← JSTL report page (CO1)
│               └── error.jsp                   ← JSP error page (CO1)
├── library-ui/                                 ← Next.js 16 frontend (CO5)
│   ├── app/
│   ├── components/
│   └── package.json
├── pom.xml
├── .env
└── README.md
```

---

## 🎓 Course Outcome Mapping

| CO | Outcome | Technologies Used | Key Files |
|---|---|---|---|
| **CO1** | Servlet & JSP API | `OncePerRequestFilter` (Servlet Filter) · `InternalResourceViewResolver` · JSTL (`c:forEach`, `c:choose`, `fmt:formatNumber`) · JSP pages | `RequestLoggingFilter.java` · `AdminViewController.java` · `report.jsp` · `error.jsp` |
| **CO2** | OOP, JDBC, Multithreading | `@Entity` classes with encapsulation & enums · Hibernate PreparedStatements (JDBC) · `@Async` + `ThreadPoolTaskExecutor` (library-async-pool) | `AsyncConfig.java` · `FineNotificationService.java` · `BorrowService.java` · `LibraryStatsDto.java` · All entity classes |
| **CO3** | Implement using Java Frameworks | Spring Boot 3.3.4 · Hibernate ORM · Lombok (`@Data`, `@Builder`) · Spring Security (BCrypt) · Razorpay Java SDK 1.4.6 | `pom.xml` · `SecurityConfig.java` · `PaymentController.java` · All `@Entity` classes |
| **CO4** | Integrate Relational Database | MySQL 8 · Spring Data JPA (`JpaRepository`) · Hibernate ORM · 3 normalised tables with FK constraints · Custom `@Query` JPQL | `BookRepository.java` · `UserRepository.java` · `BorrowRecordRepository.java` · `Book.java` · `BorrowRecord.java` · `User.java` |
| **CO5** | Build & Deploy Full-Stack App | Spring Boot REST API (5 controllers) · Next.js 16 frontend · Razorpay order/verify end-to-end · CORS configuration | All controllers · `library-ui/` directory · `PaymentController.java` · `CorsConfig.java` |

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8 (via XAMPP or standalone)
- Node.js 18+

### 1. Database Setup
```sql
CREATE DATABASE library_db;
```

### 2. Environment Variables
Create a `.env` file in the project root:
```env
NEXT_PUBLIC_RAZORPAY_KEY_ID=your_key_id
NEXT_PUBLIC_RAZORPAY_KEY_SECRET=your_key_secret
```

### 3. Start Backend
```bash
# From project root
./mvnw spring-boot:run
```
Backend runs on → **http://localhost:8080**

### 4. Start Frontend
```bash
cd library-ui
npm install
npm run dev
```
Frontend runs on → **http://localhost:3000**

### 5. Access JSP Admin Report (CO1)
```
http://localhost:8080/admin/report
```

---

## 🔌 REST API Endpoints

### Books (`/api/books`)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/books` | Get all books |
| GET | `/api/books/available` | Get available books |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/search?title=` | Search by title |
| GET | `/api/books/search/author?author=` | Search by author |
| POST | `/api/books` | Add new book |
| PUT | `/api/books/{id}` | Update book |
| DELETE | `/api/books/{id}` | Delete book |

### Users (`/api/users`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users` | Get all users (admin) |

### Borrow (`/api/borrow`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/borrow/issue?userId=&bookId=` | Issue a book |
| PUT | `/api/borrow/return/{recordId}` | Return a book (fine auto-calculated) |
| GET | `/api/borrow/history/{userId}` | Full borrow history |
| GET | `/api/borrow/active/{userId}` | Active borrows |
| GET | `/api/borrow/overdue` | All overdue records (admin) |
| GET | `/api/borrow/all` | All records (admin) |

### Payment (`/api/payment`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payment/create-order?recordId=` | Create Razorpay order for fine |
| POST | `/api/payment/verify?recordId=` | Verify payment signature |

### JSP Views (CO1)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/report` | JSP admin dashboard with library stats & overdue table |

---

## 🔧 CO2: How Async Works

When a book is returned **with a fine**:

```
HTTP Request → BorrowController.returnBook()
                  ↓
            BorrowService.returnBook()
               ├── Calculate fine
               ├── Update DB (synchronous)
               ├── HTTP Response sent ←── Client gets response immediately
               └── fineNotificationService.sendFineAlert()  ← runs on library-async-pool-1
                       (separate thread, does not block HTTP response)
```

Thread pool config (`application.properties`):
```properties
library.async.core-pool-size=5
library.async.max-pool-size=10
library.async.queue-capacity=25
library.async.thread-name-prefix=library-async-pool-
```

---

## 🏦 Database Schema

```
┌──────────┐         ┌─────────────────┐         ┌──────────────┐
│  users   │         │  borrow_records  │         │    books     │
├──────────┤         ├─────────────────┤         ├──────────────┤
│ id (PK)  │◄────────│ user_id (FK)    │────────►│ id (PK)      │
│ username │         │ book_id (FK)    │         │ title        │
│ password │         │ issue_date      │         │ author       │
│ email    │         │ due_date        │         │ isbn         │
│ role     │         │ return_date     │         │ total_copies │
│ created  │         │ fine            │         │ avail_copies │
└──────────┘         │ fine_paid       │         │ added_at     │
                     │ status          │         └──────────────┘
                     └─────────────────┘
```

---

## 💡 Features

- ✅ Book CRUD (Add / Update / Delete / Search)
- ✅ User Registration & Login (BCrypt hashed passwords)
- ✅ Book Issue & Return with automatic fine calculation (₹5/day overdue)
- ✅ Overdue tracking with live fine preview
- ✅ Razorpay online fine payment with HMAC signature verification
- ✅ JSP Admin Report (live stats + JSTL overdue table) — **CO1**
- ✅ Async fine notifications on dedicated thread pool — **CO2**
- ✅ Servlet Filter for request/response logging — **CO1**
- ✅ Next.js 16 frontend with Tailwind CSS — **CO5**

---

## 📜 License

This project is for academic purposes — AJAVA (Advanced Java) course.
