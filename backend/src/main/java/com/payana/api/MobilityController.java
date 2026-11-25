package com.payana.api;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.payana.service.BmtcRealtimeService;
import com.payana.service.BmtcNetworkDataService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api")
public class MobilityController {
  private final BmtcRealtimeService bmtcRealtimeService;
  private final BmtcNetworkDataService bmtcNetworkDataService;

  public MobilityController(BmtcRealtimeService bmtcRealtimeService,
      BmtcNetworkDataService bmtcNetworkDataService) {
    this.bmtcRealtimeService = bmtcRealtimeService;
    this.bmtcNetworkDataService = bmtcNetworkDataService;
  }

  @GetMapping("/realtime/buses")
  public Map<String, Object> realtimeBuses() { return bmtcRealtimeService.vehicles(); }

  @GetMapping("/realtime/status")
  public Map<String, Object> realtimeStatus() { return bmtcRealtimeService.status(); }

  @GetMapping("/bmtc/data/{dataset}")
  public JsonNode bmtcData(@PathVariable String dataset) {
    return bmtcNetworkDataService.get(dataset);
  }

  @GetMapping("/trains")
  public Object trains(@RequestParam(defaultValue="Bengaluru") String station) {
    return Map.of("station", station, "source", "railway-provider-required", "live", false,
      "trains", List.of(), "message", "Connect an authorized railway feed for live train running status.");
  }

  @GetMapping("/flights")
  public Object flights(@RequestParam(defaultValue="BLR") String airport) {
    return Map.of("airport", airport, "source","flight-provider-required", "live", false,
      "arrivals", List.of(), "departures", List.of(),
      "message", "Connect an authorized aviation feed for live aircraft positions.");
  }

  @PostMapping("/rides/quotes")
  public Object rides(@RequestBody Map<String, String> request) {
    String from=request.getOrDefault("from","current location"), to=request.getOrDefault("to","destination");
    return Map.of("from",from,"to",to,"source","ride-provider-required", "live", false,
      "quotes",List.of(), "message", "Connect approved ride-partner APIs for live ETA and fare quotes.");
  }
}
