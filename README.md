# Senior House App

This application consists of:
- `frontend` - Vue 3 + Vite + Tailwind
- `backend` - Spring Boot + Maven
- `database` - PostgreSQL (locally via Docker)

## Requirements

- Node.js `22.x`
- npm `10+`
- Java `17`
- Docker

## Quick Start

From the project root:

```sh
docker compose up --build
```

After startup:
- frontend: `http://localhost:5173`
- backend: `http://localhost:8080`
- postgres: `localhost:5432`


### Frontend

```sh
cd frontend
npm install
npm run lint
npm run build
```

### Backend

```sh
cd backend
./mvnw.cmd test
```
