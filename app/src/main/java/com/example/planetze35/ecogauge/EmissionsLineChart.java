package com.example.planetze35.ecogauge;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class EmissionsLineChart {


    public static void setDefaultLineChart(LineChart lineChart) {
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.setDescription(null);
    }
    public static void setDefaultValues(LineChart lineChart) {
        List<Entry> tempEntries = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            tempEntries.add(new Entry(i,0));
        }
        LineDataSet dataset1 = new LineDataSet(tempEntries,null);
        lineChart.setData(new LineData(dataset1));
        lineChart.invalidate();
    }
    public static void setBottomLabelDaily(LineChart lineChart) {
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("Sun");xValues.add("Mon");xValues.add("Tue");xValues.add("Wed");xValues.add("Thu");xValues.add("Fri");xValues.add("Sat");
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
    }
    public static void setBottomLabelWeekly(LineChart lineChart) {
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("Week1");xValues.add("Week2");xValues.add("Week3");xValues.add("Week4");
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
    }
    public static void setBottomLabelMonthly(LineChart lineChart) {
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("Jan");xValues.add("Feb");xValues.add("Mar");xValues.add("Apr");xValues.add("May");xValues.add("Jun");xValues.add("Jul");
        xValues.add("Aug");xValues.add("Sep");xValues.add("Oct");xValues.add("Nov");xValues.add("Dec");
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
    }
}
