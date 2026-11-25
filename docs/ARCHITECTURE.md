# Payana architecture

## Request flow

Browser -> Leaflet/OpenStreetMap
       -> Java REST API (/api)
       -> PostgreSQL (stops, routes, documents)
       -> C route engine (future native shortest-path adapter)
       -> local RAG service (keyword retrieval over travel documents)

### Live BMTC feed

`BmtcRealtimeService` fetches the public Bengaluru GTFS-Realtime vehicle JSON feed, normalizes each vehicle to latitude, longitude, bearing, route and timestamp, and caches the last successful response for 30 seconds. The frontend polls `/api/realtime/buses` every 20 seconds and draws vehicles on the OpenStreetMap layer. Stale or unavailable data is shown explicitly; it is never replaced with fake live positions.

Train, flight and ride services use the same provider boundary but remain credential-backed: railway data, a FlightRadar24 API token, and approved ride-partner credentials are required before those surfaces can claim live status or fares.

## DBMS

PostgreSQL stores stops, routes and knowledge documents. Schema is in backend/src/main/resources/db/schema.sql. The Java service keeps a small in-memory stop index for local development while route geometry comes from the OpenStreetMap/OSRM adapter.

## Computer networking

The frontend is a separate HTTP origin during development. CORS is restricted to the local frontend ports in WebConfig. Production should serve the static frontend behind the same reverse proxy as the API, enable HTTPS, and expose WebSocket/SSE for live vehicle updates.

## RAG

KnowledgeService is a deliberately small baseline: it retrieves the document with the highest keyword overlap and returns answer, confidence and source. It is designed so a vector index/embedding provider can replace the retrieval method later without changing /api/knowledge.

## DSA

The C module uses an adjacency-list graph and Dijkstra's shortest-path algorithm. It is independently compilable and is the starting point for a native route-planning adapter.

## OOP

The Java backend separates controllers, services and immutable records. Controllers own HTTP concerns, services own business rules, and model records own API contracts.

