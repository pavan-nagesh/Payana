package com.payana.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Fetches the public Bengaluru BMTC GTFS-Realtime feed and keeps a short cache. */
@Service
public class BmtcRealtimeService {
  private final ObjectMapper mapper;
  private final HttpClient client;
  private final String vehicleUrl;
  private final String statusUrl;
  private final long cacheTtlSeconds;
  private Map<String, Object> cached = Map.of();
  private Instant cachedAt = Instant.EPOCH;

  public BmtcRealtimeService(ObjectMapper mapper,
      @Value("${payana.realtime.bmtc.vehicle-url}") String vehicleUrl,
      @Value("${payana.realtime.bmtc.status-url}") String statusUrl,
      @Value("${payana.realtime.cache-ttl-seconds:30}") long cacheTtlSeconds) {
    this.mapper = mapper;
    this.vehicleUrl = vehicleUrl;
    this.statusUrl = statusUrl;
    this.cacheTtlSeconds = cacheTtlSeconds;
    this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
  }

  public synchronized Map<String, Object> vehicles() {
    if (!cached.isEmpty() && Duration.between(cachedAt, Instant.now()).toSeconds() < cacheTtlSeconds) {
      return cached;
    }
    try {
      JsonNode feed = getJson(vehicleUrl);
      List<Map<String, Object>> vehicles = new ArrayList<>();
      for (JsonNode entity : feed.path("entity")) {
        JsonNode vehicle = entity.path("vehicle");
        JsonNode position = vehicle.path("position");
        if (!position.has("latitude") || !position.has("longitude")) continue;
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", text(vehicle.path("vehicle").path("id"), entity.path("id").asText("unknown")));
        item.put("label", text(vehicle.path("vehicle").path("label"), "BMTC bus"));
        item.put("routeId", text(vehicle.path("trip").path("routeId"), ""));
        item.put("tripId", text(vehicle.path("trip").path("tripId"), ""));
        item.put("latitude", position.path("latitude").asDouble());
        item.put("longitude", position.path("longitude").asDouble());
        item.put("bearing", position.path("bearing").asDouble(0));
        item.put("timestamp", vehicle.path("timestamp").asLong(0));
        vehicles.add(item);
      }
      long feedTime = feed.path("header").path("timestamp").asLong(0);
      Map<String, Object> result = new LinkedHashMap<>();
      result.put("source", "BMTC GTFS-Realtime");
      result.put("sourceUrl", vehicleUrl);
      result.put("feedTime", feedTime);
      result.put("fetchedAt", Instant.now().toString());
      result.put("stale", false);
      result.put("vehicles", vehicles);
      cached = result;
      cachedAt = Instant.now();
      return result;
    } catch (Exception error) {
      if (!cached.isEmpty()) {
        Map<String, Object> stale = new LinkedHashMap<>(cached);
        stale.put("stale", true);
        stale.put("error", "Upstream BMTC feed temporarily unavailable");
        return stale;
      }
      return Map.of("source", "BMTC GTFS-Realtime", "stale", true, "vehicles", List.of(),
          "error", "Live BMTC feed unavailable");
    }
  }

  public Map<String, Object> status() {
    try {
      JsonNode json = getJson(statusUrl);
      return Map.of("source", "BMTC GTFS-Realtime", "live", true, "status", json);
    } catch (Exception error) {
      return Map.of("source", "BMTC GTFS-Realtime", "live", false, "error", "Status feed unavailable");
    }
  }

  private JsonNode getJson(String url) throws Exception {
    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(15))
        .header("Accept", "application/json")
        .GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new IllegalStateException("Feed returned HTTP " + response.statusCode());
    }
    return mapper.readTree(response.body());
  }

  private static String text(JsonNode node, String fallback) {
    return node.isMissingNode() || node.isNull() || node.asText().isBlank() ? fallback : node.asText();
  }
}
