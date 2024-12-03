package com.example.planetze35.EcoTrackerAllParts;

import java.util.HashMap;
import java.util.Map;

public class Meal implements AbstractActivity {
    public String mealType; // e.g., "beef", "vegetarian", etc.
    public int numServings;

    public Meal() {

    }
    public Meal(int numServings, String mealType) {
        this.mealType = mealType;
        this.numServings = numServings;
    }

    //for future purposes to get/set data
    public String getMealType() {
        return mealType;
    }
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    public int getNumServings() {
        return numServings;
    }
    public void setNumServings(int numServings) {
        this.numServings = numServings;
    }
    // You can still access CO2e using the inherited method from AbstractActivity


    @Override
    public double calculateCO2e() {
        double co2PerServing = 0;

        switch (mealType.toLowerCase()) {
            case "beef":
                co2PerServing = 2500.0 / 365.0; // kg CO2 per day per serving
                break;
            case "pork":
                co2PerServing = 1450.0 / 365.0;
                break;
            case "chicken":
                co2PerServing = 950.0 / 365.0;
                break;
            case "fish":
                co2PerServing = 800.0 / 365.0;
                break;
            case "plant-based": //average of all 3 types: vegetarian, vegan, pescatarian
                co2PerServing = 1000.0 / 365.0;
                break;
            default:
                break;
        }

        // Calculate the total CO2 emissions for the given number of servings
        return co2PerServing * numServings;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> mealData = new HashMap<>();
        mealData.put("numServings", numServings);
        mealData.put("CO2e", calculateCO2e());
        return mealData;
    }
}
