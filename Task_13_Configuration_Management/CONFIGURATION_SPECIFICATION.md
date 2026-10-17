# Task 13 — Configuration Specification
## Diagnostic Laboratory Report Portal

### Target and scope
- Tool: Ansible.
- Target: Ubuntu/Debian Linux deployment node (not the current Windows workstation).
- Application: React + Vite frontend, Java 21 Spring Boot/Maven backend, Neon PostgreSQL.
- Frontend: NGINX static hosting and reverse proxy.

### Prerequisite specification
| Category | Required state |
|---|---|
| Packages | `openjdk-21-jre-headless`, `nginx`, `curl`, `ca-certificates`, `unzip` |
| User | System account `portal`, non-interactive shell |
| Application directories | `/opt/diagnostic-lab-portal`, `backend/`, `frontend/`, `logs/`, `releases/` |
| Web directory | `/var/www/diagnostic-lab-portal`, owned by `www-data` |
| Configuration file | `/etc/nginx/sites-available/diagnostic-lab-portal` |
| Enabled site | Symlink `/etc/nginx/sites-enabled/diagnostic-lab-portal` |
| Services | NGINX enabled and running; configuration tested before reload |
| Ports | TCP 80 for NGINX; TCP 8085 for Spring Boot upstream. Jenkins 8080 and Vite 5173 are documented only and are not opened by this playbook. |
| Database | Neon PostgreSQL is external; no local PostgreSQL package or port required |

### Environment note
The developer workstation is Windows and currently uses Windows Jenkins/NGINX plus a Linux Docker backend. To keep the first execution quick, the included inventory targets `localhost` and is intended to run inside Ubuntu on WSL. This demonstrates configuration management on a Linux target environment; it does not configure Windows services. Run the playbook from WSL and use sudo/become when prompted.

### Execution and evidence
Run syntax validation, then execute against the real target. Save actual terminal output as `evidence/task13-first-execution.log`. Do not report successful execution until the real command exits successfully.
