# 🎮 Camel Board Game – Team B Project

A real-time multiplayer board game built with a **React + Typescript frontend** and **Spring Boot backend**. 
Designed for interactive gameplay with websocket support and REST APIs.

---

# 📚 Project Navigation

- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Local Development](#local-development)
- [Production Build](#production-build)
- [OpenAPI Swagger](#openapi-swagger)
- [Running Backend Tests](#running-backend-tests)
---

## 📁 Project Structure

```
team-b/
├── backend/      # Spring Boot application
├── frontend/     # React TypeScript application
└── docker-compose.yml
```

---

## 🚀 Getting Started

### 🔧 Requirements

- Java 21+
- Maven
- Node.js (v20+)
- npm or yarn

---
## 🧪 Local Development

You can either run the project using Docker or build and run it manually.

### 🐳 Run with Docker

```bash
docker compose build
docker compose up
```

This will set up both the backend and frontend services.  
- Backend: `http://localhost:8080`  
- Frontend: `http://localhost:80`

---

### ▶️ Run Manually

#### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend will be available at: `http://localhost:8080`

---

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at: `http://localhost:80`

---

## 📦 Production Build

### 🏗 Backend (JAR Build)

```bash
cd backend
mvn clean package
```

Output: `target/CamelUp-backend-1.0-SNAPSHOT.jar`

### 🏗 Frontend (Static Build)

```bash
cd frontend
npm run build
```

Output: `frontend/build/`

---

## 🌐 OpenAPI Swagger

The backend provides an OpenAPI (Swagger) UI for exploring and testing the REST API endpoints.
- After starting the backend, access Swagger UI at: `http://localhost:8080/swagger-ui.html`

---

## ⚙️ Frontend Port & Environment

- The frontend is configured to run on port **80** by default, as it is prepared for deployment in production environments (see `frontend/Dockerfile` and `docker-compose.yml`).
- **If you want to run the frontend locally on a different port or in development mode:**
  1. Create a `.env` file in the `frontend/` directory (see `.env.example`).
  2. Set `REACT_APP_LOCAL=true` in your `.env` file.
  3. You can change the port in the Dockerfile or docker-compose if needed.
- Default URLs:
  - Backend: `http://localhost:8080`
  - Frontend: `http://localhost:80`

---

## 🧪 Running Backend Tests

To run all backend tests using Maven:

```bash
cd backend
mvn test
```

Test classes are located in `backend/src/test/java/tuc/isse/services/`:
- [CamelMovementServiceTest.java](backend/src/test/java/tuc/isse/services/camel/CamelMovementServiceTest.java)
- [GameActionsServiceTest.java](backend/src/test/java/tuc/isse/services/game/GameActionsServiceTest.java)
- [GameCreationServiceTest.java](backend/src/test/java/tuc/isse/services/game/GameCreationServiceTest.java)
- [GameParticipationServiceTest.java](backend/src/test/java/tuc/isse/services/game/GameParticipationServiceTest.java)

Test reports are generated in `backend/target/surefire-reports/`.

# camel-up
