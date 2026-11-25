# Payana project brief

## Outcome

Build an original Bengaluru-first mobility super-app with a map workspace, public transport discovery, train status, flight status, route planning, ride comparison and a local transit assistant.

## Product boundaries

Payana can be inspired by common mobility workflows but must use original branding, layout and code. External services require their own APIs and licenses; local demo data must be clearly labelled.

## Current stack

- Frontend: vanilla HTML/CSS/JavaScript, Leaflet, OpenStreetMap.
- Backend: Java 21, Spring Boot REST API.
- Data: PostgreSQL schema with deterministic demo fallback.
- Algorithms: C graph/Dijkstra module.
- Assistant: local keyword retrieval baseline.

## Acceptance criteria

- One workspace with non-overlapping surfaces.
- Search and route planning remain usable.
- Bus, train, flight and ride modules are accessible from one navigation system.
- Demo mode works without credentials and labels simulated/unavailable live data.
- External data adapters can be added without rewriting the UI.

