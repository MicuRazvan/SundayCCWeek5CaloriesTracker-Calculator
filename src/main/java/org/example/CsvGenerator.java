package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvGenerator {

    public void createSampleCsv() {
        File csvFile = new File("samples.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            // Write the header of the CSV file
            pw.println("name,calories");

            List<String[]> data = new ArrayList<>();

            // Fruits
            data.add(new String[]{"Apple (medium piece)", "95"});
            data.add(new String[]{"Apricot (100g)", "48"});
            data.add(new String[]{"Avocado (100g)", "160"});
            data.add(new String[]{"Banana (medium piece)", "105"});
            data.add(new String[]{"Blackberries (100g)", "43"});
            data.add(new String[]{"Cherries (100g)", "50"});
            data.add(new String[]{"Clementines (medium piece)", "35"});
            data.add(new String[]{"Fresh Coconut (100g)", "354"});
            data.add(new String[]{"Cranberries (100g)", "46"});
            data.add(new String[]{"Dried Dates (100g)", "282"});
            data.add(new String[]{"Fresh Figs (100g)", "74"});
            data.add(new String[]{"Grapefruit (medium piece)", "52"});
            data.add(new String[]{"Grapes (100g)", "69"});
            data.add(new String[]{"Kiwi (medium piece)", "42"});
            data.add(new String[]{"Lemon (medium piece)", "29"});
            data.add(new String[]{"Lime (medium piece)", "20"});
            data.add(new String[]{"Mango (100g)", "60"});
            data.add(new String[]{"Honeydew Melon (100g)", "36"});
            data.add(new String[]{"Orange (medium piece)", "62"});
            data.add(new String[]{"Peaches (medium piece)", "59"});
            data.add(new String[]{"Pear (medium piece)", "101"});
            data.add(new String[]{"Pineapple (100g)", "50"});
            data.add(new String[]{"Strawberries (100g)", "32"});
            data.add(new String[]{"Watermelon (100g)", "30"});

            // Vegetables
            data.add(new String[]{"Artichoke (100g)", "47"});
            data.add(new String[]{"Asparagus (100g)", "20"});
            data.add(new String[]{"Beetroot (100g)", "43"});
            data.add(new String[]{"Broccoli (100g)", "34"});
            data.add(new String[]{"Brussels Sprouts (100g)", "43"});
            data.add(new String[]{"Cabbage (100g)", "25"});
            data.add(new String[]{"Carrot (medium piece)", "25"});
            data.add(new String[]{"Cauliflower (100g)", "25"});
            data.add(new String[]{"Celery (100g)", "16"});
            data.add(new String[]{"Corn (100g)", "86"});
            data.add(new String[]{"Cucumber (100g)", "15"});
            data.add(new String[]{"Green Beans (100g)", "31"});
            data.add(new String[]{"Kale (100g)", "49"});
            data.add(new String[]{"Leek (100g)", "61"});
            data.add(new String[]{"Iceberg Lettuce (100g)", "14"});
            data.add(new String[]{"Mushroom (100g)", "22"});
            data.add(new String[]{"Onion (100g)", "40"});
            data.add(new String[]{"Peas (100g)", "81"});
            data.add(new String[]{"Red Pepper (100g)", "31"});
            data.add(new String[]{"Potato (100g)", "77"});
            data.add(new String[]{"Pumpkin (100g)", "26"});
            data.add(new String[]{"Radish (100g)", "16"});
            data.add(new String[]{"Spinach (100g)", "23"});
            data.add(new String[]{"Sweet Potato (100g)", "86"});
            data.add(new String[]{"Tomato (100g)", "18"});
            data.add(new String[]{"Zucchini (100g)", "17"});

            // Grains, Beans, and Pasta
            data.add(new String[]{"Barley (100g cooked)", "123"});
            data.add(new String[]{"Black Beans (100g cooked)", "132"});
            data.add(new String[]{"Brown Rice (100g cooked)", "111"});
            data.add(new String[]{"Chickpeas (100g cooked)", "164"});
            data.add(new String[]{"Lentils (100g cooked)", "116"});
            data.add(new String[]{"Oats (100g raw)", "389"});
            data.add(new String[]{"Pasta (100g cooked)", "131"});
            data.add(new String[]{"Quinoa (100g cooked)", "120"});
            data.add(new String[]{"White Bread (1 slice)", "75"});
            data.add(new String[]{"White Flour (100g)", "364"});
            data.add(new String[]{"White Rice (100g cooked)", "130"});
            data.add(new String[]{"Whole Wheat Bread (1 slice)", "81"});
            data.add(new String[]{"Whole Wheat Flour (100g)", "340"});

            // Meat and Poultry
            data.add(new String[]{"Bacon pork (100g cooked)", "541"});
            data.add(new String[]{"Lean Beef Mince (100g cooked)", "215"});
            data.add(new String[]{"Chicken Breast (100g cooked)", "165"});
            data.add(new String[]{"Ground Beef (100g cooked)", "250"});
            data.add(new String[]{"Roast Chicken (100g)", "239"});
            data.add(new String[]{"Roast Beef (100g)", "200"});
            data.add(new String[]{"Turkey Breast (100g cooked)", "135"});

            // Fish and Seafood
            data.add(new String[]{"Cod (100g cooked)", "82"});
            data.add(new String[]{"Salmon (100g cooked)", "208"});
            data.add(new String[]{"Shrimp (100g cooked)", "99"});
            data.add(new String[]{"Tuna in water (100g)", "116"});

            // Dairy and Eggs
            data.add(new String[]{"Boiled Egg (large piece)", "78"});
            data.add(new String[]{"Butter (100g)", "717"});
            data.add(new String[]{"Cheddar Cheese (100g)", "404"});
            data.add(new String[]{"Cottage Cheese (100g)", "98"});
            data.add(new String[]{"Whole Milk (100g)", "61"});
            data.add(new String[]{"Plain Yogurt (100g)", "59"});

            // Nuts and Seeds
            data.add(new String[]{"Almonds (100g)", "579"});
            data.add(new String[]{"Chia Seeds (100g)", "486"});
            data.add(new String[]{"Flaxseed (100g)", "534"});
            data.add(new String[]{"Peanut Butter (100g)", "588"});
            data.add(new String[]{"Peanuts (100g)", "567"});
            data.add(new String[]{"Walnuts (100g)", "654"});

            // Oils and Fats
            data.add(new String[]{"Avocado Oil (100g)", "884"});
            data.add(new String[]{"Canola Oil (100g)", "884"});
            data.add(new String[]{"Olive Oil (100g)", "884"});

            // Write data to the file
            for (String[] record : data) {
                pw.println(String.join(",", record));
            }

            System.out.println("samples.csv created successfully!");

        } catch (IOException e) {
            System.err.println("An error occurred while writing the CSV file.");
            e.printStackTrace();
        }
    }
}