package lk.uomcse.fs.view;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.model.QueryService;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dulanjaya
 * @since 10/25/2017
 */
public class ResultTableModel extends DefaultTableModel {
    private QueryService queryService;

    public ResultTableModel(QueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        int length = 0;
        if (queryService != null) {
            length = queryService.getSearchResults().size();
        }
        return length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        ArrayList<Node> keys = new ArrayList<>(queryService.getSearchResults().keySet());
        Map<Node, List<String>> map = queryService.getSearchResults();
        switch (column) {
            case 0:
                return keys.get(row).getIp();
            case 1:
                return keys.get(row).getPort();
            case 2:
                return String.join(",", map.get(keys.get(row)));
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "IP";
            case 1: return "Port";
            case 2: return "File Names";
            default: return null;
        }
    }
}
