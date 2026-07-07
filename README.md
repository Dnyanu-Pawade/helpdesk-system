# 🎫 Enterprise Help Desk & IT Ticketing System v1.0

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![License](https://img.shields.io/badge/License-MIT-green)

A full-featured Enterprise Help Desk & IT Ticketing System built with **Spring Boot**, **MySQL**, and **HTML/CSS/JS**. Supports multi-role workflows, SLA tracking, real-time notifications, and audit logging.

---

## 🚀 Features

- 🔐 **JWT Authentication** with role-based access control
- 🎫 **Ticket Lifecycle Management** — Open → In Progress → Resolved → Closed
- 👥 **4 Roles**: Admin, Team Lead, Support Engineer, Employee
- ⏱️ **SLA Breach Detection** — Hourly scheduler auto-flags breached tickets
- 🔔 **Real-time Notifications** per user
- 📊 **Admin Dashboard** with charts and analytics
- 📝 **Audit Logs** for all critical actions
- 📎 **File Attachments** on tickets
- 📧 **Async Email Notifications**
- 🏷️ **11 Ticket Categories**, 4 Priority levels, 7 Status stages
- 🔑 **Forgot Password** flow
- 📖 **Swagger API Docs** at `/swagger-ui.html`

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2.0, Spring Security, Spring Data JPA |
| Auth | JWT (JSON Web Tokens) |
| Database | MySQL 8.0, Hibernate ORM |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Email | Spring Mail (Async) |
| Docs | SpringDoc OpenAPI (Swagger) |
| Build | Maven |

---

## 👤 Default Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `Admin@123` |
| Team Lead | `teamlead1` | `TeamLead@123` |
| Engineer | `engineer1` | `Engineer@123` |
| Engineer | `engineer2` | `Engineer@123` |
| Employee | `employee1` | `Employee@123` |
| Employee | `employee2` | `Employee@123` |

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.8+

### 1. Clone the repo
```bash
git clone https://github.com/Dnyanu-Pawade/helpdesk-system.git
cd helpdesk-system
```

### 2. Create MySQL database
```sql
CREATE DATABASE helpdesk_db;
```

### 3. Configure `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/helpdesk_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Build & Run
```bash
mvn clean package -DskipTests
java -jar target/helpdesk-system-1.0.0.jar
```

### 5. Open in browser
```
http://localhost:8083
```

---

## 📁 Project Structure

```
helpdesk-system/
├── src/main/java/com/helpdesk/
│   ├── config/          # Security, Web, Swagger, DataInitializer
│   ├── controller/      # Auth, Ticket, Engineer, TeamLead, Admin, Notification, User
│   ├── dto/             # TicketSummaryDTO, LoginRequest, JwtResponse, etc.
│   ├── entity/          # User, Ticket, Department, Notification, AuditLog, etc.
│   ├── enums/           # Role, TicketStatus, Priority, TicketCategory
│   ├── repository/      # 9 JPA repositories
│   ├── scheduler/       # SlaScheduler (hourly SLA breach check)
│   ├── security/        # JWT filter, UserDetailsImpl
│   └── service/         # Auth, Ticket, User, Email, Notification, AuditLog
└── src/main/resources/
    └── static/
        ├── html/        # 13 HTML pages
        ├── css/         # style.css
        ├── js/          # api.js, app.js, login.js
        └── index.html   # Login page
```

---

## 🔗 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/register` | Register |
| POST | `/api/auth/forgot-password` | Forgot password |
| GET | `/api/tickets` | Get my tickets |
| POST | `/api/tickets` | Raise new ticket |
| GET | `/api/engineer/tickets` | Engineer assigned tickets |
| GET | `/api/teamlead/tickets` | Team lead ticket overview |
| GET | `/api/admin/dashboard` | Admin stats & charts |
| GET | `/api/notifications` | Get notifications |
| GET | `/swagger-ui.html` | API documentation |

---

## 📄 License

MIT License — feel free to use and modify.

---

> Built with ❤️ by [Dnyaneshwar Pawde](https://github.com/Dnyanu-Pawade)
