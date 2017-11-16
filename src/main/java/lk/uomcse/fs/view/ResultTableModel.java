package lk.uomcse.fs.view;

import lk.uomcse.fs.model.ResultList;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.service.QueryService;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Map;

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
        return 5;
    }

    @Override
    public int getRowCount() {
        int length = 0;
        if (queryService != null) {
            length = queryService.getResults().size();
        }
        return length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        ArrayList<Node> keys = new ArrayList<>(queryService.getResults().keySet());
        Map<Node, ResultList> resultMap = queryService.getResults();
        switch (column) {
            case 0:
                return keys.get(row).getIp();
            case 1:
                return keys.get(row).getPort();
            case 2:
                return String.join(", ", resultMap.get(keys.get(row))).replace('_', ' ');
            case 3:
                return resultMap.get(keys.get(row)).getLatency();
            case 4:
                return resultMap.get(keys.get(row)).getHops();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "IP";
            case 1:
                return "Port";
            case 2:
                return "File Names";
            case 3:
                return "Latency(Milliseconds)";
            case 4:
                return "Hops";
            default:
                return null;
        }
    }
}
