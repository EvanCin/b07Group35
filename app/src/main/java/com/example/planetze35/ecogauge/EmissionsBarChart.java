package com.example.planetze35.ecogauge;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class EmissionsBarChart {

    BarChart barChart;

    EmissionsBarChart(BarChart barChart) {
        this.barChart = barChart;
    }

    public void setDefaultBarChart() {
        barChart.getAxisRight().setDrawLabels(false);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        //yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);

        barChart.getDescription().setEnabled(false);
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

    public void setBarChartData(ArrayList<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Category Emissions (kg CO2e)");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.85f);
        System.out.println("dataSet: " + dataSet);
        System.out.println("barData: " + barData);
        barChart.setData(barData);
    }
}
