package org.example;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

//table model to manage the data for the food diary JTable.
public class FoodTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Aliment", "Calories", "Quantity", "Total Kcal", "Total Day Kcal", "Action"};
    private final List<Object[]> data = new ArrayList<>();

    //adds a new row with default "air" values to the table.
    public void addNewRow() {
        addRow(new Object[]{"air", 0, 0.0});
    }

    //adds a specific row of data to the table, used when loading from a file.
    public void addRow(Object[] row) {
        data.add(row);
        // Notify the JTable that a row has been inserted so it can update the display
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
        fireTableRowsUpdated(data.size(), data.size()); // Also update the total row
    }

    public void clearData() {
        int rowCount = data.size();
        if (rowCount == 0) return;
        data.clear();
        // notify the JTable that all rows have been deleted
        fireTableRowsDeleted(0, rowCount - 1);
    }

    //removes specific row
    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            data.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
            fireTableRowsUpdated(data.size(), data.size()); // Update total
        }
    }

    public List<Object[]> getData() {
        return data;
    }

    @Override
    public int getRowCount() {
        return data.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex >= data.size()) return false;
        // only the first 3 columns (Aliment, Calories, Quantity) and the Action(delete) column are editable
        return columnIndex <= 2 || columnIndex == 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // handle the "Total" row at the bottom
        if (rowIndex == data.size()) {
            if (columnIndex == 4) { // Grand total column
                double grandTotal = data.stream().mapToDouble(row -> (Integer) row[1] * (Double) row[2]).sum();
                return String.format("%.2f", grandTotal);
            }
            return "";
        }

        // handle normal data rows
        Object[] rowData = data.get(rowIndex);
        switch (columnIndex) {
            case 0: case 1: case 2:
                return rowData[columnIndex]; // return raw data for editable cells
            case 3: // "total Kcal for Aliment" is calculated on the fly
                return String.format("%.2f", (Integer) rowData[1] * (Double) rowData[2]);
            case 4:
                return ""; // this column is empty for data rows
            case 5:
                return "Delete"; // text for the delete button
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex >= data.size()) return;

        Object[] rowData = data.get(rowIndex);
        try {
            switch (columnIndex) {
                case 0: rowData[columnIndex] = aValue; break;
                case 1: rowData[columnIndex] = Integer.parseInt(aValue.toString()); break;
                case 2: rowData[columnIndex] = Double.parseDouble(aValue.toString()); break;
            }
            // notify the table that cells need to be redrawn with new values
            fireTableCellUpdated(rowIndex, columnIndex);
            fireTableCellUpdated(rowIndex, 3);
            fireTableCellUpdated(getRowCount() - 1, 4);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format entered.");
        }
    }
}