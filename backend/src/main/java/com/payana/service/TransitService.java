package com.payana.service;

import com.payana.model.JourneyOption;
import com.payana.model.JourneyRequest;
import com.payana.model.Stop;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransitService {
  private final List<Stop> stops = List.of(
      new Stop("majestic","Kempegowda Bus Station",12.9767,77.5713,"bus"),
      new Stop("silk-board","Silk Board",12.9173,77.6228,"bus"),
      new Stop("ypr","Yeshwanthpur",13.0285,77.5547,"train"),
      new Stop("airport","Kempegowda International Airport",13.1986,77.7066,"flight"));

  public List<Stop> nearby(double lat, double lon, double radiusKm) {
    return stops.stream().filter(s -> distance(lat,lon,s.latitude(),s.longitude()) <= radiusKm).toList();
  }

  public List<JourneyOption> plan(JourneyRequest request) {
    return List.of();
  }

  public List<Stop> stops() { return stops; }

  private double distance(double aLat,double aLon,double bLat,double bLon) {
    double dLat=Math.toRadians(bLat-aLat), dLon=Math.toRadians(bLon-aLon);
    double x=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(aLat))*Math.cos(Math.toRadians(bLat))*Math.sin(dLon/2)*Math.sin(dLon/2);
    return 6371 * 2 * Math.atan2(Math.sqrt(x),Math.sqrt(1-x));
  }
}
