package com.example.planetze35;

import java.util.HashMap;
import java.util.Map;

public class NewClothes implements AbstractActivity {
    public int numItems;
    public NewClothes() {}
    public NewClothes(int numItems) {
        this.numItems = numItems;
    }
    // Calculate daily CO2 emissions for clothing purchases:
    // - 5 clothes per month = 60 clothes/year
    // - Monthly CO2 emission = 360 kg CO2/year, so emissions per item = 360 / 60 = 6 kg CO2/item
    // - Daily emissions = 6 kg * numClothes purchased that day
    @Override
    public double calculateCO2e() {
        return 6 * numItems;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> purchaseData = new HashMap<>();
        purchaseData.put("numItems", numItems);
        purchaseData.put("CO2e", calculateCO2e());
        return purchaseData;
    }
}
