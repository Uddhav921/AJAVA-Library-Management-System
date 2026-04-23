# 📘 Technology Stack & Project Directory Guide
## AJAVA Library Management System

---

## 🧰 PART 1 — Technologies Used & Their Role in This Project

---

### 1. 🍃 Spring Boot 3.3.4
**Category:** Backend Framework
**Role in Project:**
- Acts as the foundation of the entire backend.
- Auto-configures the embedded **Apache Tomcat** server (port 8080).
- Eliminates boilerplate setup — no need to write `web.xml` or configure beans manually.
- Provides the `@SpringBootApplication` entry point (`AJavaLibrary1Application.java`).
- Manages component scanning, dependency injection, and lifecycle.

**Files Where Used:**
- `AJavaLibrary1Application.java` → `@SpringBootApplication`, `@EnableAsync`
- `pom.xml` → `spring-boot-starter-parent` (version 3.3.4)
- `application.properties` → server port, JPA, JSP, async config

---

### 2. 🗄️ Hibernate ORM (via Spring Data JPA)
**Category:** Object-Relational Mapping (ORM)
**Role in Project:**
- Maps Java entity classes to MySQL database tables automatically.
- Generates SQL (`CREATE TABLE`, `INSERT`, `SELECT`, `UPDATE`) without writing raw SQL.
- Each `@Entity` class = one database table.
- Uses **PreparedStatements** internally (satisfies JDBC requirement of CO2).
- Handles relationships between tables with `@ManyToOne`, `@JoinColumn` (FK constraints).

**Files Where Used:**
- `entity/Book.java` → `@Entity`, `@Table(name="books")`
- `entity/User.java` → `@Entity`, `@Table(name="users")`
- `entity/BorrowRecord.java` → `@Entity`, `@ManyToOne` (FK to users + books)
- `application.properties` → `spring.jpa.hibernate.ddl-auto=update`

---

### 3. 🐬 MySQL 8
**Category:** Relational Database
**Role in Project:**
- Stores all persistent application data.
- Three normalized tables: `users`, `books`, `borrow_records`.
- Foreign key constraints enforce referential integrity between tables.
- Hibernate auto-creates/updates schema on startup (`ddl-auto=update`).
- Connected via HikariCP connection pool for efficient DB connections.

**Configuration:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Files Where Used:**
- `pom.xml` → `mysql-connector-j` dependency
- `application.properties` → datasource URL, username, password
- All `repository/*.java` → queries executed against MySQL

---

### 4. 🔒 Spring Security
**Category:** Security Framework
**Role in Project:**
- Provides the security filter chain for the application.
- `BCryptPasswordEncoder` hashes user passwords before storing in DB — protects against plain-text password leaks.
- `SecurityFilterChain` bean controls which endpoints are accessible.
- Currently configured to permit all requests (REST-friendly open mode for development).

**Files Where Used:**
- `config/SecurityConfig.java` → `@EnableWebSecurity`, `SecurityFilterChain`, `BCryptPasswordEncoder`
- `service/UserService.java` → `passwordEncoder.encode(password)` on registration

---

### 5. 🪄 Lombok
**Category:** Code Generation / Boilerplate Reduction
**Role in Project:**
- Eliminates repetitive Java code (getters, setters, constructors, toString).
- `@Data` = generates all getters + setters + equals/hashCode + toString.
- `@Builder` = generates fluent builder pattern (`Book.builder().title("x").build()`).
- `@NoArgsConstructor` + `@AllArgsConstructor` = generates constructors.
- `@RequiredArgsConstructor` = constructor-based dependency injection.
- `@Slf4j` = injects a Logger field automatically.

**Annotations Used in Project:**

| Annotation | Used In |
|---|---|
| `@Data` | All entity & DTO classes |
| `@Builder` | All entity & DTO classes |
| `@NoArgsConstructor` | All entity & DTO classes |
| `@AllArgsConstructor` | All entity & DTO classes |
| `@RequiredArgsConstructor` | All service & controller classes |
| `@Slf4j` | `FineNotificationService`, `RequestLoggingFilter` |

---

### 6. 💳 Razorpay Java SDK 1.4.6
**Category:** Payment Gateway Integration
**Role in Project:**
- Enables online fine payment by library members.
- **Flow:**
  1. Member has an overdue fine → frontend calls `/api/payment/create-order`
  2. `RazorpayClient` creates an order on Razorpay servers
  3. Member pays via Razorpay checkout popup
  4. Frontend sends payment IDs to `/api/payment/verify`
  5. `Utils.verifyPaymentSignature()` validates HMAC signature
  6. If valid → `finePaid = true` saved in DB

**Files Where Used:**
- `pom.xml` → `razorpay-java 1.4.6` dependency
- `controller/PaymentController.java` → `RazorpayClient`, `Utils.verifyPaymentSignature()`
- `.env` → `NEXT_PUBLIC_RAZORPAY_KEY_ID`, `NEXT_PUBLIC_RAZORPAY_KEY_SECRET`

---

### 7. 📄 JSP + JSTL (Jakarta Server Pages)
**Category:** Server-Side View Technology (CO1)
**Role in Project:**
- Renders the Admin Report page as a server-side HTML page.
- `InternalResourceViewResolver` maps logical view names to `.jsp` files.
- JSTL tags used for dynamic content rendering without Java scriptlets.

**JSTL Tags Used:**

| Tag | Purpose | Where |
|---|---|---|
| `<c:forEach>` | Loop over overdue records list | `report.jsp` |
| `<c:choose>` + `<c:when>` | Conditional badge (Paid/Unpaid) | `report.jsp` |
| `<c:out>` | Safe HTML output (XSS protection) | `report.jsp` |
| `<fmt:formatNumber>` | Format fine amounts (e.g., ₹500.00) | `report.jsp` |

**Files Where Used:**
- `webapp/WEB-INF/views/admin/report.jsp` → Full JSTL admin dashboard
- `webapp/WEB-INF/views/error.jsp` → Custom error page
- `pom.xml` → `tomcat-embed-jasper` + `jakarta.servlet.jsp.jstl`
- `application.properties` → `spring.mvc.view.prefix/suffix`

---

### 8. 🔀 Spring MVC Servlet Filter
**Category:** Servlet API (CO1)
**Role in Project:**
- `RequestLoggingFilter` intercepts every HTTP request before it reaches controllers.
- Logs request method, URI, query params, client IP, response status, and processing time.
- Extends `OncePerRequestFilter` — guaranteed single execution per request.
- Skips static resources (CSS, JS, images) to reduce log noise.

**Files Where Used:**
- `filter/RequestLoggingFilter.java` → `OncePerRequestFilter`, `@Component`

**Sample log output:**
```
[REQUEST ] --> POST /api/borrow/issue?userId=1&bookId=3 | Client: 127.0.0.1
[RESPONSE] <-- POST /api/borrow/issue?userId=1&bookId=3 | Status: 201 | Time: 42 ms
```

---

### 9. ⚡ Spring @Async + ThreadPoolTaskExecutor
**Category:** Multithreading (CO2)
**Role in Project:**
- `FineNotificationService` methods run on a **dedicated background thread pool** instead of the HTTP request thread.
- This means: HTTP response is returned to client immediately; notification processing happens in parallel.
- `ThreadPoolTaskExecutor` configured with 5 core / 10 max / 25 queue threads.
- Thread pool named `library-async-pool` (visible in logs as `library-async-pool-1`, `library-async-pool-2`, etc.).
- Triggered after book return (with fine) and after book issue (confirmation).

**Files Where Used:**
- `AJavaLibrary1Application.java` → `@EnableAsync`
- `config/AsyncConfig.java` → `ThreadPoolTaskExecutor` bean
- `service/FineNotificationService.java` → `@Async("libraryAsyncExecutor")`
- `service/BorrowService.java` → calls async methods

---

### 10. ▲ Next.js 16 (React 19 + TypeScript)
**Category:** Frontend Framework (CO5)
**Role in Project:**
- Provides the user-facing library management interface.
- Members can browse books, issue/return books, view borrow history, pay fines.
- Admin dashboard component (`admin-dashboard.tsx`) for managing the system.
- Communicates with Spring Boot backend via HTTP REST API calls.
- Tailwind CSS for styling.

**Files Where Used:**
- `library-ui/app/page.tsx` → Main page (19KB — full frontend logic)
- `library-ui/components/admin-dashboard.tsx` → Admin panel component
- `library-ui/app/layout.tsx` → Root layout
- `library-ui/app/globals.css` → Global styles

---

### 11. 🌐 CORS Configuration
**Category:** Cross-Origin Resource Sharing
**Role in Project:**
- Allows the Next.js frontend (port 3000) to call the Spring Boot backend (port 8080).
- Without CORS config, browsers would block these cross-origin requests.

**Files Where Used:**
- `config/CorsConfig.java` → `WebMvcConfigurer`, allowed origins/methods/headers
- `controller/PaymentController.java` → `@CrossOrigin(origins="http://localhost:3000")`

---

### 12. 🔧 Spring Dotenv (`spring-dotenv`)
**Category:** Environment Variable Management
**Role in Project:**
- Loads `.env` file at startup so Spring can read `NEXT_PUBLIC_RAZORPAY_KEY_ID` etc.
- Prevents secrets (API keys) from being hardcoded in `application.properties`.

**Files Where Used:**
- `pom.xml` → `me.paulschwarz:spring-dotenv`
- `.env` (project root) → Razorpay key ID and secret

---
---

## 📂 PART 2 — Backend Directory Structure Explained

```
src/main/java/com/image/ajlibrary/
```

---

### 📌 Root — `AJavaLibrary1Application.java`
**The application entry point.**

```java
@SpringBootApplication   // Enables component scan, auto-config, bean registration
@EnableAsync             // Activates @Async proxy — CO2 multithreading
public class AJavaLibrary1Application { ... }
```

- This is the first class Spring Boot executes.
- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan` combined.
- `@EnableAsync` tells Spring to create async proxies for `@Async`-annotated methods.

---

### 📁 `config/` — Application Configuration
All `@Configuration` classes that set up Spring beans and infrastructure.

| File | What It Does |
|---|---|
| `AsyncConfig.java` | Defines `ThreadPoolTaskExecutor` with 5 core / 10 max threads. Named `libraryAsyncExecutor`. Used by `@Async` methods. (CO2) |
| `CorsConfig.java` | Allows Next.js (port 3000) to call backend (port 8080) for cross-origin REST calls. |
| `GlobalExceptionHandler.java` | `@ControllerAdvice` — catches exceptions from all controllers and returns clean JSON error responses instead of stack traces. |
| `SecurityConfig.java` | Configures Spring Security filter chain. Disables CSRF, permits all endpoints (dev mode). Provides `BCryptPasswordEncoder` bean for hashing passwords. |

---

### 📁 `controller/` — REST API & MVC Controllers
Handles incoming HTTP requests, delegates to services, returns responses.

| File | Type | Endpoint Prefix | Purpose |
|---|---|---|---|
| `AdminViewController.java` | `@Controller` (MVC) | `/admin` | Returns JSP views. `GET /admin/report` → renders `report.jsp` with live library stats. (CO1) |
| `BookController.java` | `@RestController` | `/api/books` | CRUD operations for books — add, update, delete, search by title/author. |
| `BorrowController.java` | `@RestController` | `/api/borrow` | Issue book, return book, view history, view active borrows, view overdue records. |
| `HomeController.java` | `@RestController` | `/` | Health check / home endpoint. |
| `PaymentController.java` | `@RestController` | `/api/payment` | Razorpay integration — `/create-order` creates payment order, `/verify` validates HMAC signature. (CO3, CO5) |
| `UserController.java` | `@RestController` | `/api/users` | User registration (BCrypt password), login, fetch by ID, list all. |

---

### 📁 `dto/` — Data Transfer Objects
POJOs used to carry data between layers (client ↔ controller ↔ service). Not persisted to DB.

| File | Direction | Fields |
|---|---|---|
| `BookRequest.java` | Client → Controller (request body) | `title`, `author`, `isbn`, `totalCopies` |
| `BorrowResponse.java` | Controller → Client (response) | `recordId`, `userId`, `username`, `bookTitle`, `issueDate`, `dueDate`, `fine`, `finePaid`, `status`, `overdue` |
| `LibraryStatsDto.java` | Controller → JSP (Model attribute) | `totalBooks`, `totalUsers`, `activeBorrows`, `overdueCount`, `returnedRecords`, `totalFinesCollected`, `totalFinesPending` |
| `LoginRequest.java` | Client → Controller | `username`, `password` |
| `RegisterRequest.java` | Client → Controller | `username`, `email`, `password`, `role` |

---

### 📁 `entity/` — JPA Entity Classes (Database Tables)
Each class maps to a MySQL table. Hibernate reads these and creates/manages the schema.

| File | Table Name | Key Fields | Relationships |
|---|---|---|---|
| `User.java` | `users` | `id`, `username`, `email`, `password`, `role` (ADMIN/MEMBER), `createdAt` | None (referenced by BorrowRecord) |
| `Book.java` | `books` | `id`, `title`, `author`, `isbn`, `totalCopies`, `availableCopies`, `addedAt` | None (referenced by BorrowRecord) |
| `BorrowRecord.java` | `borrow_records` | `id`, `issueDate`, `dueDate`, `returnDate`, `fine`, `finePaid`, `status` (ISSUED/RETURNED) | `@ManyToOne` → `User` (FK: `user_id`), `@ManyToOne` → `Book` (FK: `book_id`) |

**Database Relationships:**
```
users (1) ──────< borrow_records >────── (1) books
                user_id (FK)   book_id (FK)
```

---

### 📁 `filter/` — Servlet Filters (CO1)
HTTP request/response interceptors implemented as Servlet Filters.

| File | Extends | Purpose |
|---|---|---|
| `RequestLoggingFilter.java` | `OncePerRequestFilter` | Logs every HTTP request (method, URI, client IP) and response (status, duration). Skips static files. |

---

### 📁 `repository/` — Data Access Layer (CO4)
Spring Data JPA repository interfaces. No SQL needed — methods are auto-implemented by Hibernate.

| File | Extends | Custom Methods |
|---|---|---|
| `BookRepository.java` | `JpaRepository<Book, Long>` | `findByAvailableCopiesGreaterThan()`, `existsByIsbn()`, `findByTitleContainingIgnoreCase()`, `findByAuthorContainingIgnoreCase()` |
| `UserRepository.java` | `JpaRepository<User, Long>` | `findByUsername()`, `existsByUsername()`, `existsByEmail()` |
| `BorrowRecordRepository.java` | `JpaRepository<BorrowRecord, Long>` | `findByUserId()`, `findByUserIdAndStatus()`, `findByUserIdAndBookIdAndStatus()`, `findByStatus()`, `@Query findOverdueRecords()`, `deleteByBookId()` |

---

### 📁 `service/` — Business Logic Layer (CO2, CO3)
Contains all business rules. Controllers are thin — they only call services.

| File | Annotations | Key Responsibilities |
|---|---|---|
| `BookService.java` | `@Service`, `@Transactional` | Add/update/delete books with availability tracking; search by title/author. |
| `BorrowService.java` | `@Service`, `@Transactional` | Issue book (availability check + duplicate check), return book (auto fine calc = overdue days × ₹5/day), calls async notification. |
| `UserService.java` | `@Service` | Register user (BCrypt password), login (password match), fetch user. |
| `FineNotificationService.java` | `@Service`, `@Async` | Sends fine alerts and book-issued confirmations on `library-async-pool` thread. Runs non-blocking. (CO2) |

---

### 📁 `webapp/WEB-INF/views/` — JSP View Templates (CO1)

| File | Controller | Purpose |
|---|---|---|
| `admin/report.jsp` | `AdminViewController` | Full admin dashboard — stats cards (books, users, borrows, overdue, fines) + JSTL overdue records table |
| `error.jsp` | Auto (Spring error handler) | Custom JSP error page shown on application errors |

---

## 🔁 Request Flow Summary

```
Browser / Next.js Frontend
        │
        ▼
RequestLoggingFilter.java     ← Servlet Filter (CO1) — logs request
        │
        ▼
DispatcherServlet             ← Spring MVC core (embedded in Boot)
        │
   ┌────┴──────────────────────────────────┐
   │ REST Request          │ JSP Request   │
   ▼                       ▼               │
@RestController        @Controller         │
BookController       AdminViewController   │
BorrowController            │              │
UserController              │              │
PaymentController     Model attributes     │
   │                   injected into JSP   │
   ▼                       ▼               │
@Service Layer         report.jsp          │
BorrowService       (JSTL rendering)       │
BookService                                │
UserService                                │
FineNotificationService.@Async ────────────┘
        │
        ▼ (separate library-async-pool thread)
@Repository Layer
BorrowRecordRepository → Hibernate → MySQL
BookRepository
UserRepository
```

---

## 📊 Technology vs Course Outcome Map

| Technology | CO1 | CO2 | CO3 | CO4 | CO5 |
|---|:---:|:---:|:---:|:---:|:---:|
| Spring Boot 3.3.4 | ✅ | ✅ | ✅ | ✅ | ✅ |
| Hibernate / JPA | | ✅ | ✅ | ✅ | |
| MySQL 8 | | | | ✅ | |
| Spring Security | | | ✅ | | |
| Lombok | | ✅ | ✅ | | |
| Razorpay Java SDK | | | ✅ | | ✅ |
| JSP + JSTL | ✅ | | | | |
| Servlet Filter | ✅ | | | | |
| @Async + Thread Pool | | ✅ | | | |
| Next.js 16 | | | | | ✅ |
| CORS Config | | | | | ✅ |
| Spring Dotenv | | | ✅ | | |
