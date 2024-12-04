package com.example.planetze35.ecogauge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.planetze35.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EcoGaugeActivity extends AppCompatActivity {

    Button totalWeeklyEmissionsButton, totalMonthlyEmissionsButton, totalYearlyEmissionsButton;
    TextView totalEmissionsTextView;
    BarChart emissionsChart;
    LineChart lineChart;
    Button dailyButton, weeklyButton, monthlyButton;
    FirebaseUser user;
    DatabaseReference mDatabase;
    float weeklyEmissionsForTextView, monthlyEmissionsForTextView, yearlyEmissionsForTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_gauge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        totalWeeklyEmissionsButton = findViewById(R.id.totalWeeklyEmissionButton);
        totalMonthlyEmissionsButton = findViewById(R.id.totalMonthlyEmissionsButton);
        totalYearlyEmissionsButton = findViewById(R.id.totalYearlyEmissionsButton);
        totalEmissionsTextView = findViewById(R.id.totalEmissionsTextView);

        emissionsChart = findViewById(R.id.emissionsChart);
        EmissionsBarChart emissionsBarChart = new EmissionsBarChart(emissionsChart);
        emissionsBarChart.setDefaultBarChart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        UpdateCategoryEmissions updateCategoryEmissions = new UpdateCategoryEmissions(user.getUid());
        updateCategoryEmissions.updateCategories();
        System.out.println(user.getUid());

        //Gets Category emissions for barchart
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EmissionsCategoryModel categoryModel = dataSnapshot.getValue(EmissionsCategoryModel.class);
                if (categoryModel != null) {
                    double transportationEmissions = categoryModel.transportationEmissions;
                    double energyEmissions = categoryModel.energyEmissions;
                    double consumptionEmissions = categoryModel.consumptionEmissions;
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(1f, (float) transportationEmissions));
                    entries.add(new BarEntry(2f, (float) energyEmissions));
                    entries.add(new BarEntry(3f, (float) consumptionEmissions));

                    BarDataSet dataSet = new BarDataSet(entries, "Category Emissions (kg CO2e)");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.85f);
                    emissionsChart.setData(barData);
                    emissionsChart.invalidate();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("Post Error", "Getting post failed");
            }
        });

        lineChart = findViewById(R.id.linechart);
        EmissionsLineChart.setDefaultLineChart(lineChart);
        EmissionsLineChart.setDefaultValues(lineChart);

        DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid() + "/DailyActivities");
        dbNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> datesHS = (HashMap<String, Object>) snapshot.getValue();
                if (datesHS == null) {
                    datesHS = new HashMap<>();
                }
                HashMap<String, Object> datesMap = new HashMap<>();
                HashMap<String, String> dateEmissionMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : datesHS.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    datesMap.put(key, value);
                    HashMap<String, Object> temp = (HashMap<String, Object>) datesHS.get(key);
                    if (key != null) {
                        dateEmissionMap.put(key, String.valueOf(temp.get("total_daily_emissions")));
                    }
                }

                Date d = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = df.format(d);
                String dayAbs = formattedDate.substring(0, 2);
                String monthAbs = formattedDate.substring(3, 5);
                String yearAbs = formattedDate.substring(6, 10);

                Calendar c = Calendar.getInstance();
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.MONTH, -1);
                c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
                int day = c.get(Calendar.DAY_OF_WEEK);

                //Calculate the data for each day of the week
                weeklyEmissionsForTextView = 0;
                List<Entry> dailyEmissions = new ArrayList<>();
                for (int i = 1; i <= day; i++) {
                    int currDay = Integer.parseInt(dayAbs) - day + i;
                    //Last month
                    int currMonth = Integer.parseInt(monthAbs);
                    if (currDay <= 0) {
                        currDay += c1.get(Calendar.DAY_OF_MONTH);
                        currMonth -= 1;
                    }
                    String currDate = yearAbs + "-" + currMonth + "-" + currDay;
                    if (dateEmissionMap.containsKey(currDate) && dateEmissionMap.get(currDate) != null) {
                        dailyEmissions.add(new Entry(i - 1, Float.parseFloat(dateEmissionMap.get(currDate))));
                        weeklyEmissionsForTextView += Float.parseFloat(dateEmissionMap.get(currDate));
                    } else {
                        dailyEmissions.add(new Entry(i - 1, 0));
                    }
                }
                for (int i = day + 1; i < 8; i++) {
                    dailyEmissions.add(new Entry(i - 1, 0));
                }

                //Store the emission info of each day for the whole year
                //Get current day
                float[] yearlyEmissions = new float[366];
                float[] monthlyEmissions = new float[12];
                float emissionsPerMonth = 0;
                int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
                String[] months = {"", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
                int currDay = 1;
                int currMonth = 1;
                yearlyEmissionsForTextView = 0;
                for (int i = 0; i < dayOfYear; i++) {
                    String currDate = yearAbs + "-" + currMonth + "-" + currDay;
                    if (dateEmissionMap.containsKey(currDate) && dateEmissionMap.get(currDate) != null && !dateEmissionMap.get(currDate).equals("null")) {
                        yearlyEmissions[i] = Float.parseFloat(dateEmissionMap.get(currDate));
                        emissionsPerMonth += yearlyEmissions[i];
                        yearlyEmissionsForTextView += yearlyEmissions[i];
                    }
                    if (currMonth == 1 || currMonth == 3 || currMonth == 5 || currMonth == 7 || currMonth == 8 || currMonth == 10 || currMonth == 12) {
                        if (currDay == 31) {
                            monthlyEmissions[currMonth - 1] = emissionsPerMonth;
                            emissionsPerMonth = 0;
                            currMonth++;
                            currDay = 0;
                        }
                    } else if (currMonth == 4 || currMonth == 6 || currMonth == 9 || currMonth == 11) {
                        if (currDay == 30) {
                            monthlyEmissions[currMonth - 1] = emissionsPerMonth;
                            emissionsPerMonth = 0;
                            currMonth++;
                            currDay = 0;
                        }
                    } else if (currMonth == 2) {
                        if (currDay == 28 && Integer.parseInt(yearAbs) % 4 != 0) {
                            monthlyEmissions[currMonth - 1] = emissionsPerMonth;
                            emissionsPerMonth = 0;
                            currMonth++;
                            currDay = 0;
                        } else if (currDay == 29) {
                            monthlyEmissions[currMonth - 1] = emissionsPerMonth;
                            emissionsPerMonth = 0;
                            currMonth++;
                            currDay = 0;
                        }
                    }
                    currDay++;
                }
                monthlyEmissions[currMonth - 1] = emissionsPerMonth;

                //Calculate data for each week of month
                int[] daysPerMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                float[] weeklyEmission = new float[4];
                float weekEmission = 0;
                currDay = 0;
                for (int i = 0; i < Integer.parseInt(monthAbs) - 1; i++) {
                    currDay += daysPerMonth[i];
                }
                for (int week = 0; week < 4; week++) {
                    for (int i = 0; i < 7; i++) {
                        weekEmission += yearlyEmissions[currDay];
                        currDay++;
                    }
                    weeklyEmission[week] = weekEmission;
                    weekEmission = 0;
                }
                List<Entry> weeklyEmissionsEntry = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    weeklyEmissionsEntry.add(new Entry(i, weeklyEmission[i]));
                }
                //Calculate data for each month of year
                List<Entry> monthlyEmissionsEntry = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    monthlyEmissionsEntry.add(new Entry(i, monthlyEmissions[i]));
                }
                //Calculate data for monthlyTextView
                monthlyEmissionsForTextView = monthlyEmissions[Integer.parseInt(monthAbs) - 1];

                //Display weekly emissions by default
                totalEmissionsTextView.setText("You've emitted --- kg CO2e this ---");

                //Emissions Chart


                //Line chart for emissions trend graph
                LineDataSet dataset1 = new LineDataSet(dailyEmissions, "Daily Emissions");
                lineChart.setData(new LineData(dataset1));
                lineChart.invalidate();

                dailyButton = findViewById(R.id.dailyButton);
                weeklyButton = findViewById(R.id.weeklyButton);
                monthlyButton = findViewById(R.id.monthlyButton);
                //Display total weekly emissions for textview
                totalWeeklyEmissionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        totalEmissionsTextView.setText("You've emitted " + weeklyEmissionsForTextView + " kg CO2e this week.");
                    }
                });
                //Display total monthly emissions for textview
                totalMonthlyEmissionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        totalEmissionsTextView.setText("You've emitted " + monthlyEmissionsForTextView + " kg CO2e this month.");
                    }
                });
                //Display total yearly emissions for textview
                totalYearlyEmissionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        totalEmissionsTextView.setText("You've emitted " + yearlyEmissionsForTextView + " kg CO2e this year.");
                    }
                });
                //Display daily emissions trend graph
                dailyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lineChart.setData(new LineData(new LineDataSet(dailyEmissions, "Daily Emissions (kg CO2e)")));
                        EmissionsLineChart.setBottomLabelDaily(lineChart);
                        lineChart.invalidate();
                    }
                });
                //Display weekly emissions trend graph
                weeklyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lineChart.setData(new LineData(new LineDataSet(weeklyEmissionsEntry, "Weekly Emissions (kg CO2e)")));
                        EmissionsLineChart.setBottomLabelWeekly(lineChart);
                        lineChart.invalidate();
                    }
                });
                //Display monthly emissions trend graph
                monthlyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lineChart.setData(new LineData(new LineDataSet(monthlyEmissionsEntry, "Monthly Emissions (kg CO2e)")));
                        EmissionsLineChart.setBottomLabelMonthly(lineChart);
                        lineChart.invalidate();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Average comparison
        loadFragment(R.id.gaugeAvgComparisonFragmentContainer, new AvgComparisonFragment());
    }

    private void loadFragment(int fragContainerID, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(fragContainerID, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}