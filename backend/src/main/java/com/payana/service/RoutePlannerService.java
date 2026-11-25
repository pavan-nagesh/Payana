package com.payana.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Uses public OpenStreetMap geocoding and OSRM routing for real route geometry. */
@Service
public class RoutePlannerService {
  private final ObjectMapper mapper;
  private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

  public RoutePlannerService(ObjectMapper mapper) { this.mapper = mapper; }

  public Map<String, Object> plan(String from, String to, String mode) {
    try {
      double[] origin = geocode(from);
      double[] destination = geocode(to);
      String url = "https://router.project-osrm.org/route/v1/driving/" + origin[1] + "," + origin[0]
          + ";" + destination[1] + "," + destination[0] + "?overview=full&geometries=geojson&steps=true";
      JsonNode route = get(url).path("routes").path(0);
      if (route.isMissingNode()) throw new IllegalStateException("No route found");

      List<List<Double>> coordinates = new ArrayList<>();
      for (JsonNode point : route.path("geometry").path("coordinates")) {
        coordinates.add(List.of(point.path(1).asDouble(), point.path(0).asDouble()));
      }
      double km = route.path("distance").asDouble() / 1000.0;
      long minutes = Math.max(1, Math.round(route.path("duration").asDouble() / 60.0));
      Map<String, Object> result = new LinkedHashMap<>();
      result.put("from", from);
      result.put("to", to);
      result.put("mode", mode == null ? "bus" : mode);
      result.put("distanceKm", Math.round(km * 10.0) / 10.0);
      result.put("durationMinutes", minutes);
      result.put("geometry", coordinates);
      result.put("source", "OpenStreetMap + OSRM");
      result.put("options", List.of(Map.of("mode", "Road", "title", from + " → " + to,
          "detail", String.format("Open route · %.1f km · OSRM", km), "duration", minutes + " min")));
      return result;
    } catch (Exception error) {
      return Map.of("options", List.of(), "source", "OpenStreetMap + OSRM", "error",
          "Could not find a route. Try specific place or stop names.");
    }
  }

  private double[] geocode(String place) throws Exception {
    String query = URLEncoder.encode(place + ", Bengaluru, India", StandardCharsets.UTF_8);
    JsonNode first = get("https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=" + query).path(0);
    if (first.isMissingNode()) throw new IllegalArgumentException("Place not found: " + place);
    return new double[]{first.path("lat").asDouble(), first.path("lon").asDouble()};
  }

  private JsonNode get(String url) throws Exception {
    HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(20))
        .header("Accept", "application/json").header("User-Agent", "Payana Bengaluru mobility app")
        .GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() < 200 || response.statusCode() >= 300) throw new IllegalStateException("HTTP " + response.statusCode());
    return mapper.readTree(response.body());
  }
}
