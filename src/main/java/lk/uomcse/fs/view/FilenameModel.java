package lk.uomcse.fs.view;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * @author Dulanjaya
 * @since 10/25/2017
 */
public class FilenameModel extends DefaultTableModel {
    List<String> filenames;

    public FilenameModel(List<String> filenames) {
        this.filenames = filenames;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        if (filenames != null) return filenames.size();
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

    /**
     * Sets the object value for the cell at <code>column</code> and
     * <code>row</code>.
     */
    @Override
    public void setValueAt(Object aValue, int row, int column) {
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
