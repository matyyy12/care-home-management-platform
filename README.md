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
- postgres: `localhost:5432`

The Vite dev server proxies `/api` requests to the backend on port `8080`.

### API vs dane demo

Moduły **Seniorzy**, **Stanowiska** i **Adresy / poczty** ładują dane z backendu (`/api/...`).
Gdy backend nie działa lub `VITE_USE_API=false`, widoki automatycznie przełączają się na `mock/data.js`.

Pozostałe ekrany (pulpit, pracownicy, pomieszczenia, raporty itd.) nadal korzystają z mocków —
backend nie ma jeszcze dla nich endpointów.

Skopiuj `frontend/.env.example` do `frontend/.env` i dostosuj zmienne w razie potrzeby.

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
docker compose --profile test run --rm backend-test
```
