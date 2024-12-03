package com.example.planetze35.EcoTrackerAllParts;

import java.util.HashMap;
import java.util.Map;

public class TakePublicTransportation implements AbstractActivity {
    public String vehicleType;
    public double timeSpent;

    public TakePublicTransportation() {
    }
    public TakePublicTransportation(double timeSpent, String vehicleType) {
        this.vehicleType = vehicleType;
        this.timeSpent = timeSpent;
    }

    //For future purposes to get/set data
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(double timeSpent) {
        this.timeSpent = timeSpent;
    }

    // The CO2e values are annual emissions for different time spent on public transport.
    // To calculate daily emissions, the annual CO2e is divided by 365.
    // 'timeSpent' represents hours spent on public transport per day.
    @Override
    public double calculateCO2e() {
        double co2e = 0.0;
        if (timeSpent < 1) {
            co2e = 573 / 365.0;
        } else if (timeSpent >= 1 && timeSpent <= 3) {
            co2e = 1911 / 365.0;
        } else if (timeSpent > 3 && timeSpent <= 5) {
            co2e = 3822 / 365.0;
        } else if (timeSpent > 5 && timeSpent <= 10) {
            co2e = 7166/365.0;
        } else if (timeSpent > 10) {
            co2e = 9555/365.0;
        }
        return co2e;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> transportData = new HashMap<>();
        transportData.put("timeSpent", timeSpent);
        transportData.put("CO2e", calculateCO2e());
        return transportData;
    }
}
