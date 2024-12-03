package com.example.planetze35.setup;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AnnualEmissionsCalculator {
    double totalEmissions; //kg
    int currQChoice; //User choice for current question
    ArrayList<ArrayList<int[]>> houseData;
    Context context;
    AnnualEmissionsCalculator(Context context) {
        this.context = context;
        houseData = new ArrayList<>();
    }
    public void readData() {
        try{
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("HouseFormulas.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            for(int i = 0; i < 12; i++) {
                ArrayList<int[]> dataSet = new ArrayList<>();
                for(int j = 0; j < 4; j++) {
                    String line = br.readLine();
                    String[] stringData = line.split(",");
                    int[] intData = new int[stringData.length];
                    for(int k = 0; k < stringData.length; k++) {
                        intData[k] = Integer.parseInt(stringData[k]);
                    }
                    dataSet.add(intData);
                }
                houseData.add(dataSet);
                br.readLine();
            }
        } catch (IOException exception) {
            Log.d("AnnualEmissionsCalculator", "Invalid File");
        }
    }
    public Map<String, String> calculateEmissions(ArrayList<Integer> selectedChoices) {
        totalEmissions = 0;
        double totalTransportationEmissions = 0;
        //Transportation Questions
        currQChoice = selectedChoices.get(0);
        //User uses a car
        if(currQChoice == 0) {
            double emissionFactor = 0;
            double distanceDriven = 0;
            currQChoice = selectedChoices.get(1);
            switch(currQChoice) {
                case 0: emissionFactor = 0.24; break;
                case 1: emissionFactor = 0.27; break;
                case 2: emissionFactor = 0.16; break;
                case 3: emissionFactor = 0.05; break;
            }
            currQChoice = selectedChoices.get(2);
            switch(currQChoice) {
                case 0: distanceDriven = 5000; break;
                case 1: distanceDriven = 10000; break;
                case 2: distanceDriven = 15000; break;
                case 3: distanceDriven = 20000; break;
                case 4: distanceDriven = 25000; break;
                case 5: distanceDriven = 30000; break;
            }
            totalEmissions += emissionFactor*distanceDriven;
        }
        int transportationUse = selectedChoices.get(3);
        int publicTransportEmissions = 0;
        currQChoice = selectedChoices.get(4);
        switch(transportationUse) {
            case 1:
                switch(currQChoice) {
                    case 0: publicTransportEmissions = 246; break;
                    case 1: publicTransportEmissions = 819; break;
                    case 2: publicTransportEmissions = 1638; break;
                    case 3: publicTransportEmissions = 3071; break;
                    case 4: publicTransportEmissions = 4095; break;
                } break;
            case 2:
            case 3:
                switch(currQChoice) {
                    case 0: publicTransportEmissions = 573; break;
                    case 1: publicTransportEmissions = 1911; break;
                    case 2: publicTransportEmissions = 3822; break;
                    case 3: publicTransportEmissions = 7166; break;
                    case 4: publicTransportEmissions = 9555; break;
                } break;
        }
        totalEmissions += publicTransportEmissions;
        currQChoice = selectedChoices.get(5);
        int flightEmission = 0;
        switch(currQChoice) {
            case 1: flightEmission = 225; break;
            case 2: flightEmission = 600; break;
            case 3: flightEmission = 1200; break;
            case 4: flightEmission = 1800; break;
        }
        totalEmissions += flightEmission;
        flightEmission = 0;
        currQChoice = selectedChoices.get(6);
        switch(currQChoice) {
            case 1: flightEmission = 825; break;
            case 2: flightEmission = 2200; break;
            case 3: flightEmission = 4400; break;
            case 4: flightEmission = 6600; break;
        }
        totalEmissions += flightEmission;
        totalTransportationEmissions = totalEmissions;

        //Food Questions
        double totalFoodEmissions = 0;
        currQChoice = selectedChoices.get(7);
        double foodEmissions = 0;
        switch(currQChoice) {
            case 0: foodEmissions = 1000; break;
            case 1: foodEmissions = 500; break;
            case 2: foodEmissions = 1500; break;
            case 3: switch(selectedChoices.get(8)) {
                case 0: foodEmissions += 2500; break;
                case 1: foodEmissions += 1900; break;
                case 2: foodEmissions += 1300; break;
            }
                switch(selectedChoices.get(9)) {
                    case 0: foodEmissions += 1450; break;
                    case 1: foodEmissions += 860; break;
                    case 2: foodEmissions += 450; break;
                }
                switch(selectedChoices.get(10)) {
                    case 0: foodEmissions += 950; break;
                    case 1: foodEmissions += 600; break;
                    case 2: foodEmissions += 200; break;
                }
                switch(selectedChoices.get(11)) {
                    case 0: foodEmissions += 800; break;
                    case 1: foodEmissions += 500; break;
                    case 2: foodEmissions += 150; break;
                }
        }
        totalEmissions += foodEmissions;
        foodEmissions = 0;
        currQChoice = selectedChoices.get(12);
        switch(currQChoice) {
            case 1: foodEmissions = 23.4; break;
            case 2: foodEmissions = 70.2; break;
            case 3: foodEmissions = 140.4; break;
        }
        totalEmissions += foodEmissions;
        totalFoodEmissions = totalEmissions - totalTransportationEmissions;

        //Housing Questions
        double totalHousingEmissions = 0;
        int typeOfHome = selectedChoices.get(13);
        int numOfOccupants = selectedChoices.get(14);
        int sizeOfHome = selectedChoices.get(15);
        int energyForHeat = selectedChoices.get(16);
        int monthlyElectricBill = selectedChoices.get(17);
        int energyForWater = selectedChoices.get(18);
        int renewableEnergySource = selectedChoices.get(19);
        int housingEmissions = 0;
        //Calculations for other type of home is same as townhouse
        if(typeOfHome == 4) typeOfHome = 2;
        int[] currArr = houseData.get(typeOfHome*3 + sizeOfHome).get(numOfOccupants);
        housingEmissions = currArr[energyForHeat + monthlyElectricBill*5];
        totalEmissions += housingEmissions;
        if(energyForHeat != energyForWater || energyForHeat == 4) {
            totalEmissions += 233;
        }
        switch(renewableEnergySource) {
            case 0: totalEmissions -= 6000;
            case 1: totalEmissions -= 4000;
        }

        totalHousingEmissions = totalEmissions - totalFoodEmissions - totalTransportationEmissions;

        //Consumption Questions
        double totalConsumptionEmissions = 0;
        int buyClothFrequency = selectedChoices.get(20);
        int buySecondHand = selectedChoices.get(21);
        int boughtDevices = selectedChoices.get(22);
        int recycleFrequency = selectedChoices.get(23);
        double consumptionEmissions = 0;
        switch (buyClothFrequency) {
            case 0: consumptionEmissions = 360;
            case 1: consumptionEmissions = 120;
            case 2: consumptionEmissions = 100;
            case 3: consumptionEmissions = 5;
        }
        switch (buySecondHand) {
            case 0: consumptionEmissions *= 0.5;
            case 1: consumptionEmissions *= 0.7;
        }
        totalEmissions += consumptionEmissions;
        consumptionEmissions = 0;
        switch (boughtDevices) {
            case 1: consumptionEmissions = 300;
            case 2: consumptionEmissions = 600;
            case 3: consumptionEmissions = 1200;
        }
        switch (buyClothFrequency) {
            case 0: switch (recycleFrequency) {
                case 1: consumptionEmissions -= 54;
                case 2: consumptionEmissions -= 108;
                case 3: consumptionEmissions -= 180;
            }
            case 1: switch (recycleFrequency) {
                case 1: consumptionEmissions -= 18;
                case 2: consumptionEmissions -= 36;
                case 3: consumptionEmissions -= 60;
            }
            case 3: switch (recycleFrequency) {
                case 1: consumptionEmissions -= 15;
                case 2: consumptionEmissions -= 30;
                case 3: consumptionEmissions -= 50;
            }
            case 4: switch (recycleFrequency) {
                case 1: consumptionEmissions -= 0.75;
                case 2: consumptionEmissions -= 1.5;
                case 3: consumptionEmissions -= 2.5;
            }
        }
        switch (boughtDevices) {
            case 1: switch (recycleFrequency) {
                case 1: consumptionEmissions -= 45;
                case 2: consumptionEmissions -= 60;
                case 3: consumptionEmissions -= 90;
            }
            case 2: switch (recycleFrequency) {
                case 1: consumptionEmissions -= 60;
                case 2: consumptionEmissions -= 120;
                case 3: consumptionEmissions -= 180;
            }
            case 3: switch (recycleFrequency) {
                case 1: consumptionEmissions -= 120;
                case 2: consumptionEmissions -= 240;
                case 3: consumptionEmissions -= 360;
            }
        }
        totalEmissions += consumptionEmissions;
        totalConsumptionEmissions = totalEmissions - totalHousingEmissions - totalFoodEmissions - totalTransportationEmissions;

        Map<String, String> emissionsByCategory = new HashMap<>();
        emissionsByCategory.put("total", String.format(Locale.CANADA, "%5f", totalEmissions/1000));
        emissionsByCategory.put("transportation", String.format(Locale.CANADA, "%4f", totalTransportationEmissions/1000));
        emissionsByCategory.put("food", String.format(Locale.CANADA, "%4f", totalFoodEmissions/1000));
        emissionsByCategory.put("housing", String.format(Locale.CANADA, "%4f", totalHousingEmissions/1000));
        emissionsByCategory.put("consumption", String.format(Locale.CANADA, "%4f", totalConsumptionEmissions/1000));

        return emissionsByCategory;
    }
}
