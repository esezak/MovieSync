package project.client.UI.Elements.Panels;
import project.client.UiMainWindow;
import project.client.UI.Elements.SimpleButton;
import org.json.JSONArray;
import org.json.JSONObject;

import static project.client.UiMainWindow.GLOBAL_FONT;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WatchlistPanel extends SimplePanel{
    class WatchilstTableModel extends AbstractTableModel {
        private String[] columnNames = {"Movie Name", "Date Added", "Rating","Status"};
        private ArrayList<Object[]> data = new ArrayList<>();
        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex)[columnIndex];

        }
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public void addRow(Object[] rowData) {
            data.add(rowData);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        }
        public void removeSelectedRow(int row) {
            if (row >= 0 && row < data.size()) {
                data.remove(row);
                fireTableRowsDeleted(row, row);
                fireTableDataChanged();
            }
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            switch (columnIndex){
                case 2, 3 -> {
                    return rowIndex != 0;
                }
                default -> {
                    return false;
                }
            }
        }
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            data.get(rowIndex)[columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
            fireTableDataChanged();
        }



    }

    private JTable table;
    private SimpleButton saveButton;
    private String data;
    private UiMainWindow uiMainWindow;
    private WatchilstTableModel tableModel;
    private JTable watchlistTable;
    private JComboBox<Integer> ratingComboBox = new JComboBox<>(
            new Integer[]{0,1,2,3,4,5,6,7,8,9,10}
    );
    private JComboBox<String> statusComboBox = new JComboBox<>(
            new String[]{"Watching","Plan to Watch","Dropped","Watched"}
    );
    public WatchlistPanel(String data, UiMainWindow uiMainWindow) {
        super();
        this.uiMainWindow = uiMainWindow;
//        this.data = data;
//        WatchilstTableModel tableModel = new WatchilstTableModel();
//        JSONArray jsonArray = new JSONArray(data);
//        Object[] row = new Object[]{"Movie Name","Date Added","Rating","Status"};
//        tableModel.addRow(row);
//        for(int i = 0; i < jsonArray.length(); i++){
//            row = new Object[5];
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            row[4] = jsonObject.getString("movie_id");
//            row[0] = jsonObject.getString("title");
//            row[1] = jsonObject.getString("date_added");
//            row[2] = jsonObject.getInt("user_rating");
//            row[3] = jsonObject.getString("status");
//            tableModel.addRow(row);
//        }
//        table = new JTable(tableModel);
//        table.setRowHeight(30);
//        TableColumnModel columns = table.getColumnModel();
//        columns.getColumn(0).setMaxWidth(400);
//        columns.getColumn(0).setMinWidth(400);
//        columns.getColumn(1).setMaxWidth(230);
//        columns.getColumn(1).setMinWidth(230);
//        columns.getColumn(2).setMaxWidth(100);
//        columns.getColumn(2).setMinWidth(100);
//        TableColumn editableColumn = table.getColumnModel().getColumn(2);
//        editableColumn.setCellEditor(new DefaultCellEditor(ratingComboBox));
//        editableColumn = table.getColumnModel().getColumn(3);
//        editableColumn.setCellEditor(new DefaultCellEditor(statusComboBox));
        // Initializing the JTable
//        table.setBackground(Color.gray);
//        table.setFont(new Font(GLOBAL_FONT.getFontName(), Font.PLAIN, 22));
//        panel.add(table, BorderLayout.NORTH);
//        saveButton = new SimpleButton("Save");
//        saveButton.getButton().addActionListener(new onSaveButtonClick());
//        panel.add(saveButton.getButton(), BorderLayout.SOUTH);
        initialize(data);
        saveButton = new SimpleButton("Save");
        saveButton.getButton().addActionListener(new onSaveButtonClick());
        panel.add(table, BorderLayout.NORTH);
        panel.add(saveButton.getButton(), BorderLayout.SOUTH);
        display();
    }
    public void initialize(String data) {
        this.data = data;
        tableModel = new WatchilstTableModel();
        JSONArray jsonArray = new JSONArray(data);
        Object[] row = new Object[]{"Movie Name","Date Added","Rating","Status"};
        tableModel.addRow(row);
        for(int i = 0; i < jsonArray.length(); i++){
            row = new Object[5];
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            row[4] = jsonObject.getString("movie_id");
            row[0] = jsonObject.getString("title");
            row[1] = jsonObject.getString("date_added");
            row[2] = jsonObject.getInt("user_rating");
            row[3] = jsonObject.getString("status");
            tableModel.addRow(row);
        }
        table = new JTable(tableModel);
        table.setRowHeight(30);
        TableColumnModel columns = table.getColumnModel();
        columns.getColumn(0).setMaxWidth(400);
        columns.getColumn(0).setMinWidth(400);
        columns.getColumn(1).setMaxWidth(230);
        columns.getColumn(1).setMinWidth(230);
        columns.getColumn(2).setMaxWidth(100);
        columns.getColumn(2).setMinWidth(100);
        TableColumn editableColumn = table.getColumnModel().getColumn(2);
        editableColumn.setCellEditor(new DefaultCellEditor(ratingComboBox));
        editableColumn = table.getColumnModel().getColumn(3);
        editableColumn.setCellEditor(new DefaultCellEditor(statusComboBox));
        table.setBackground(Color.gray);
        table.setFont(new Font(GLOBAL_FONT.getFontName(), Font.PLAIN, 22));

    }
    public void display() {
        panel.revalidate();
        panel.repaint();
    }
    private class onSaveButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            JSONArray tempJsonArray = new JSONArray(data);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", uiMainWindow.getUsername());
            jsonArray.put(jsonObject);
            for(int i = 1; i < table.getRowCount(); i++){
                System.out.println("Row"+i+": "+
                        table.getValueAt(i, 0)+" "+
                        table.getValueAt(i, 1)+" "+
                        table.getValueAt(i, 2)+" "+
                        table.getValueAt(i, 3));
                jsonObject = new JSONObject(tempJsonArray.getJSONObject(i-1).toString());
                jsonObject.put("user_rating", table.getValueAt(i, 2));
                jsonObject.put("status", table.getValueAt(i, 3));
                jsonArray.put(jsonObject);

            }
            if (uiMainWindow.connection.sendUpdateWatchListRequest(jsonArray.toString())) {
                String newData = uiMainWindow.connection.getWatchlistRequest(uiMainWindow.getUsername()).getData();
                initialize(newData);
            }

        }
    }
}
