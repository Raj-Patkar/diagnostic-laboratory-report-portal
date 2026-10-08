# Diagnostic Laboratory Report Portal

A web-based application for managing patients, diagnostic tests, laboratory reports, and report status through a centralized portal.

The project is being developed as an academic DevOps project. The application provides the functional foundation, while the major focus is on demonstrating a practical DevOps lifecycle including version control, continuous integration, automated testing, containerization, deployment, configuration management, health checks, and recovery.

---

## 1. Project Overview

The Diagnostic Laboratory Report Portal provides a centralized system for managing diagnostic laboratory information.

The portal is designed around the following workflow:

**Patient Registration → Test Registration → Testing → Result Entry → Report Completion → Report Viewing**

Diagnostic tests follow the status workflow:

**Pending → In Progress → Completed**

The system supports different users according to their responsibilities in the laboratory.

---

## 2. User Roles

### Lab Administrator

Responsible for administrative operations such as managing users, viewing reports, monitoring activity, and managing test types.

### Lab Technician

Responsible for registering patients and diagnostic tests, entering test results, and updating test/report status.

### Doctor / Authorized Viewer

Can search and view completed diagnostic reports in view-only mode.

---

## 3. Core Features

The planned MVP includes:

- User authentication and role-based authorization
- Patient management
- Patient search
- Diagnostic test registration
- Test status management
- Laboratory report creation and result management
- Completed report viewing
- Report search and filtering
- Dashboard summary
- Activity logging

---

## 4. Technology Stack

| Component | Technology |
|---|---|
| Frontend | React + Vite |
| Backend | Java + Spring Boot |
| Build Tool | Apache Maven |
| Database | PostgreSQL |
| Database Hosting | Neon PostgreSQL |
| Version Control | Git |
| Repository | GitHub |
| CI/CD | Jenkins |
| Automated Testing | Selenium |
| Containerization | Docker |
| Web Server / Reverse Proxy | Nginx |
| Configuration Management | Ansible / Puppet |

---

## 5. System Architecture

```text
                Users
                  |
                  v
          React + Vite Frontend
                  |
                  | REST API
                  v
          Spring Boot Backend
                  |
                  v
          Neon PostgreSQL