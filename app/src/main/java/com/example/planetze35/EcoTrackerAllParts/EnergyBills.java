package com.example.planetze35.EcoTrackerAllParts;

import java.util.HashMap;
import java.util.Map;

public class EnergyBills implements AbstractActivity {
    public String billType;
    public double amount;
    public EnergyBills(double amount, String billType) {
        this.billType = billType;
        this.amount = amount;
    }
    @Override
    public double calculateCO2e() {
        double co2e = 0.0;

        switch (billType.toLowerCase()) {
            // For a monthly electricity bill <$50: CO₂ emissions are approximately 617,848 g CO₂ / 2880.
            // Calculated from averages of housing types and water heating sources.
            // For a monthly electricity bill $50–$100: CO₂ emissions are approximately 859,196 g CO₂ / 2880.
            // Calculated from averages of housing types and water heating sources.
            // For a monthly electricity bill $100–$150: CO₂ emissions are approximately 1,051,930 g CO₂ / 2880.
            // Calculated from averages of housing types and water heating sources.
            // For a monthly electricity bill $150–$200: CO₂ emissions are approximately 1,203,587 g CO₂ / 2880.
            // Calculated from averages of housing types and water heating sources.
            // For a monthly electricity bill >$200: CO₂ emissions are approximately 1,430,270 g CO₂ / 2880.
            // Calculated from averages of housing types and water heating sources.

            case "electricity":
                if (amount < 50) {
                    co2e = (double) 617848 / 2880;
                } else if (amount >= 50 && amount <= 100) {
                    co2e = (double) 859196 / 2880;
                } else if (amount > 100 && amount <= 150) {
                    co2e = (double) 1051930 / 2880;
                } else if (amount > 150 && amount <= 200) {
                    co2e = (double) 1203587 / 2880;
                } else if (amount > 200) {
                    co2e = (double) 1430270 / 2880;
                }
                break;
            // Calculations for Water and Gas were made based on online source
            // Since we are allowed to make reasonable assumptions.
            // For a monthly water bill <$50: Estimated consumption is 15–25 m³,
            // with CO₂ emissions ranging from 16.5–2,527.5 kg. The average of both bounds was used.

            // For a monthly water bill between $50–$100: Estimated consumption is 25–50 m³,
            // with CO₂ emissions ranging from 25–5,050 kg. The average of both bounds was used.

            // For a monthly water bill between $100–$150: Estimated consumption is 50–75 m³,
            // with CO₂ emissions ranging from 50–7,575 kg. The average of both bounds was used.

            // For a monthly water bill between $150–$200: Estimated consumption is 75–100 m³,
            // with CO₂ emissions ranging from 75–10,100 kg. The average of both bounds was used.

            // For a monthly water bill >$200: Estimated consumption exceeds 100 m³,
            // with CO₂ emissions ranging from 100–10,100 kg. The average of both bounds was used.
            case "water":

                if (amount < 50) {
                    co2e = (16.5 + 2527.5)/2;
                } else if (amount >= 50 && amount <= 100) {
                    co2e = (double) (25 + 5050)/2;
                } else if (amount > 100 && amount <= 150) {
                    co2e = (double) (50 + 7575)/2;
                } else if (amount > 150 && amount <= 200) {
                    co2e = (double) (75 + 10100)/2;
                } else if (amount > 200) {
                    co2e = (double) (100 + 10100)/2;
                }
                break;
            // For a gas bill <$50: Estimated consumption is ~222 m³, with CO₂ emissions of 422–468 kg. Average was used.
            // For a gas bill $50–$100: Estimated consumption is 222–444 m³, with CO₂ emissions of 422–844 kg. Average was used.
            // For a gas bill $100–$150: Estimated consumption is 444–666 m³, with CO₂ emissions of 844–1,266 kg. Average was used.
            // For a gas bill $150–$200: Estimated consumption is 666–888 m³, with CO₂ emissions of 1,266–1,689 kg. Average was used.
            // For a gas bill >$200: Estimated consumption exceeds 888 m³, with CO₂ emissions starting at 1,689 kg. Average was used.

            case "gas":
                if (amount < 50) {
                    co2e = (double) (422 + 468) /2;
                } else if (amount >= 50 && amount <= 100) {
                    co2e = (double) (633 + 656)/2;
                } else if (amount > 100 && amount <= 150) {
                    co2e = (double) (844 + 1266)/2;
                } else if (amount > 150 && amount <= 200) {
                    co2e = (double) (1266 + 1689)/2;
                } else if (amount > 200) {
                    co2e = 1689;
                }
                break;
        }
        return co2e;
    }
    @Override
    public Map<String, Object> toMap() {
        // Convert object fields into a Map
        Map<String, Object> energyData = new HashMap<>();
        energyData.put("amount", amount);
        energyData.put("CO2e", calculateCO2e());
        return energyData;
    }

}
