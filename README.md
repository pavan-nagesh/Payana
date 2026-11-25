# Payana

Payana is an original Bengaluru travel-information system built from scratch. It combines an OpenStreetMap/Leaflet frontend, a Java/Spring Boot API, a PostgreSQL schema, a C route-engine exercise, network documentation, and a small local keyword-RAG service.

## Project layout

- `frontend/` — original map and journey interface; no copied reference runtime.
- `backend/` — Java 21 Spring Boot REST API with OOP domain/services.
- `native/route_engine/` — C adjacency-list and Dijkstra implementation.
- `docs/` — architecture, database, networking and RAG notes.

## Run the frontend

```powershell
python -m http.server 5500 --directory frontend
```

Open `http://localhost:5500`. When the Java API is running, the map shows live BMTC positions and refreshes every 20 seconds. If the API is unavailable, Payana reports that state instead of inventing live positions.

## Run the Java API

Start PostgreSQL with `docker compose up -d postgres`, then run the Spring Boot application from `backend` with Maven. The API listens on `http://localhost:8080`.

Key endpoints: `GET /api/stops`, `GET /api/nearby?lat=...&lon=...`, `POST /api/journeys`, and `GET /api/knowledge?question=...`.

Live mobility endpoints: `GET /api/realtime/buses` and `GET /api/realtime/status`. For a local smoke test without PostgreSQL, run the packaged jar with `--spring.sql.init.mode=never`; the full DBMS setup uses `docker compose up -d postgres`.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the DBMS, computer-networking and RAG design.
