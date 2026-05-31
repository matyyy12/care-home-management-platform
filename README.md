# Senior House App

This application consists of:
- `frontend` - Vue 3 + Vite + Tailwind (local dev server)
- `backend` - Spring Boot + Maven (Docker)
- `database` - PostgreSQL (Docker)

## Requirements

- Node.js `22.x`
- npm `10+`
- Java `17` (only if you run backend tests locally without Docker)
- Docker

## Quick Start

From the project root, start the database and backend:

```sh
docker compose up --build
```

In a second terminal, start the frontend dev server:

```sh
cd frontend
npm install
npm run dev
```

After startup:
- frontend: `http://localhost:5173`
- backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- postgres: `localhost:5432`

The Vite dev server proxies `/api` requests to the backend on port `8080`.

### Frontend

```sh
cd frontend
npm install
npm run dev
npm run lint
npm run build
```

### Backend

```sh
cd backend
./mvnw.cmd test
```

### Backend tests in Docker

```sh
docker compose --profile test run --rm --build backend-test
```
