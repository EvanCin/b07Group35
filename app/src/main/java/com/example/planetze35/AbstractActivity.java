package com.example.planetze35;

import java.util.Map;
import java.util.Objects;

public interface AbstractActivity {
    Map<String, Object> toMap();
    double calculateCO2e();
}
