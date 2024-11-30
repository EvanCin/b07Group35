package com.example.planetze35;

import java.util.Map;
import java.util.Objects;

public interface AbstractActivity {
    //abstract methods
    Map<String, Object> toMap();
    double calculateCO2e();
}
