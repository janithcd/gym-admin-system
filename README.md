# Gym Admin System

A professional **Spring Boot Gym Management and Access Control System** built for a small or medium-sized gym.
The system manages members, membership plans, payments, receipts, access checks, access logs, reports, CSV exports, admin users, profile management, validation messages, and SweetAlert-based confirmations.

This project is designed to work first as a **local-hosted gym system** and can later be upgraded for cloud hosting, QR member registration, mobile access, multi-branch support, or biometric device integration.

## Main Features

* Admin login and logout
* Admin user management
* Admin profile and change password
* Member management
* Membership plan management
* Payment management
* Payment receipt printing
* Access control simulation
* Access logs
* Reports
* CSV export
* Search and filters
* Pagination
* SweetAlert confirmations
* Validation messages
* Automatic member expiry update
* Old access log cleanup
* Future biometric integration support

## Technology Stack

| Layer           | Technology                      |
| --------------- | ------------------------------- |
| Language        | Java 17                         |
| Framework       | Spring Boot                     |
| Security        | Spring Security                 |
| ORM             | Spring Data JPA / Hibernate     |
| Database        | MySQL                           |
| Template Engine | Thymeleaf                       |
| UI Framework    | Bootstrap                       |
| Charts          | Chart.js                        |
| Alerts          | SweetAlert2                     |
| Build Tool      | Maven                           |
| IDE             | IntelliJ IDEA                   |
| Hosting Target  | Local PC / Mini PC / Future VPS |

## Default Admin Login

```text
Username: admin
Password: admin123
```

After first login, change the password from:

```text
My Profile → Change Password
```

## Important URLs

| Page             | URL             |
| ---------------- | --------------- |
| Login            | `/login`        |
| Dashboard        | `/`             |
| Members          | `/members`      |
| Add Member       | `/members/new`  |
| Membership Plans | `/plans`        |
| Payments         | `/payments`     |
| Add Payment      | `/payments/new` |
| Access Scan      | `/access/scan`  |
| Access Logs      | `/access/logs`  |
| Reports          | `/reports`      |
| Admin Users      | `/admins`       |
| My Profile       | `/profile`      |

## Local Run

Create database:

```sql
CREATE DATABASE gym_admin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Run project:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## Build JAR

```bash
mvn clean package -DskipTests
```

## Local Hosting

Recommended setup:

```text
Server PC
├── Java 17
├── MySQL
├── Spring Boot JAR
├── Daily backup
└── Static IP
```

Access from server PC:

```text
http://localhost:8080
```

Access from other devices on same network:

```text
http://SERVER-IP:8080
```

Example:

```text
http://192.168.1.10:8080
```

## Future Biometric Integration

The system already has a `deviceUserId` field for future biometric integration.

Recommended method:

```text
Gym Admin System stores:
- Member code
- Device user ID
- Status
- Payments
- Access logs

Biometric device stores:
- Fingerprint template
- Face template
- Card/password data
```

Do not store fingerprint images or biometric templates directly in the MySQL database unless there is a strong legal, security, and technical reason to do so.

## Project Status

```text
Core system completed.
Ready for local hosting and real gym testing.
Future biometric integration can be added after device testing.
```

::: 
