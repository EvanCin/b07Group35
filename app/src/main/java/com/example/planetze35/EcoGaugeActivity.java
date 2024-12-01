package com.example.planetze35;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EcoGaugeActivity extends AppCompatActivity {

    Button totalWeeklyEmissionsButton, totalMonthlyEmissionsButton, totalYearlyEmissionsButton;
    TextView totalEmissionsTextView;
    BarChart emissionsChart;
    LineChart lineChart;
    Button dailyButton, weeklyButton, monthlyButton;
    FirebaseUser user;
    DatabaseReference mDatabase;

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

        DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference().child("users/defaultUserId/DailyActivities");
        dbNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Firebase", "Success");
                HashMap<String,Object> datesHS = (HashMap<String, Object>) snapshot.getValue();
                Log.d("HI","HI");
                //System.out.println(String.valueOf(datesHS));

                HashMap<String, Object> datesMap = new HashMap<>();
                HashMap<String, String > dateEmissionMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : datesHS.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    datesMap.put(key, value);
                    HashMap<String,Object> temp = (HashMap<String, Object>) datesHS.get(key);
                    if(key != null) {
                        dateEmissionMap.put(key, String.valueOf(temp.get("total_daily_emissions")));
                    }
                }
                for(String key: dateEmissionMap.keySet()) {
                    System.out.println(key + " " + dateEmissionMap.get(key));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        totalWeeklyEmissionsButton = findViewById(R.id.totalWeeklyEmissionButton);
        totalMonthlyEmissionsButton = findViewById(R.id.totalMonthlyEmissionsButton);
        totalYearlyEmissionsButton = findViewById(R.id.totalYearlyEmissionsButton);
        totalEmissionsTextView = findViewById(R.id.totalEmissionsTextView);

        //Display weekly emissions by default
        totalEmissionsTextView.setText("You've emitted 16 kg CO2e this week");

        //Emissions Chart
        emissionsChart = findViewById(R.id.emissionsChart);
        emissionsChart.getAxisRight().setDrawLabels(false);
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 20));
        entries.add(new BarEntry(2f, 38));
        entries.add(new BarEntry(3f, 69));
        entries.add(new BarEntry(4f, 27));

        YAxis yAxis = emissionsChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        BarDataSet dataSet = new BarDataSet(entries, "Subjects");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.85f);
        emissionsChart.setData(barData);

        emissionsChart.getDescription().setEnabled(false);
        emissionsChart.invalidate();
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("one");xValues.add("one");xValues.add("two");xValues.add("three");xValues.add("four");
        emissionsChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        emissionsChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        emissionsChart.getXAxis().setGranularity(1f);
        emissionsChart.getXAxis().setGranularityEnabled(true);
        emissionsChart.getAxisLeft().setDrawGridLines(false);
        emissionsChart.getAxisRight().setDrawGridLines(false);
        emissionsChart.getXAxis().setDrawGridLines(false);
        emissionsChart.setAutoScaleMinMaxEnabled(true);


        //Line chart for emissions trend graph
        lineChart = findViewById(R.id.linechart);
        LineDataSet dataset1 = new LineDataSet(getdata(),"dataset 1");
        LineDataSet dataset2 = new LineDataSet(getdata2(), "dataset 2");
        ArrayList<ILineDataSet> datasets=new ArrayList<>();
        datasets.add(dataset1);
        datasets.add(dataset2);
        LineData lineData = new LineData(datasets);
        lineChart.setData(lineData);
//        lineChart.invalidate();
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        dailyButton = findViewById(R.id.dailyButton);
        weeklyButton = findViewById(R.id.weeklyButton);
        monthlyButton = findViewById(R.id.monthlyButton);

        //Display total weekly emissions for textview
        totalWeeklyEmissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalEmissionsTextView.setText("You've emitted 16 kg CO2e this week.");
            }
        });
        //Display total monthly emissions for textview
        totalMonthlyEmissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalEmissionsTextView.setText("You've emitted 67 kg CO2e this month.");
            }
        });
        //Display total yearly emissions for textview
        totalYearlyEmissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalEmissionsTextView.setText("You've emitted 720 kg CO2e this year.");
            }
        });

        //Display daily emissions trend graph
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setData(new LineData(new LineDataSet(testData1(), "testData1")));
                lineChart.invalidate();
            }
        });
        //Display weekly emissions trend graph
        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setData(new LineData(new LineDataSet(testData2(), "testData2")));
                lineChart.invalidate();
            }
        });
        //Display monthly emissions trend graph
        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setData(new LineData(new LineDataSet(testData3(), "testData3")));
                lineChart.invalidate();
            }
        });
    }
    private List<Entry> getdata(){
        List<Entry> entries=new ArrayList<>();
        entries.add(new Entry(1,20));
        entries.add(new Entry(2,14));
        entries.add(new Entry(3,8));
        entries.add(new Entry(4,12));
        entries.add(new Entry(5,24));

        return entries;
    }
    private List<Entry> getdata2(){
        List<Entry> entries=new ArrayList<>();
        entries.add(new Entry(1,22));
        entries.add(new Entry(2,8));
        entries.add(new Entry(3,3));
        entries.add(new Entry(4,18));
        entries.add(new Entry(5,6));

        return entries;
    }
    private List<Entry> testData1(){
        List<Entry> entries=new ArrayList<>();
        entries.add(new Entry(1,10));
        entries.add(new Entry(2,10));
        entries.add(new Entry(3,10));
        entries.add(new Entry(4,10));
        entries.add(new Entry(5,10));

        return entries;
    }
    private List<Entry> testData2(){
        List<Entry> entries=new ArrayList<>();
        entries.add(new Entry(1,20));
        entries.add(new Entry(2,20));
        entries.add(new Entry(3,20));
        entries.add(new Entry(4,20));
        entries.add(new Entry(5,20));

        return entries;
    }
    private List<Entry> testData3(){
        List<Entry> entries=new ArrayList<>();
        entries.add(new Entry(1,15));
        entries.add(new Entry(2,15));
        entries.add(new Entry(3,15));
        entries.add(new Entry(4,15));
        entries.add(new Entry(5,15));

        return entries;
    }

}