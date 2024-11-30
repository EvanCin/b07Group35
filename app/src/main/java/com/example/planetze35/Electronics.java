package com.example.planetze35;

import java.util.HashMap;
import java.util.Map;

public class Electronics implements AbstractActivity {
    public int numDevices;
    public String itemType;

    public Electronics(int numDevices, String itemType) {
        this.numDevices = numDevices;
        this.itemType = itemType;
    }

    //calculation based on the annual CO2e data provided for number of electronics purchased.
    @Override
    public double calculateCO2e() {
        double co2e = 0.0;
        if (numDevices == 0) {
            co2e = 0;
        } else if (numDevices == 1) {
            co2e = 300;
        } else if (numDevices == 2) {
            co2e = 600;
        } else if (numDevices == 3) {
            co2e = 900;
        } else if (numDevices >= 4) {
            co2e = 1200;
        }
        return co2e;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> purchaseData = new HashMap<>();
        purchaseData.put("numItems", numDevices);
        purchaseData.put("CO2e", calculateCO2e());
        return purchaseData;
    }
}
