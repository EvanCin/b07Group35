package com.example.planetze35;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for fetching emission data from Eco Tracker activities for use in Eco Gauge. </br>
 *
 * To use this class, get the singleton instance by calling <code>getInstance</code>.
 * Then, use the <code>getEmissionsData</code> method to get the data. </br>
 *
 * See the descriptions for the <code>getEmissionsData</code> method and the
 * <code>DatabaseFetchCallback</code> interface for more details.
 */
public class DailyActivityEmissionsModel {
    private static DailyActivityEmissionsModel instance = null;
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
    private DailyActivityEmissionsModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String defaultUid = "defaultUserId";
        String uid = (currentUser != null) ? currentUser.getUid() : defaultUid;
        dbRef = FirebaseDatabase.getInstance().getReference()
//                .child("users/" + uid + "/DailyActivities");
                .child("users/defaultUserId/DailyActivities");

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

                // Filter the data so that only the total_daily_emissions from each day is stored
                for (Map.Entry<String, Object> entry : snapshotMap.entrySet()) {
                    String key = entry.getKey();
                    HashMap<String, Object> activitiesMap = (HashMap<String, Object>) snapshotMap.get(key);
                    if (activitiesMap == null) {
                        continue;
                    }
                    Object o2 = activitiesMap.get("total_daily_emissions");
                    if (o2 instanceof Double) {
                        dateToEmissionMap.put(key, (double) o2);
                    } else if (o2 instanceof Long)
                        dateToEmissionMap.put(key, (double) (long) o2);
                    else {
                        Log.w("DailyActivityEmissionsModel",
                                "The total_daily_emissions node at user " + uid
                                        + " and date " + key
                                        + " doesn't store a number. I don't know what to do here.");
                        return;
                    }
                }

                callBackloadedCallbacks(true, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBackloadedCallbacks(false, error);
            }
        });
    }

    /**
     * Returns the <code>DailyActivityEmissionsModel</code> instance.
     *
     * @return the <code>DailyActivityEmissionsModel</code> instance
     */
    public static DailyActivityEmissionsModel getInstance() {
        if (instance == null) {
            instance = new DailyActivityEmissionsModel();
        }
        return instance;
    }

    /**
     * Removes any listeners from the Firebase database reference and sets the instance to null. </br>
     *
     * Use if the data needs to be updated in any way, and don't forget to re-invoke the
     * <code>getInstance</code> method after.
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
     * operation is completed. </br>
     *
     * @param date the last day of the time range given
     * @param timeRange must be one of <code>"day"</code>, <code>"week"</code>, <code>"month"</code>,
     *                  or <code>"year"</code>, otherwise it will be interpreted as <code>"day"</code>
     * @param callback the callback to call when the fetch operation is complete
     */
    public void getEmissionsData(@NonNull LocalDate date, @NonNull String timeRange,
                                 @NonNull DatabaseFetchCallback callback) {
        if (!(timeRange.equals("day") || timeRange.equals("week")
                || timeRange.equals("month") || timeRange.equals("year"))) {
            Log.w("DailyActivityEmissionsModel",
                    "The given time range is " + timeRange + ", which I don't understand."
                            + "I'm going to interpret it as \"day\" instead.");
            timeRange = "day";
        }
        if (fetchComplete) {
            HashMap<String, Double> data = getPopulatedEmissionHashMap(date, timeRange);
            callback.onSuccess(data);
        } else {
            String dateStr = localDateToStr(date);
            backloadedCallbacks.put(timeRange + "-" + dateStr, callback);
        }
    }

    /**
     * Returns a formatted string representation of a given <code>LocalDate</code> instance. </br>
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
     *
     * @param date the last day of the time range given
     * @param timeRange must be one of <code>"day"</code>, <code>"week"</code>, <code>"month"</code>,
     *                  or <code>"year"</code>, otherwise it will be interpreted as <code>"day"</code>
     * @return the emission data from the given time range, where the keys are the dates and the
     * values are the corresponding emission data
     */
    private HashMap<String, Double> getPopulatedEmissionHashMap(LocalDate date, String timeRange) {
        if (!fetchComplete) {
            return null;
        }

        HashMap<String, Double> map = new HashMap<>();

        int dateKickback = 0;
        if (timeRange.equals("year")) {
            dateKickback = 364;
        } else if (timeRange.equals("month")) {
            dateKickback = 29;
        } else if (timeRange.equals("week")) {
            dateKickback = 6;
        } else if (!timeRange.equals("day")) {
            Log.w("DailyActivityEmissionsModel",
                    "The given time range is " + timeRange + ", which I don't understand."
                            + "I'm going to interpret it as \"day\" instead.");
        }

        for (LocalDate d = date.minusDays(dateKickback);
                d.isBefore(date.plusDays(1)); d = d.plusDays(1)) {
            String dStr = localDateToStr(d);
            if (dateToEmissionMap.containsKey(dStr)) {
                map.put(dStr, dateToEmissionMap.get(dStr));
            }
        }
        return map;
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
            Log.d("DailyActivityEmissionsModel", "Data fetch was successful.");
            for (Map.Entry<String, DatabaseFetchCallback> entry : backloadedCallbacks.entrySet()) {
                String[] modeAndDate = entry.getKey().split("-", 2);
                Log.d("DailyActivityEmissionsModel", "Callback with time range " + modeAndDate[0] + " and date " + modeAndDate[1]);
                HashMap<String, Double> emissionMap = getPopulatedEmissionHashMap(
                        strToLocalDate(modeAndDate[1]), modeAndDate[0]);
                entry.getValue().onSuccess(emissionMap);
            }
        } else {
            Log.d("DailyActivityEmissionsModel", "Data fetch failed.", error.toException());
            for (Map.Entry<String, DatabaseFetchCallback> entry : backloadedCallbacks.entrySet()) {
                entry.getValue().onFailure(error);
            }
        }
        backloadedCallbacks.clear();
    }

    /**
     * Callback interface for interacting with the database. </br>
     *
     * Due to the asynchronous nature of the Firebase database, you may end up trying to call
     * <code>getEmissionsData</code>) before the data is fetched from Firebase. </br>
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
}
