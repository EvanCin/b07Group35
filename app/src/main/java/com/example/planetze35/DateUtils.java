package com.example.planetze35;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getCurrentDateFormatted() {
        // Get today's date
        Date today = new Date();

        // Create SimpleDateFormat to format month and day without leading zeros
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("M-d", Locale.getDefault());
        String monthDay = monthDayFormat.format(today);

        // Get the full year (4 digits) and remove leading zeros
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String yearString = yearFormat.format(today);
        // Remove leading zeros by converting to integer
        int yearInt = Integer.parseInt(yearString);
        // Convert back to string
        String year = String.valueOf(yearInt);

        // Combine the formatted year, month, and day (no leading zeros)
        String currentDate = year + "-" + monthDay;

        return currentDate;
    }
}
