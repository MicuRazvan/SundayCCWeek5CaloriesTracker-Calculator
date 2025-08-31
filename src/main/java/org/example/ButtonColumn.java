package org.example;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


//helper class to add a clickable button to a JTable column.
public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
    private JTable table;
    private Action action;
    private JButton renderButton;
    private JButton editButton;
    private Object editorValue;

    public ButtonColumn(JTable table, Action action, int column) {
        this.table = table;
        this.action = action;

        this.renderButton = new JButton();
        this.editButton = new JButton();
        this.editButton.setFocusPainted(false);
        this.editButton.addActionListener(this);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table.getRowCount() - 1 == row) {
            return null;
        }
        renderButton.setText((value == null) ? "" : value.toString());
        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (table.getRowCount() - 1 == row) {
            return null;
        }
        editButton.setText((value == null) ? "" : value.toString());
        this.editorValue = value;
        return editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return editorValue;
    }

     //this method is called when the button is actually clicked.
    @Override
    public void actionPerformed(ActionEvent e) {
        //get the row index before stopping editing.
        int row = table.convertRowIndexToModel(table.getEditingRow());

        //stop the cell editing process.
        fireEditingStopped();

        // create a new event that contains the correct, captured row number and pass it to the action in MainWindow.
        ActionEvent event = new ActionEvent(
                table,
                ActionEvent.ACTION_PERFORMED,
                String.valueOf(row)
        );
        action.actionPerformed(event);
    }
}