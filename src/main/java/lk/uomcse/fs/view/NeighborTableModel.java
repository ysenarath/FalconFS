package lk.uomcse.fs.view;

import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dulanjaya
 * @since 10/24/2017
 */
public class NeighborTableModel extends DefaultTableModel {
    private List<Neighbour> neighbors;

    public NeighborTableModel(List<Neighbour> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "IP";
            case 1:
                return "Port";
            case 2:
                return "Health";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return neighbors.get(row).getNode().getIp();
            case 1:
                return neighbors.get(row).getNode().getPort();
            case 2:
                return neighbors.get(row).getHealth();
            default:
                return null;
        }
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        int length = 0;
        synchronized (this) {
            if (this.neighbors != null) {
                length = neighbors.size();
            }
        }
        return length;
    }
}
