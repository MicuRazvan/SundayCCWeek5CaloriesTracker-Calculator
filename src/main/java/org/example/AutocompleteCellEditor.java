package org.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutocompleteCellEditor extends DefaultCellEditor {

    private final Map<String, Integer> foodData;
    private JPopupMenu popupMenu;
    private JList<String> suggestionList;
    private DefaultListModel<String> listModel;
    private JTable table;
    private int currentRow;

    private final DocumentListener documentListener;

    public AutocompleteCellEditor(Map<String, Integer> foodData) {
        super(new JTextField());
        this.foodData = foodData;

        popupMenu = new JPopupMenu();
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        popupMenu.add(new JScrollPane(suggestionList));
        popupMenu.setFocusable(false);

        this.documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        };
        getComponent().getDocument().addDocumentListener(this.documentListener);

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSuggestion();
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.currentRow = row;
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    private void updateSuggestions() {
        SwingUtilities.invokeLater(() -> {
            JTextField editor = getComponent();

            if (!editor.isShowing()) {
                return;
            }

            String currentText = editor.getText().toLowerCase();
            listModel.clear();

            if (currentText.isEmpty()) {
                popupMenu.setVisible(false);
                return;
            }

            List<String> matches = new ArrayList<>();
            for (String foodName : foodData.keySet()) {
                if (foodName.toLowerCase().contains(currentText)) {
                    matches.add(foodName);
                }
            }

            if (!matches.isEmpty()) {
                matches.forEach(listModel::addElement);
                popupMenu.show(editor, 0, editor.getHeight());
                popupMenu.pack();
            } else {
                popupMenu.setVisible(false);
            }
        });
    }

    private void selectSuggestion() {
        String selectedValue = suggestionList.getSelectedValue();
        if (selectedValue != null) {
            Integer calories = foodData.get(selectedValue);
            JTextField editor = getComponent();

            editor.getDocument().removeDocumentListener(documentListener);

            editor.setText(selectedValue);

            editor.getDocument().addDocumentListener(documentListener);

            if (calories != null) {
                table.setValueAt(calories, currentRow, 1);
            }

            popupMenu.setVisible(false);
            stopCellEditing();
        }
    }

    @Override
    public JTextField getComponent() {
        return (JTextField) super.getComponent();
    }
}
