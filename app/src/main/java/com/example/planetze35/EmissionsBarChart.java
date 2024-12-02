package com.example.planetze35;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class EmissionsBarChart {


    public static void setDefaultBarChart(BarChart barChart) {
        barChart.getAxisRight().setDrawLabels(false);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        //yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("");xValues.add("Transportation");xValues.add("Energy Use");xValues.add("Consumption");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.setAutoScaleMinMaxEnabled(true);
    }
}
