package com.example.planetze35;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.List;

public class EcoGaugeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    TextView textViewSpinner;
    BarChart emissionsChart;
    LineChart lineChart;
    Button dailyButton, weeklyButton, monthlyButton;

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

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.date, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String date = parent.getItemAtPosition(position).toString();
        textViewSpinner = findViewById(R.id.textViewSpinner);
        textViewSpinner.setText(date);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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