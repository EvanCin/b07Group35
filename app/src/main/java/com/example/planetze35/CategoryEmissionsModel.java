package com.example.planetze35;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for fetching emission data from Eco Tracker activities for use in Eco Gauge.
 */
public class CategoryEmissionsModel {
    private static CategoryEmissionsModel instance = null;
    private DatabaseReference dbRef;
    private final HashMap<String, Double> dateToEmissionMap = new HashMap<>();
    private final HashMap<String, DatabaseFetchCallback> backloadedCallbacks = new HashMap<>();
    private boolean fetchComplete = false;

    /**
     * Constructor for the <code>DailyActivityEmissionsModel</code> class. </br>
     *
     * Calling this method will start the fetch the current user's "DailyActivities" node from
     * Firebase.
     */
    private CategoryEmissionsModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String defaultUid = "defaultUserId";
        String uid = (currentUser != null) ? currentUser.getUid() : defaultUid;
        dbRef = FirebaseDatabase.getInstance().getReference()
//                .child("users/" + uid + "/DailyActivities");
                .child("users/defaultUserId");

        /*
         * Fetch data from database.
         * Once data is fetched, all backloaded callbacks will be called.
         * Since the data within the node referenced by dbRef can't be changed within the
         * Eco Gauge activity, we can just use addListenerForSingleValueEvent here.
         */
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fetchComplete = true;

                // Get data from DailyActivities node
                Object o1 = snapshot.getValue();
                HashMap<String, Object> snapshotMap;
                if (o1 == null) {
                    snapshotMap = new HashMap<>();
                } else if (o1 instanceof HashMap) {
                    snapshotMap = (HashMap<String, Object>) snapshot.getValue();
                } else {
                    Log.w("DailyActivityEmissionsModel",
                            "The snapshot from Firebase returned something of type "
                                    + o1.getClass()
                                    + " instead of HashMap. I don't know what to do here.");
                    return;
                }
                System.out.println(String.valueOf(snapshotMap));

                // Filter the data so that only the total_daily_emissions from each day is stored
                if(snapshotMap.containsKey("consumptionEmissions") && snapshotMap.get("consumptionEmissions") != null) {
                    Object o2 = snapshotMap.get("consumptionEmissions");
                    if(o2 instanceof Double) {
                        dateToEmissionMap.put("consumptionEmissions", (double) o2);
                    } else if(o2 instanceof Long){
                        dateToEmissionMap.put("consumptionEmissions", (double) (long) o2);
                    } else {
                        return;
                    }
                }
                if(snapshotMap.containsKey("transportationEmissions") && snapshotMap.get("transportationEmissions") != null) {
                    Object o2 = snapshotMap.get("transportationEmissions");
                    if(o2 instanceof Double) {
                        dateToEmissionMap.put("transportationEmissions", (double) o2);
                    } else if(o2 instanceof Long){
                        dateToEmissionMap.put("transportationEmissions", (double) (long) o2);
                    } else {
                        return;
                    }
                }if(snapshotMap.containsKey("energyEmissions") && snapshotMap.get("energyEmissions") != null) {
                    Object o2 = snapshotMap.get("energyEmissions");
                    if(o2 instanceof Double) {
                        dateToEmissionMap.put("energyEmissions", (double) o2);
                    } else if(o2 instanceof Long){
                        dateToEmissionMap.put("energyEmissions", (double) (long) o2);
                    } else {
                        return;
                    }
                }
                System.out.println(String.valueOf(dateToEmissionMap));

                callBackloadedCallbacks(true, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBackloadedCallbacks(false, error);
            }
        });
    }

    /**
     * Gets the <code>CategoryEmissionsModel</code> instance.
     *
     * @return the <code>CategoryEmissionsModel</code> instance
     */
    public static CategoryEmissionsModel getInstance() {
        if (instance == null) {
            instance = new CategoryEmissionsModel();
        }
        return instance;
    }

    /**
     * Removes any listeners from the Firebase database reference and sets the instance to null. </br>
     * Mainly used for if the user switches to a different account.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Gets the total daily emission within a specified time range, ending at a specified date. </br>
     *
     * If the relevant data has already been fetched successfully from the database,
     * <code>callback.onSuccess</code> is immediately called.
     * Otherwise, <code>callback</code> will be backloaded and will be called when the fetch
     * operation is completed.
     *
     * @param category the date to get the total daily emissions from
     * @param callback the callback to call when the fetch operation is complete
     */
    public void getEmissionsData(@NonNull String category,@NonNull DatabaseFetchCallback callback) {
        if(fetchComplete) {
            HashMap<String, Double> data = getDateToEmissionMap();
            callback.onSuccess(data);
        } else {
            backloadedCallbacks.put("dateToEmissionMap", callback);
        }
    }

    /**
     * Callback interface for interacting with the database. </br>
     *
     * Due to the asynchronous nature of the Firebase database, you may end up trying to call the
     * other methods in this class (e.g. <code>getEmissionsFromDay</code>) before the data fetching
     * is completed. </br>
     *
     * This interface allows you to "backload" any code you may want to run if this is the case
     * so that your code doesn't just do nothing/crash the app.
     */
    public interface DatabaseFetchCallback {
        /**
         * Called when the data is successfully fetched from the database.
         *
         * @param dateToEmissionMapData the desired emission data for the given date(s) </br>
         *                              the keys are the dates, and the values are the corresponding
         *                              emission data
         */
        void onSuccess(HashMap<String, Double> dateToEmissionMapData);


        /**
         * Called when the data could not be fetched from the database.
         * @param error the error returned from the Firebase event listener.
         */
        void onFailure(@NonNull DatabaseError error);
    }

    /**
     * Formats a <code>LocalDate</code> instance as a string. </br>
     *
     * Used so that the date string is formatted the same as the keys in <code>dateToEmissionMap</code>.
     *
     * @param date the <code>LocalDate</code> instance
     * @return the formatted date as a string.
     */
    private String localDateToStr(@NonNull LocalDate date) {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        return String.format("%d-%d-%d", year, month, day);
    }

    /**
     * Returns a <code>LocalDate</code> given a string that is assumed to be in valid format.
     *
     * @param str the (validly formatted) string
     * @return the corresponding <code>LocalDate</code> object
     */
    private LocalDate strToLocalDate(@NonNull String str) {
        String[] dateComponentStrs = str.split("-", 3);
        return LocalDate.of(Integer.parseInt(dateComponentStrs[0]),
                Integer.parseInt(dateComponentStrs[1]), Integer.parseInt(dateComponentStrs[2]));
    }

    /**
     * Returns the emission data from the given time range, ending at the date given (inclusive).
     * @return the emission data from the given time range, where the keys are the dates and the
     * values are the corresponding emission data
     */
    private HashMap<String,Double> getDateToEmissionMap() {
        if (!fetchComplete) {
            return null;
        }
        return dateToEmissionMap;
    }

    /**
     * Call all the currently backloaded callbacks. </br>
     *
     * This method will call the <code>DatabaseFetchCallback::onSuccess</code> method of all
     * backloaded <code>DatabaseFetchCallback</code> instances if the data fetch succeeded.
     * Otherwise, the <code>DatabaseFetchCallback::onFailure</code> method is called instead.
     *
     * @param fetchSuccessful whether the fetch was successful or not
     * @param error the error given by the database, if any, otherwise <code>null</code> (and not used)
     */
    private void callBackloadedCallbacks(boolean fetchSuccessful, DatabaseError error) {
        if (fetchSuccessful) {
            Log.d("CategoryEmissionsModel", "Data fetch was successful.");
            for (Map.Entry<String, DatabaseFetchCallback> entry : backloadedCallbacks.entrySet()) {
                String currEntry = entry.getKey();
                HashMap<String, Double> emissionMap = getDateToEmissionMap();
                entry.getValue().onSuccess(emissionMap);
            }
        } else {
            Log.d("CategoryEmissionsModel", "Data fetch failed.", error.toException());
            for (Map.Entry<String, DatabaseFetchCallback> entry : backloadedCallbacks.entrySet()) {
                entry.getValue().onFailure(error);
            }
        }
        backloadedCallbacks.clear();
    }
}
