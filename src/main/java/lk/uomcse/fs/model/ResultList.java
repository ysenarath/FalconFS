package lk.uomcse.fs.model;


import java.util.ArrayList;
import java.util.List;

public class ResultList extends ArrayList<String> implements List<String> {
    private final List<String> filenames;

    private Long latency;

    private int hops;

    public ResultList(List<String> filenames) {
        this.filenames = filenames;
    }

    public Long getLatency() {
        return latency;
    }

    public void setLatency(Long latency) {
        this.latency = latency;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }
}
