# Payana project structure

The active application is intentionally small and original.

- frontend/index.html — frontend shell and accessible controls.
- frontend/styles.css — responsive interface and layout.
- frontend/app.js — Leaflet map, journey planner, train/flight surfaces and API client.
- backend/pom.xml — Java 21 Spring Boot build.
- backend/src/main/java/com/payana/model — immutable API/domain records.
- backend/src/main/java/com/payana/service — journey planning and local RAG services.
- backend/src/main/java/com/payana/api — REST controllers.
- backend/src/main/resources/db/schema.sql — PostgreSQL schema.
- native/route_engine/route_engine.c — readable C adjacency-list/Dijkstra engine.
- native/route_engine/README.md — build and DSA explanation for the native module.
- docs/ARCHITECTURE.md — DBMS, networking, OOP, DSA and RAG design.
The frontend uses Leaflet and OpenStreetMap tiles. It works with fallback data without the backend and uses Java API endpoints when the backend is running.

