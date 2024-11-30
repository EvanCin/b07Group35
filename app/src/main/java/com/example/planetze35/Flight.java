package com.example.planetze35;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Flight implements AbstractActivity {
    public int flightCount;
    public String flightType; // "Short-haul" or "Long-haul"

    public Flight(int flightCount, String flightType) {
        this.flightCount = flightCount;
        this.flightType = flightType;
    }
    //for future purposes to get/set data
    public int getFlightCount() {
        return flightCount;
    }
    public void setFlightCount(int flightCount) {
        this.flightCount = flightCount;
    }
    public String getFlightType() {
        return flightType;
    }
    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    // CO2e calculated based on the number of flights and flight type (Short-haul or Long-haul).
    // The values represent the CO2e emissions for a given range of flight counts
    @Override
    public double calculateCO2e() {
        double co2e = 0.0;
        if (flightCount == 0) {
            return 0;
        }
        if (flightCount >= 1 && flightCount <= 2) {
            if (Objects.equals(flightType, "Short-haul")) {
                co2e = 225;
            } else {
                co2e = 825;
            }
        }
        if (flightCount >= 3 && flightCount <= 5) {
            if (Objects.equals(flightType, "Short-haul")) {
                co2e = 600;
            } else {
                co2e = 2200;
            }
        }
        if (flightCount >= 6 && flightCount <= 10) {
            if (Objects.equals(flightType, "Short-haul")) {
                co2e = 1200;
            } else {
                co2e = 4400;
            }
        }
        if (flightCount > 10) {
            if (Objects.equals(flightType, "Short-haul")) {
                co2e = 1800;
            } else {
                co2e = 6600;
            }
        }
        return co2e;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> flightData = new HashMap<>();
        flightData.put("flightCount", flightCount);
        flightData.put("CO2e", calculateCO2e());
        return flightData;
    }
}
