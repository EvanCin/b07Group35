package com.example.planetze35;

import java.util.HashMap;
import java.util.Map;

public class CyclingOrWalking implements AbstractActivity {
    public double distance;
    public CyclingOrWalking(double distance) {
        this.distance = distance;
    }
    //For future purposes, to retrieve data
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public double calculateCO2e() {
        return 0; //CO2e for cycling and walking is 0;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> cyclingData = new HashMap<>();
        cyclingData.put("distance", distance);
        cyclingData.put("CO2e", calculateCO2e());
        return cyclingData;
    }

}
