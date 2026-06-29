[CONTRIBUTING.md](https://github.com/user-attachments/files/29460381/CONTRIBUTING.md)
# Contributing to Gym Admin System

Thank you for your interest in contributing to the **Gym Admin System**.

This project is a Spring Boot-based gym management system for small and medium-sized gyms. It includes member management, membership plans, payments, receipts, reports, access control simulation, admin management, SweetAlert messages, CSV export, and future biometric integration support.

This guide explains how to contribute safely and professionally.

---

## Project Scope

The goal of this project is to provide a reliable and practical gym management system.

Current core modules include:

- Admin login
- Admin management
- Admin profile
- Change password
- Member management
- Membership plan management
- Payment management
- Payment receipts
- Access control simulation
- Access logs
- Reports
- CSV export
- Search and filters
- Pagination
- SweetAlert confirmations
- Validation messages
- Local hosting support
- Future biometric device integration support

Contributions should improve stability, usability, security, maintainability, or real-world gym workflow.

---

## Before You Start

Before contributing, make sure you understand the existing system flow.

Good contribution examples:

- Fixing validation bugs
- Improving UI consistency
- Adding missing error messages
- Improving reports
- Improving CSV export
- Improving backup instructions
- Adding tests
- Improving documentation
- Preparing biometric integration carefully

Avoid unnecessary features that make the system too complex for a small gym.

---

## Development Requirements

Install these tools before working on the project:

- Java 17
- Maven
- MySQL Server
- IntelliJ IDEA or another Java IDE
- Git
- Web browser

Recommended:

- MySQL Workbench
- Postman, optional
- GitHub Desktop, optional

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

---

## Setting Up the Project

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/gym-admin-system.git
cd gym-admin-system
```

### 2. Create the database

```sql
CREATE DATABASE gym_admin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure `application.properties`

Create or update:

```text
src/main/resources/application.properties
```

Example local configuration:

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/gym_admin_db?useSSL=false&serverTimezone=Asia/Colombo&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

spring.thymeleaf.cache=false
```

Do not commit real production passwords.

### 4. Run the project

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

Default login:

```text
Username: admin
Password: admin123
```

Change the password after first login when testing real data.

---

## Branching Rules

Use meaningful branch names.

Recommended branch naming:

```text
feature/member-photo-upload
feature/qr-registration
feature/biometric-device-module
fix/payment-pagination-error
fix/member-validation
docs/update-readme
refactor/payment-service
```

Branch types:

| Type | Purpose |
|---|---|
| `feature/` | New feature |
| `fix/` | Bug fix |
| `docs/` | Documentation update |
| `refactor/` | Code improvement without changing behavior |
| `test/` | Test-related changes |
| `security/` | Security improvements |

Do not commit directly to `main` unless you are the project owner and the change is small.

---

## Commit Message Guidelines

Use clear commit messages.

Good examples:

```text
Add CSV export service
Fix duplicate NIC validation
Update member pagination UI
Add SweetAlert confirmation for plan delete
Improve README local hosting section
```

Bad examples:

```text
Update
Fix
Done
Changes
Final
```

Recommended format:

```text
type: short description
```

Examples:

```text
feature: add access log pagination
fix: prevent duplicate member code
docs: add local hosting guide
refactor: clean payment service validation
```

---

## Coding Standards

### Java Standards

Use clear class and method names.

Good:

```java
public List<Member> getExpiredMembers() {
    return memberRepository.findByStatus(MemberStatus.EXPIRED);
}
```

Avoid unclear names:

```java
public List<Member> getData() {
    return memberRepository.findAll();
}
```

### Package Structure

Follow the existing package structure:

```text
lk.janith.gymadmin.controller
lk.janith.gymadmin.dto
lk.janith.gymadmin.entity
lk.janith.gymadmin.repository
lk.janith.gymadmin.security
lk.janith.gymadmin.service
```

### Controller Rules

Controllers should handle:

- Request mappings
- Model attributes
- Redirect attributes
- View names

Controllers should not contain heavy business logic.

### Service Rules

Services should handle:

- Business logic
- Validation
- Data processing
- Transaction-related logic

### Repository Rules

Repositories should handle:

- Database queries
- Spring Data JPA methods
- Custom JPQL queries when needed

### Entity Rules

Entities should represent database tables only.

Do not put complex business logic inside entities.

---

## Database Guidelines

Be careful when changing database-related code.

Before changing entities:

- Check existing table relationships
- Check existing forms
- Check reports
- Check CSV export
- Check search/filter queries

When adding a new field to an entity:

1. Add the field to the entity
2. Update the form if needed
3. Update the list/view page if needed
4. Update validation if needed
5. Update CSV export if needed
6. Test old records
7. Backup the database before production update

For development, this is acceptable:

```properties
spring.jpa.hibernate.ddl-auto=update
```

For stable production, use:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Never delete production data without backup.

---

## Frontend Guidelines

The project uses:

- Thymeleaf
- Bootstrap
- SweetAlert2
- Custom `admin.css`
- Custom `admin.js`

### HTML Guidelines

Use existing layout fragments:

```html
<head th:replace="~{fragments/layout :: head('Page Title')}"></head>
<div th:replace="~{fragments/layout :: sidebar}"></div>
<div th:replace="~{fragments/layout :: topbar('Title', 'Subtitle')}"></div>
<div th:replace="~{fragments/layout :: scripts}"></div>
```

Use existing card style:

```html
<div class="content-card">
    ...
</div>
```

Use existing button style:

```html
class="btn btn-primary action-btn"
```

### SweetAlert Guidelines

For confirmation links, use:

```html
<a href="/example/delete/1"
   class="btn btn-sm btn-outline-danger"
   data-swal-confirm="true"
   data-swal-title="Delete record?"
   data-swal-text="This action cannot be undone."
   data-swal-confirm-button="Yes, delete">
    Delete
</a>
```

For success/error messages, use controller flash attributes:

```java
redirectAttributes.addFlashAttribute("successMessage", "Saved successfully.");
redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong.");
```

---

## Security Guidelines

Do not commit sensitive data.

Never commit:

- Real database passwords
- Real admin passwords
- `.env` files
- `application-prod.properties`
- SQL backup files
- Real member data
- Real NIC numbers
- Real phone numbers
- Server credentials
- Private keys
- API keys

Recommended `.gitignore` entries:

```gitignore
target/
*.log
logs/
backups/
*.sql
*.db

.env
application-prod.properties

.idea/
*.iml
.DS_Store
```

If a password was accidentally committed:

1. Remove it from the code
2. Change the real password immediately
3. Clean Git history if needed
4. Do not assume deleting the latest commit is enough

---

## Testing Checklist

Before submitting a contribution, test the affected area.

### Admin

- Login works
- Logout works
- Admin list loads
- Admin save works
- Admin delete confirmation works
- Change password works
- Duplicate username/email validation works

### Members

- Add member
- Edit member
- Delete member
- Search member
- Filter by status
- Filter by gender
- Pagination works
- Duplicate member code validation works
- Duplicate NIC validation works

### Plans

- Add plan
- Edit plan
- Delete plan
- Duplicate plan validation works
- Invalid price validation works
- Invalid duration validation works

### Payments

- Add payment
- Receipt page works
- Cancel payment
- Delete payment
- Search payment
- Filter payment
- Pagination works

### Access Control

- Valid active member gets granted access
- Expired member gets denied access
- Suspended member gets denied access
- Unknown member gets denied access
- Access log is saved
- Access log search works
- Access log pagination works

### Reports

- Daily income report works
- Monthly income report works
- Expired members report works
- Access report works
- CSV export downloads properly

### UI

- SweetAlert success message works
- SweetAlert error message works
- Delete confirmations work
- Layout does not break on smaller screens

---

## Pull Request Process

Before creating a pull request:

1. Make sure the project runs locally
2. Make sure the database connects properly
3. Test your changed module
4. Check that no secrets are committed
5. Keep the pull request focused
6. Explain what you changed clearly

Pull request description should include:

```text
## What changed?

## Why was this needed?

## How was it tested?

## Screenshots, if UI changed

## Notes or risks
```

Example:

```text
## What changed?
Added SweetAlert confirmation to membership plan delete button.

## Why was this needed?
The old browser confirmation looked unprofessional and inconsistent.

## How was it tested?
Tested delete confirmation on the plans page and confirmed success message after delete.
```

---

## Reporting Bugs

When reporting a bug, include:

- What you were trying to do
- What happened
- What you expected to happen
- Screenshot, if available
- Console error, if available
- Steps to reproduce the problem

Good bug report:

```text
When I click Delete Payment, the confirmation appears, but after confirming it redirects to an error page.

Steps:
1. Open /payments
2. Click Delete on a paid payment
3. Confirm SweetAlert popup
4. Error page appears

Expected:
Payment should be deleted or show a proper error message.
```

---

## Suggesting Features

Before suggesting a feature, think about whether it is useful for a real gym.

Good feature ideas:

- QR member registration
- Member photo upload
- Backup and restore page
- Biometric device import
- Staff role restrictions
- Member card printing
- Payment reminders

Avoid features that add complexity without real benefit.

Feature suggestion format:

```text
## Feature Name

## Problem

## Proposed Solution

## Who benefits?

## Priority
Low / Medium / High
```

---

## Future Contribution Areas

Possible future improvements:

- Public QR member registration
- Pending member applications
- Member photo upload
- Print member card
- Biometric device module
- USB access log import
- ZKTeco device sync
- Local hosting setup script
- Backup and restore UI
- Multi-branch support
- Role-based staff permissions
- Excel export
- SMS or WhatsApp payment reminders
- Member mobile app or PWA

---

## Code of Conduct

Be respectful and professional.

When contributing:

- Do not insult others
- Do not break existing features intentionally
- Do not upload real private data
- Do not expose passwords or credentials
- Focus on helpful improvements
- Keep communication clear and respectful

---

## Maintainer Notes

This project is currently maintained by:

```text
Janith Dasanayaka
Sri Lanka
```

The project is intended for educational, practical, and real-world small gym usage.

---

## Final Reminder

Before publishing or deploying:

```text
Do not expose real passwords.
Do not commit database backups.
Do not publish real member data.
Always backup before production changes.
Test locally before deployment.
```
