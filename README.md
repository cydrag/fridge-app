# 🧊 Fridge Tracker API

## 📖 The Story
This application is designed to help users manage their fridge contents and track expiry dates. It features a robust Public/Private fridge system, allowing for both personal household management and shared community pantries.

## 🚀 Key Features
- **Public & Private Fridges:** granular access control using Spring Security.
- **Zero-Wait Concurrency:** Uses Optimistic Locking (`@Version`) to handle multiple users editing the same fridge.
- **Production Ready:** Database migrations via Flyway ensure consistent environments.

## 🛠 Tech Stack
- Language: Java 21+
- Framework: Spring Boot 4.x
- Build Tool: Maven
- Database: PostgreSQL 18
- ORM: Spring Data JPA (Hibernate)
- Security: Spring Security + JWT (Stateless)
- Migrations: Flyway
- Containerization: Docker Compose

---

## ⚡ Quick Start

### Prerequisites
- Java 21 or higher
- Maven
- Docker & Docker Compose

### Instructions

1. Start the Infrastructure in one command:
```bash
docker-compose up -d
```

2. Run the Application
```bash
mvn spring-boot:run
```

*The application will start on http://localhost:8080 by default.*

---

## 🐳 Docker Services
- Postgres: Port `5432`