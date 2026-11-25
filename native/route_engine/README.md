# Native route engine

This dependency-free C module demonstrates Payana's transit graph layer.

- `TransitGraph` stores stops and adjacency lists.
- `Edge` stores a destination, travel cost, and next linked-list edge.
- `dijkstra` uses distance, predecessor, and settled arrays.
- `free_graph` releases every allocated edge.

Build and run:

```bash
cc -std=c11 -Wall -Wextra -O2 route_engine.c -o route_engine
./route_engine 0 2
```

The browser route uses OpenStreetMap/OSRM for geocoding and road geometry;
this C module is the readable DSA boundary for a future transit-graph adapter.
