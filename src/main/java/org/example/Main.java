package org.example;


import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {

    public static Map<String, Integer> foodCalorieMap = new HashMap<>();

    public static void main(String[] args) {
        //CsvGenerator generator = new CsvGenerator();
        //generator.createSampleCsv();

        loadSamplesCsv();
        SwingUtilities.invokeLater(Main::createAndShowStartUI);
    }

    private static void loadSamplesCsv() {
        String filePath = "samples.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", 2); // Split only on the first comma
                if (values.length == 2) {
                    String foodName = values[0].trim().replace("\"", "");
                    try {
                        int calories = Integer.parseInt(values[1].trim());
                        foodCalorieMap.put(foodName, calories);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid calorie value for: " + foodName);
                    }
                }
            }
            System.out.println("Loaded " + foodCalorieMap.size() + " food items from CSV.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not find or read samples.csv.", "File Not Found Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void createAndShowStartUI() {
        JFrame startFrame = new JFrame("Calorie Tracker");
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setSize(300, 200); // Increased height for the new button
        startFrame.setLocationRelativeTo(null);

        // create UI components
        JButton newDayButton = new JButton("New Day");
        JButton loadDayButton = new JButton("Load Day");
        JButton graphButton = new JButton("Show Graph");

        // set up layout
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10)); // Changed to 3 rows
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(newDayButton);
        panel.add(loadDayButton);
        panel.add(graphButton); // --- NEW ---

        // actions to the buttons
        newDayButton.addActionListener(e -> showNewDayDialog(startFrame));
        loadDayButton.addActionListener(e -> showLoadDayDialog(startFrame));
        graphButton.addActionListener(e -> showCalorieGraph(startFrame)); // --- NEW ---

        startFrame.add(panel);
        startFrame.setVisible(true);
    }

    private static void showNewDayDialog(JFrame parentFrame) {
        JTextField dayNameField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel("Enter a name for the new day:"));
        panel.add(dayNameField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Create New Day", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dayName = dayNameField.getText();
            if (dayName != null && !dayName.trim().isEmpty()) {
                parentFrame.dispose();
                MainWindow mainWindow = new MainWindow(dayName.trim());
                mainWindow.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Day name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void showLoadDayDialog(JFrame parentFrame) {
        File daysDir = new File("days");
        // check if the directory exists and contains any saved days
        if (!daysDir.exists() || !daysDir.isDirectory() || daysDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv")).length == 0) {
            JOptionPane.showMessageDialog(parentFrame, "No saved days found in the 'days' directory.", "No Days Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] savedDays = Arrays.stream(daysDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv")))
                .map(file -> file.getName().replace(".csv", "")).toArray(String[]::new);

        String chosenDay = (String) JOptionPane.showInputDialog(parentFrame, "Choose a day to load:", "Load Day", JOptionPane.PLAIN_MESSAGE, null, savedDays, savedDays[0]);

        if (chosenDay != null) {
            parentFrame.dispose();
            File dayFile = new File(daysDir, chosenDay + ".csv");
            MainWindow mainWindow = new MainWindow(chosenDay, dayFile);
            mainWindow.setVisible(true);
        }
    }

    private static void showCalorieGraph(JFrame parentFrame) {
        File daysDir = new File("days");
        if (!daysDir.exists() || !daysDir.isDirectory()) {
            JOptionPane.showMessageDialog(parentFrame, "The 'days' directory does not exist.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // use a TreeMap to automatically sort the data by date.
        Map<LocalDate, Double> calorieData = new TreeMap<>();

        // list all files
        File[] dayFiles = daysDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        // case where directory is empty or contains no CSV files
        if (dayFiles == null || dayFiles.length == 0) {
            JOptionPane.showMessageDialog(parentFrame, "No saved day files found in the 'days' directory.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (File dayFile : dayFiles) {
            String fileName = dayFile.getName().replace(".csv", "");
            try {
                LocalDate date = LocalDate.parse(fileName);

                double totalCalories = calculateTotalCalories(dayFile);

                calorieData.put(date, totalCalories);

            } catch (DateTimeParseException e) {
                // this is not an error, it just means the file name is not a valid date so we ignore it
                System.out.println("Skipping file with non-date name: " + dayFile.getName());
            }
        }

        if (calorieData.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No valid day files with date names (e.g., YYYY-MM-DD.csv) were found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // if we have data, create and show the chart window.
        new ChartViewer(calorieData);
    }

    private static double calculateTotalCalories(File dayFile) {
        double total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(dayFile))) {
            String line;
            boolean inFoodSection = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("# FOOD DIARY")) {
                    inFoodSection = true;
                    br.readLine(); // skip header
                    continue;
                }

                if (inFoodSection) {
                    String[] parts = line.replace("\"", "").split(",");
                    if (parts.length >= 3) {
                        try {
                            int calories = Integer.parseInt(parts[1].trim());
                            double quantity = Double.parseDouble(parts[2].trim());
                            total += calories * quantity;
                        } catch (NumberFormatException e) {
                            System.err.println("Skipping malformed food entry in file: " + dayFile.getName());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + dayFile.getName());
            e.printStackTrace();
        }
        return total;
    }
}