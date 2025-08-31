package org.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Objects;

public class MainWindow extends JFrame {

    private final String dayName;
    private FoodTableModel tableModel;
    private JTable table;
    private boolean hasUnsavedChanges = false;

    // UI components for the calculator panel
    private JTextField weightField, heightField, ageField;
    private JRadioButton manRadio, womanRadio;
    private JComboBox<String> activityComboBox;
    private JLabel bmrResultLabel, tdeeResultLabel, loss1Label, loss2Label;
    private JPanel calculatorPanel;
    private JCheckBox showCalcCheckBox;

    public MainWindow(String dayName) {
        super("Calorie Tracker - " + dayName);
        this.dayName = dayName;
        initializeUI();
        setupWindowListener();
    }

    public MainWindow(String dayName, File dayFileToLoad) {
        this(dayName);
        loadDayFromFile(dayFileToLoad);
    }

    private void initializeUI() {
        // Main table setup
        tableModel = new FoodTableModel();
        tableModel.addTableModelListener(e -> markAsChanged()); // Listen for any data changes
        table = new JTable(tableModel);
        setupTable();

        // Bottom button panel
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> tableModel.addNewRow());
        JButton saveButton = new JButton("Save Day");
        saveButton.addActionListener(e -> saveDayToFile());
        JButton saveAlimentButton = new JButton("Save Aliment");
        saveAlimentButton.addActionListener(e -> showSaveAlimentDialog());
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> goBackToStartWindow());

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomButtonPanel.add(addButton);
        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(saveAlimentButton);
        bottomButtonPanel.add(backButton);

        // Right-side calculator panel
        calculatorPanel = createCalculatorPanel();
        showCalcCheckBox = new JCheckBox("Calculate Caloric Needs");
        showCalcCheckBox.addActionListener(e -> calculatorPanel.setVisible(showCalcCheckBox.isSelected()));
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(showCalcCheckBox, BorderLayout.NORTH);
        rightPanel.add(calculatorPanel, BorderLayout.CENTER);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(bottomButtonPanel, BorderLayout.SOUTH);
        contentPane.add(rightPanel, BorderLayout.EAST);

        setSize(1100, 600);
        setLocationRelativeTo(null);
    }

    private void loadDayFromFile(File dayFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(dayFile))) {
            tableModel.clearData();
            String line;
            boolean inFoodSection = false;
            boolean hasCalcData = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // ignore empty lines

                // handle section transitions
                if (line.trim().equals("# FOOD DIARY")) {
                    inFoodSection = true;
                    br.readLine();
                    continue;
                }
                if (line.trim().equals("# CALORIC NEEDS DATA")) {
                    continue;
                }

                // parse data based on the current section
                if (inFoodSection) {
                    String[] parts = line.replace("\"", "").split(",");
                    if (parts.length == 3) {
                        tableModel.addRow(new Object[]{parts[0], Integer.parseInt(parts[1]), Double.parseDouble(parts[2])});
                    }
                } else { // in calculator section
                    String[] parts = line.split(",", 2);
                    if (parts.length < 2 || parts[1].trim().isEmpty() || parts[1].trim().equalsIgnoreCase("null")) continue;
                    hasCalcData = true;
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    switch (key) {
                        case "Weight": weightField.setText(value); break;
                        case "Height": heightField.setText(value); break;
                        case "Age": ageField.setText(value); break;
                        case "Gender":
                            if (value.equals("Man")) manRadio.setSelected(true);
                            else womanRadio.setSelected(true);
                            break;
                        case "Activity Level": activityComboBox.setSelectedItem(value); break;
                    }
                }
            }

            // update UI after loading
            if (hasCalcData) {
                showCalcCheckBox.setSelected(true);
                calculatorPanel.setVisible(true);
            }
            updateCalculations();
            SwingUtilities.invokeLater(() -> hasUnsavedChanges = false); // Defer to ensure UI is stable

        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Failed to load or parse day file: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDayToFile() {
        File daysDir = new File("days");
        if (!daysDir.exists()) { daysDir.mkdir(); }
        File outputFile = new File(daysDir, dayName + ".csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            // write calculator data
            pw.println("# CALORIC NEEDS DATA");
            pw.printf("Weight,%s%n", weightField.getText());
            pw.printf("Height,%s%n", heightField.getText());
            pw.printf("Age,%s%n", ageField.getText());
            pw.printf("Gender,%s%n", manRadio.isSelected() ? "Man" : "Woman");
            pw.printf("Activity Level,%s%n", Objects.requireNonNull(activityComboBox.getSelectedItem()));

            // write food diary data
            pw.println("# FOOD DIARY");
            pw.println("Aliment,Calories,Quantity");
            for (Object[] rowData : tableModel.getData()) {
                pw.printf("\"%s\",%d,%.2f%n", rowData[0], rowData[1], rowData[2]);
            }

            hasUnsavedChanges = false;
            setTitle("Calorie Tracker - " + dayName);
            JOptionPane.showMessageDialog(this, "Day saved successfully!", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAsChanged() {
        if (!hasUnsavedChanges) {
            hasUnsavedChanges = true;
            setTitle(getTitle() + "*");
        }
    }

    private void setupTable() {
        table.setFillsViewportHeight(true);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(100);

        TableColumn alimentColumn = columnModel.getColumn(0);
        alimentColumn.setCellEditor(new AutocompleteCellEditor(Main.foodCalorieMap)); // Or CalorieTrackerApp.foodCalorieMap

        // delete Button
        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.parseInt(e.getActionCommand());
                tableModel.removeRow(modelRow);
            }
        };

        new ButtonColumn(table, deleteAction, 5);
    }

    private void setupWindowListener() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (hasUnsavedChanges) {
                    int response = JOptionPane.showConfirmDialog(MainWindow.this,
                            "You have unsaved changes. Are you sure you want to exit?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });
    }

    private JPanel createCalculatorPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Caloric Needs Calculator"));

        // BMR Section
        JPanel bmrPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        bmrPanel.setBorder(BorderFactory.createTitledBorder("1. Calculate BMR"));
        weightField = new JTextField("0");
        heightField = new JTextField("0");
        ageField = new JTextField("0");
        manRadio = new JRadioButton("Man", true);
        womanRadio = new JRadioButton("Woman");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(manRadio);
        genderGroup.add(womanRadio);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(manRadio);
        genderPanel.add(womanRadio);
        bmrResultLabel = new JLabel("BMR: 0.0 kcal");
        bmrPanel.add(new JLabel("Weight (kg):"));
        bmrPanel.add(weightField);
        bmrPanel.add(new JLabel("Height (cm):"));
        bmrPanel.add(heightField);
        bmrPanel.add(new JLabel("Age (years):"));
        bmrPanel.add(ageField);
        bmrPanel.add(new JLabel("Gender:"));
        bmrPanel.add(genderPanel);
        bmrPanel.add(new JLabel("Result:"));
        bmrPanel.add(bmrResultLabel);

        // TDEE Section
        JPanel tdeePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        tdeePanel.setBorder(BorderFactory.createTitledBorder("2. Adjust for Activity (TDEE)"));
        String[] activityLevels = {"Sedentary (little/no exercise)", "Lightly active (1–3 days/week)", "Moderately active (3–5 days/week)", "Very active (6–7 days/week)", "Extremely active (athlete)"};
        activityComboBox = new JComboBox<>(activityLevels);
        tdeeResultLabel = new JLabel("TDEE: 0.0 kcal");
        tdeePanel.add(new JLabel("Activity Level:"));
        tdeePanel.add(activityComboBox);
        tdeePanel.add(new JLabel("Result:"));
        tdeePanel.add(tdeeResultLabel);

        // Deficit Section
        JPanel deficitPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        deficitPanel.setBorder(BorderFactory.createTitledBorder("3. Caloric Deficit for Weight Loss"));
        loss1Label = new JLabel("For 0.5 kg/week loss: 0.0 kcal");
        loss2Label = new JLabel("For 1 kg/week loss: 0.0 kcal");
        deficitPanel.add(loss1Label);
        deficitPanel.add(loss2Label);

        DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateAndMark(); }
            public void removeUpdate(DocumentEvent e) { updateAndMark(); }
            public void insertUpdate(DocumentEvent e) { updateAndMark(); }
        };
        weightField.getDocument().addDocumentListener(listener);
        heightField.getDocument().addDocumentListener(listener);
        ageField.getDocument().addDocumentListener(listener);
        manRadio.addActionListener(e -> updateAndMark());
        womanRadio.addActionListener(e -> updateAndMark());
        activityComboBox.addActionListener(e -> updateAndMark());

        mainPanel.add(bmrPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(tdeePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(deficitPanel);
        mainPanel.setVisible(false);
        return mainPanel;
    }

    private void updateAndMark() {
        markAsChanged();
        updateCalculations();
    }

    private void updateCalculations() {
        try {
            double weight = Double.parseDouble(weightField.getText());
            double height = Double.parseDouble(heightField.getText());
            int age = Integer.parseInt(ageField.getText());
            double bmr = manRadio.isSelected() ? (10 * weight + 6.25 * height - 5 * age + 5) : (10 * weight + 6.25 * height - 5 * age - 161);
            bmrResultLabel.setText(String.format("BMR: %.2f kcal", bmr));

            double activityFactor = 1.2;
            switch (Objects.requireNonNull(activityComboBox.getSelectedIndex())) {
                case 1: activityFactor = 1.375; break;
                case 2: activityFactor = 1.55; break;
                case 3: activityFactor = 1.725; break;
                case 4: activityFactor = 1.9; break;
            }
            double tdee = bmr * activityFactor;
            tdeeResultLabel.setText(String.format("TDEE: %.2f kcal", tdee));

            loss1Label.setText(String.format("For 0.5 kg/week loss: %.2f kcal", tdee - 500));
            loss2Label.setText(String.format("For 1 kg/week loss: %.2f kcal", tdee - 1000));
        } catch (NumberFormatException ex) {
            // reset labels if input is not a valid number
            bmrResultLabel.setText("BMR: Invalid input");
            tdeeResultLabel.setText("TDEE: Invalid input");
            loss1Label.setText("For 0.5 kg/week loss: N/A");
            loss2Label.setText("For 1 kg/week loss: N/A");
        }
    }

    private void showSaveAlimentDialog() {
        JTextField nameField = new JTextField(20);
        JTextField caloriesField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Calories:"));
        panel.add(caloriesField);

        // dialog with "Save" and "Cancel" options
        int result = JOptionPane.showConfirmDialog(this, panel,
                "Save New Aliment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // if clicked "Save" (OK)
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String caloriesText = caloriesField.getText().trim();

            // validation
            if (name.isEmpty() || caloriesText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both Name and Calories fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (name.contains(",")) {
                JOptionPane.showMessageDialog(this, "The aliment name cannot contain a comma (,).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int calories = Integer.parseInt(caloriesText);

                Main.foodCalorieMap.put(name, calories);

                appendAlimentToCsv(name, calories);

                JOptionPane.showMessageDialog(this, "Aliment saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Calories must be a valid whole number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void appendAlimentToCsv(String name, int calories) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("samples.csv", true))) {
            pw.printf("\"%s\",%d%n", name, calories);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving the new aliment to samples.csv:\n" + e.getMessage(), "File Write Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void goBackToStartWindow() {
        // check for unsaved changes, using the same logic as closing the window.
        if (hasUnsavedChanges) {
            int response = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Are you sure you want to go back to the menu?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            // if selected "No", cancel the action and do nothing.
            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        this.dispose();

        Main.createAndShowStartUI();
    }
}