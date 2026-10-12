# Diagnostic Laboratory Report Portal

A web-based application for managing patients, diagnostic tests, laboratory reports, and report status through a centralized portal.

This project is being developed as an academic DevOps project. The application provides the functional foundation, while the broader objective is to demonstrate a practical DevOps lifecycle through version control, continuous integration, automated testing, containerization, deployment, configuration management, health checks, and recovery.

## 1. Project Overview

The Diagnostic Laboratory Report Portal (DiagLab) centralizes common diagnostic laboratory workflows in a single web application.

The intended workflow is:

**Patient Registration → Test Registration → Testing → Result Entry → Report Completion → Report Viewing**

Diagnostic tests follow this status workflow:

**Pending → In Progress → Completed**

The application uses authenticated access and role-based authorization to control access to protected features.

## 2. User Roles

### Lab Administrator

- Accesses the administrative dashboard.
- Reviews laboratory records and reports.
- Views the activity log.
- Manages staff accounts through the User Management page.
- Can create Doctor and Lab Technician accounts.

### Lab Technician

- Works with patient records and laboratory tests according to assigned permissions.
- Tracks test-processing status.
- Supports the laboratory report workflow.

### Doctor / Authorized Viewer

- Accesses permitted diagnostic information and reports according to the configured role permissions.
- Uses the portal to review report information.

> Access to individual operations is enforced by the backend. The exact permissions available to each role depend on the application's security configuration.

## 3. Application Features

The current MVP brings together the following modules:

- **Authentication:** Login, logout, and session-based authentication.
- **Role-based access control:** Protected API endpoints and role-specific access.
- **Dashboard:** Summary counts for patients, laboratory tests, reports created, and final reports.
- **Patient Management:** Create, list, and search patient records.
- **Laboratory Test Management:** Register tests, review test information, and update processing status.
- **Diagnostic Reports:** Create and review reports, manage results and reference ranges, and finalize reports.
- **Activity Log:** Administrative visibility into recorded application activities.
- **User Management:** Administrator-only listing and registration of Doctor and Lab Technician accounts.
- **Input validation:** Validation of submitted data and rejection of duplicate staff usernames.
- **Password protection:** Passwords are hashed with BCrypt rather than stored as plain text.

The portal is an academic MVP and is not intended to replace a production clinical information system.

## 4. Technology Stack

| Component | Technology |
|---|---|
| Frontend | React + Vite |
| Backend | Java + Spring Boot |
| Backend Build Tool | Apache Maven |
| Database | PostgreSQL |
| Database Hosting | Neon PostgreSQL |
| Authentication | Spring Security, session-based authentication |
| Password Hashing | BCrypt |
| API Testing | Postman |
| Version Control | Git |
| Source Code Hosting | GitHub |
| Continuous Integration / Delivery | Jenkins — planned project activity |
| Automated UI Testing | Selenium — planned project activity |
| Containerization | Docker — planned project activity |
| Web Server / Reverse Proxy | Nginx — planned project activity |
| Configuration Management | Ansible / Puppet — planned project activity |

> The stack table identifies the intended DevOps tools. A tool should be described as implemented only after its configuration and execution have been demonstrated with project evidence.

## 5. System Architecture

```text
                    Laboratory Users
                           |
                           v
                  React + Vite Frontend
                    (localhost:5173)
                           |
                           | REST API / HTTP
                           v
                 Spring Boot Backend
                    (localhost:8085)
                           |
                           v
                    Neon PostgreSQL
                     (Hosted Database)

       Source Control: Git + GitHub
       DevOps Extension: Jenkins, Selenium,
       Docker, Nginx, and Ansible/Puppet
```

### Architecture Components

- **Frontend:** React provides the user interface, with Vite used for development and frontend builds.
- **Backend:** Spring Boot exposes REST endpoints for authentication and laboratory operations. Maven manages the Java build.
- **Database:** PostgreSQL hosted through Neon stores application records.
- **Security:** Spring Security protects backend endpoints and applies role-based authorization. Session cookies support authenticated requests.
- **DevOps workflow:** Git and GitHub provide source control. Jenkins, Selenium, Docker, Nginx, and configuration-management tools are part of the broader DevOps project scope and should be documented as completed only when verified.

## 6. Repository Structure

The main project structure is organized into frontend and backend applications.

```text
diagnostic-laboratory-report-portal/
├── frontend/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/diaglab/portal/
│   │   └── test/
│   ├── pom.xml
│   └── mvnw
└── README.md
```

Additional files may exist depending on local configuration and the DevOps tasks completed. Environment files, credentials, and other secrets must not be committed to Git.

## 7. Prerequisites

Install the following before running the application:

- Java 21 or a compatible version required by the backend project.
- Node.js and npm.
- Git.
- Access to a PostgreSQL database, such as a Neon database.
- Maven Wrapper files included in the backend repository.

Jenkins, Docker, Selenium, Nginx, and Ansible/Puppet are required only for the corresponding DevOps tasks.

## 8. Configuration

### Backend Database Configuration

Configure the backend environment variables in your local environment:

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |

The application may also support the following optional variables for initial demo accounts, if configured in the backend:

| Variable | Purpose |
|---|---|
| `APP_ADMIN_PASSWORD` | Initial administrator account password |
| `APP_TECHNICIAN_PASSWORD` | Initial technician account password |
| `APP_DOCTOR_PASSWORD` | Initial doctor account password |

Use the exact variable names expected by the current backend configuration. Do not put real credentials in this README, source code, screenshots, or Git commits.

A `.env` file is not automatically loaded by Spring Boot unless the project explicitly configures a loader. Ensure that variables are supplied through the method configured for your local environment.

## 9. Running the Application Locally

Open two terminals from the repository root.

### Terminal 1: Start the Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend has been configured for local development on port **8085**.

### Terminal 2: Start the Frontend

```powershell
cd frontend
npm install
npm run dev
```

Open the Vite URL printed by the terminal. The current local frontend configuration uses:

```text
http://localhost:5173
```

The frontend and backend must both be running, and the backend must have valid database configuration.

## 10. Main Application Workflows

### Patient Management

1. Sign in with an authorized account.
2. Open the Patients page.
3. Add a patient record or search existing records.
4. Confirm that the record appears in the list.

### Laboratory Test Management

1. Open Lab tests.
2. Register a test against an existing patient.
3. Review the requested test and assigned staff information.
4. Update the processing status as permitted.

### Diagnostic Report Management

1. Open Reports.
2. Create a report for an eligible laboratory test.
3. Enter the result and reference information.
4. Finalize the report when the workflow requirements are met.
5. Confirm the report status in the list.

### Administrative User Management

1. Sign in as an administrator.
2. Open User management.
3. Register a Doctor or Lab Technician account.
4. Confirm that the new account appears in the list.
5. Verify that duplicate usernames are rejected and that non-administrative users cannot access the protected user-management API.

### Activity Log

Administrators can review recorded application activities to support traceability of selected operations.

## 11. Testing and Verification

Testing should be recorded with actual execution evidence. The following checks are relevant to the current MVP:

- Successful login and logout.
- Protected API access after authentication.
- Patient creation and search.
- Laboratory test creation and status updates.
- Report creation and finalization.
- Activity log access according to role.
- Staff account creation by an administrator.
- Rejection of duplicate usernames.
- Authorization checks for non-administrator access to user management.
- Backend tests and frontend production build.

Useful commands include:

**Backend tests**

```powershell
cd backend
.\mvnw.cmd test
```

**Frontend production build**

```powershell
cd frontend
npm run build
```

Record each command's actual result. Do not mark a test or build as successful unless it completes successfully.

## 12. DevOps Lifecycle Plan

The project is intended to demonstrate DevOps practices progressively.

| Stage | Intended activity | Evidence to capture |
|---|---|---|
| Version control | Feature branches, meaningful commits, and GitHub synchronization | Branch list, commit history, successful push |
| Build automation | Build the Spring Boot backend and React frontend | Successful Maven and frontend build output |
| Continuous integration | Configure a Jenkins pipeline to retrieve source code and execute build/test stages | Jenkinsfile, pipeline configuration, successful build |
| Automated testing | Automate selected application workflows using Selenium | Test scripts, execution output, screenshots |
| Containerization | Build and run application containers using Docker | Dockerfile or Compose configuration, container status |
| Deployment | Deploy the application using the selected hosting approach | Deployment configuration and successful application access |
| Reverse proxy | Configure Nginx if included in the deployment design | Configuration file and routing verification |
| Configuration management | Automate machine or service configuration using Ansible or Puppet, as selected | Playbook/manifest and execution output |
| Health checks | Verify application and service availability | Health-check configuration and responses |
| Recovery | Demonstrate recovery from a documented failure scenario | Failure, recovery steps, and successful verification |

These are project objectives, not claims that every stage has already been implemented.

## 13. Security and Configuration Notes

- Never commit `.env` files, database credentials, or passwords.
- Keep secrets in environment variables or an appropriate secret-management mechanism.
- Do not expose password hashes in API responses.
- Keep administrative endpoints protected by backend authorization.
- Use HTTPS and production-appropriate session-cookie settings in any deployed environment.
- Review CSRF protection, CORS settings, error handling, and database access before production use.
- Use synthetic patient information for academic demonstrations; do not include real patient data in public repositories or screenshots.

## 14. Current MVP Status

The current application includes a dashboard, patient management, laboratory test management, diagnostic reports, authentication, role-based access, activity logging, and administrative staff-account management.

The dashboard, patient list, lab test list, and reports page have been captured as application evidence. Build results, automated tests, Jenkins execution, containerization, deployment, configuration management, health checks, and recovery should be recorded separately as those tasks are carried out and verified.

## 15. Project Outcome

The Diagnostic Laboratory Report Portal establishes a working application foundation for the academic DevOps project. It brings the principal laboratory workflows into a centralized interface and provides a codebase on which the DevOps lifecycle can be demonstrated.

The next stages focus on turning the application workflow into a repeatable and verifiable delivery process through version control, automated builds, testing, CI/CD, deployment, configuration management, health checks, and recovery.

## Author

**Raj Patkar**  
B.Tech Computer Engineering  
Vidyalankar Institute of Technology, Mumbai

## Repository

[GitHub – Diagnostic Laboratory Report Portal](https://github.com/Raj-Patkar/diagnostic-laboratory-report-portal)
