package com.example.planetze35.ecogauge;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.planetze35.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * This class is used for getting data from the CSV file stored in the res/raw folder containing
 * the average CO2 emissions of countries/regions around the world.
 */
public class GlobalAveragesCSVModel {
    private static GlobalAveragesCSVModel instance = null;
    private final HashMap<String, Double> averages = new HashMap<>();

    /**
     * Constructor for the <code>AvgEmissionModel</code> class. </br>
     * @param context the application context (to be able to find the raw file)
     * @throws IOException if the method fails to access the raw file
     */
    private GlobalAveragesCSVModel(@NonNull Context context) throws IOException {
        InputStream stream = context.getResources().openRawResource(R.raw.global_averages);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        // Skip header
        String line = reader.readLine();

        // Get data from file
        while ((line = reader.readLine()) != null) {
            String[] splitLine = line.split(",");
            averages.put(splitLine[0], Double.valueOf(splitLine[1]));
        }
    }

    /**
     * Gets the <code>AvgEmissionModel</code> instance.
     * @param context the application context (to be able to find the raw file)
     * @return the <code>AvgEmissionModel</code> instance
     * @throws IOException if the AvgEmissionModel constructor fails to access the raw file
     */
    public static GlobalAveragesCSVModel getInstance(@NonNull Context context) throws IOException {
        if (instance == null) {
            instance = new GlobalAveragesCSVModel(context);
        }
        return instance;
    }

    /**
     * Gets the average emission of a specified region.
     * @param region the region to get the average emissions of
     * @return the average emissions of the region <code>region</code>, or <code>-1</code> if no
     * such region exists
     */
    public double getAvgEmission(String region) {
        Double avg = averages.get(region);
        return (avg != null) ? avg : -1;
    }
}
