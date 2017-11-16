package lk.uomcse.fs.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private final AtomicInteger received;

    private final AtomicInteger resolved;

    public Statistics() {
        this.received = new AtomicInteger(0);
        this.resolved = new AtomicInteger(0);
    }

    public int getReceived() {
        return received.get();
    }

    public int getResolved() {
        return resolved.get();
    }

    public int getForwarded() {
        return received.get() - resolved.get();
    }

    public synchronized void reset() {
        resolved.set(0);
        received.set(0);
    }

    public void addReceived(int delta) {
        received.addAndGet(delta);
    }

    public void addResolved(int delta) {
        resolved.addAndGet(delta);
    }
}
