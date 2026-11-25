/*
 * Payana native route engine
 *
 * Build:  cc -std=c11 -Wall -Wextra -O2 route_engine.c -o route_engine
 * Run:    route_engine [start] [goal]
 *
 * DSA used: adjacency lists, Dijkstra's shortest path, and predecessor-based
 * route reconstruction. The web route uses OpenStreetMap/OSRM for geometry;
 * this dependency-free module is the readable transit-graph engine.
 */
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>

#define STOP_COUNT 6
#define INF (INT_MAX / 4)

typedef struct Edge { int destination; int minutes; struct Edge *next; } Edge;
typedef struct { const char *name; Edge *outgoing; } Stop;
typedef struct { Stop stops[STOP_COUNT]; } TransitGraph;

static void add_edge(TransitGraph *graph, int from, int to, int minutes) {
    Edge *edge = malloc(sizeof(*edge));
    if (!edge) { fprintf(stderr, "out of memory\n"); exit(EXIT_FAILURE); }
    edge->destination = to; edge->minutes = minutes;
    edge->next = graph->stops[from].outgoing; graph->stops[from].outgoing = edge;
}

static void add_two_way_edge(TransitGraph *graph, int a, int b, int minutes) {
    add_edge(graph, a, b, minutes); add_edge(graph, b, a, minutes);
}

static int cheapest_unvisited(const int distance[], const int settled[]) {
    int selected = -1;
    for (int i = 0; i < STOP_COUNT; ++i)
        if (!settled[i] && distance[i] < (selected < 0 ? INF : distance[selected])) selected = i;
    return selected;
}

static void print_path(const TransitGraph *graph, const int previous[], int goal) {
    int path[STOP_COUNT], length = 0;
    for (int current = goal; current >= 0 && length < STOP_COUNT; current = previous[current]) path[length++] = current;
    for (int i = length - 1; i >= 0; --i) printf("%s%s", graph->stops[path[i]].name, i ? " -> " : "\n");
}

static void dijkstra(const TransitGraph *graph, int start, int goal) {
    int distance[STOP_COUNT], previous[STOP_COUNT], settled[STOP_COUNT] = {0};
    for (int i = 0; i < STOP_COUNT; ++i) { distance[i] = INF; previous[i] = -1; }
    distance[start] = 0;
    for (int step = 0; step < STOP_COUNT; ++step) {
        int current = cheapest_unvisited(distance, settled);
        if (current < 0) break;
        settled[current] = 1;
        for (Edge *edge = graph->stops[current].outgoing; edge; edge = edge->next) {
            int candidate = distance[current] + edge->minutes;
            if (candidate < distance[edge->destination]) { distance[edge->destination] = candidate; previous[edge->destination] = current; }
        }
    }
    if (distance[goal] == INF) { puts("no route"); return; }
    printf("cost_minutes=%d\npath=", distance[goal]); print_path(graph, previous, goal);
}

static void free_graph(TransitGraph *graph) {
    for (int i = 0; i < STOP_COUNT; ++i) {
        Edge *edge = graph->stops[i].outgoing;
        while (edge) { Edge *next = edge->next; free(edge); edge = next; }
    }
}

int main(int argc, char **argv) {
    TransitGraph graph = { .stops = {
        {"Majestic", NULL}, {"Shivajinagar", NULL}, {"Silk Board", NULL},
        {"Yeshwanthpur", NULL}, {"Airport", NULL}, {"Electronic City", NULL} } };
    add_two_way_edge(&graph, 0, 1, 8); add_two_way_edge(&graph, 1, 2, 6);
    add_two_way_edge(&graph, 0, 3, 4); add_two_way_edge(&graph, 3, 4, 7);
    add_two_way_edge(&graph, 4, 2, 5); add_two_way_edge(&graph, 2, 5, 12);
    int start = argc > 1 ? atoi(argv[1]) : 0, goal = argc > 2 ? atoi(argv[2]) : 2;
    if (start < 0 || start >= STOP_COUNT || goal < 0 || goal >= STOP_COUNT) {
        fprintf(stderr, "stop indexes must be between 0 and %d\n", STOP_COUNT - 1); free_graph(&graph); return EXIT_FAILURE;
    }
    dijkstra(&graph, start, goal); free_graph(&graph); return EXIT_SUCCESS;
}
