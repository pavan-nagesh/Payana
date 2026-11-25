package com.payana.api;

import com.payana.model.JourneyRequest;
import com.payana.service.TransitService;
import com.payana.service.RoutePlannerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransitController {
  private final TransitService transit;
  private final RoutePlannerService routes;
  public TransitController(TransitService transit, RoutePlannerService routes){this.transit=transit;this.routes=routes;}
  @GetMapping("/stops") public Object stops(){return transit.stops();}
  @GetMapping("/nearby") public Object nearby(@RequestParam double lat,@RequestParam double lon,@RequestParam(defaultValue="3") double radiusKm){return transit.nearby(lat,lon,radiusKm);}
  @PostMapping("/journeys") public Object journeys(@Valid @RequestBody JourneyRequest request){return routes.plan(request.from(),request.to(),request.mode());}
}
