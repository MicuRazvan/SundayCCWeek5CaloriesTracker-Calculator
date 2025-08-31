package org.example;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ChartViewer extends JFrame {
    public ChartViewer(Map<LocalDate, Double> calorieData) {
        super("Calorie Trend Graph");

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Calorie Intake Over Time")
                .xAxisTitle("Date")
                .yAxisTitle("Total Calories (kcal)")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDatePattern("yyyy-MM-dd");
        chart.getStyler().setMarkerSize(5);
        chart.getStyler().setYAxisMin(0.0);

        chart.getStyler().setXAxisTicksVisible(true);
        chart.getStyler().setXAxisMaxLabelCount(calorieData.size());
        chart.getStyler().setXAxisLabelRotation(45);

        // prepare Data for the Chart
        List<Date> dates = calorieData.keySet().stream()
                .map(localDate -> Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .collect(Collectors.toList());
        List<Double> calories = List.copyOf(calorieData.values());

        // add the Data Series to the Chart
        chart.addSeries("Total Calories", dates, calories);

        //display the Chart and UI Components
        JPanel chartPanel = new XChartPanel<>(chart);

        // Back button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            this.dispose();
            Main.createAndShowStartUI();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);

        this.setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}