package com.payana.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Loads the public BMTC network datasets used by Payana's map. */
@Service
public class BmtcNetworkDataService {
  private static final String DATA_ROOT = "https://maps.reclaim.city/bmtc/data/";
  private static final Duration CACHE_TTL = Duration.ofMinutes(10);
  private static final Map<String, String> DATASETS = Map.of(
      "routes", "routes_lite.geojson?v=202611171230",
      "stops", "bus_stops.geojson?v=202611171230",
      "termini", "bus_termini.geojson?v=202611171230",
      "notes", "notes.json?v=202611171230",
      "sources", "sources.json?v=202611171230",
      "timings", "timings.json?v=202611171230",
      "route-stops", "route_stops.json?v=202611171230",
      "metro-lines", "metro_lines.geojson");

  private final ObjectMapper mapper;
  private final HttpClient client = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10)).build();
  private final Map<String, CachedJson> cache = new ConcurrentHashMap<>();

  public BmtcNetworkDataService(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public JsonNode get(String dataset) {
    String resource = DATASETS.get(dataset);
    if (resource == null) {
      throw new IllegalArgumentException("Unknown BMTC dataset: " + dataset);
    }
    CachedJson existing = cache.get(dataset);
    if (existing != null && Duration.between(existing.loadedAt(), Instant.now()).compareTo(CACHE_TTL) < 0) {
      return existing.value();
    }
    try {
      HttpRequest request = HttpRequest.newBuilder(URI.create(DATA_ROOT + resource))
          .timeout(Duration.ofSeconds(20))
          .header("Accept", "application/json, application/geo+json")
          .header("User-Agent", "Payana/1.0 public-BMTC-data-client")
          .GET().build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        throw new IllegalStateException("BMTC dataset returned HTTP " + response.statusCode());
      }
      JsonNode value = mapper.readTree(response.body());
      cache.put(dataset, new CachedJson(value, Instant.now()));
      return value;
    } catch (Exception error) {
      if (existing != null) return existing.value();
      throw new IllegalStateException("BMTC dataset unavailable: " + dataset, error);
    }
  }

  private record CachedJson(JsonNode value, Instant loadedAt) {}
}
