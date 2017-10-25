package lk.uomcse.fs.view;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * @author Dulanjaya
 * @since 10/25/2017
 */
public class FilenameModel extends DefaultTableModel{
    ArrayList<String> filenames;

    public FilenameModel(ArrayList<String> filenames) {
        this.filenames = filenames;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        if(filenames !=null) return filenames.size();
        else return 0;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return filenames.get(row);
    }

    @Override
    public String getColumnName(int column) {
        return "File Names";
    }
}
