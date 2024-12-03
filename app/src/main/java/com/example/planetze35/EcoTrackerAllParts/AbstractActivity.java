package com.example.planetze35.EcoTrackerAllParts;

import java.util.Map;

public interface AbstractActivity {
    //abstract methods
    Map<String, Object> toMap();
    double calculateCO2e();
}
