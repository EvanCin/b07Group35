package com.example.planetze35;

import java.util.HashMap;
import java.util.Map;

public class DrivePersonalVehicle implements AbstractActivity {
    public String vehicleType;
    public double distance;

    public DrivePersonalVehicle(double distance, String vehicleType) {
        this.vehicleType = vehicleType;
        this.distance = distance;
    }

    // CO2e calculated using emission factors based on vehicle type (gasoline, electric, diesel, hybrid).
    // The emission factor values are based on annual data provided for each vehicle type and are used to calculate the CO2e per distance driven.
    public double calculateCO2e() {
        double emissionFactor = 0.0;
        switch (vehicleType.toLowerCase()) {
            case "gasoline":
                emissionFactor = 0.24;
                break;
            case "electric":
                emissionFactor = 0.05;
                break;
            case "diesel":
                emissionFactor = 0.27;
                break;
            case "hybrid":
                emissionFactor = 0.16;
                break;
            case "i don\\'t know":
                emissionFactor =  0.18; // I used average of other 4 car types to calculate its emission factor
                break;
            default:
                emissionFactor = 0.24; // by default spinner has gasoline car type
        }
        return emissionFactor * distance;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> vehicleData = new HashMap<>();
        vehicleData.put("distance", distance);
        vehicleData.put("CO2e", calculateCO2e());
        return vehicleData;
    }

    //for future purposes to get/set data
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
