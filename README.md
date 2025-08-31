# Week 5 Project — Calories Tracker / Calculator

## Context

I lost a bet with a friend and he challenged me that for the next 52 weeks, during weekends I need to create from scratch a new project.

The rules are the following:

*   Each Friday night, he will decide what project I need to do for the weekend.
*   I am allowed to suggest and work on my own ideas if he agrees on them.
*   Once the project is decided, he will tell me if I'm allowed to work Saturday and Sunday, or only Sunday.

This week, continuing the fitness theme from last week's Exercise Tracker, we decided on a **Calories Tracker and Calculator** to help monitor daily food intake and nutritional goals. I was allowed to work on it only Sunday.

## About the Project

This is a desktop application built entirely in Java using the **Swing** framework for the user interface. It provides a solution for tracking daily food consumption, calculating personal caloric needs based on scientific formulas, and visualizing nutritional trends over time.

The application saves each day's log as a `.csv` file in the `/days` directory. For data visualization, the project is using **XChart** library to generate graphs.

## Features

*   **Daily Food Logging:** Create a new sheet for any day (defaults to the current date) and log food items with their calorie counts and quantities (grams, pieces, etc.).
*   **Automatic Calculations:** The main table automatically calculates the total calories for each food item (`calories * quantity`) and provides a running grand total for the day at the bottom.
*   **Built-in Calorie Calculator:**
    *   **BMR Calculation:** Estimates Basal Metabolic Rate using the **Mifflin-St Jeor equation**, based on weight, height, age, and gender.
    *   **TDEE Calculation:** Adjusts BMR for activity level to find Total Daily Energy Expenditure (TDEE).
    *   **Weight Loss Goals:** Calculates the required daily calorie intake for sustainable weight loss goals (0.5 kg/week or 1 kg/week).
*   **Dynamic Food Database:**
    *   **Autocomplete:** The "Aliment" field provides a live search with suggestions from a master food list (`samples.csv`) to speed up data entry. Selecting an item automatically fills the calories.
    *   **Save New Foods:** A "Save Aliment" feature allows you to add new foods and their calorie counts to the master list, making them available for future use.
*   **Data Persistence:**
    *   **Save/Load Days:** Every daily log can be saved and reloaded, preserving both the food entries and the calculator's input data.
    *   **Unsaved Changes Warning:** The application warns you if you try to exit or go back to the menu with unsaved changes.
*   **Trend Visualization:** A "Show Graph" feature on the main menu reads all valid date-named day files, sorts them chronologically, and displays a line chart of your calorie intake over time.

## Project Structure

The project is organized into several classes:

*   `Main` (or `CalorieTrackerApp`): The entry point that launches the application and handles the main menu.
*   `MainWindow`: The primary UI for a single day, containing the food table and calculator panel.
*   `FoodTableModel`: A custom Swing `TableModel` that manages all data and logic for the food log table.
*   `AutocompleteCellEditor`: A custom `TableCellEditor` that provides the live search and suggestion functionality in the food table.
*   `ChartViewer`: A dedicated `JFrame` that uses the XChart library to display the calorie trend graph.
*   `ButtonColumn`: A reusable helper class for adding clickable buttons (like "Delete") into a `JTable` column.

## Examples

* I left multiple examples of saved day files in the `/days` directory.
* I also provided a samples.csv with some type of foods already added. If you want to create a new one, modify the createSampleCsv method from CsvGenerator class
* 2 pictures with the main features:
<img width="798" height="660" alt="chart" src="https://github.com/user-attachments/assets/62abfb49-3ca9-4533-a866-e1ca4897b049" />
<img width="1085" height="589" alt="UI" src="https://github.com/user-attachments/assets/534b7e97-c7a6-44db-be2d-268f7d901050" />

