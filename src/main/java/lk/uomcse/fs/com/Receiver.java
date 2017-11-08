package lk.uomcse.fs.com;

import com.google.common.collect.Queues;
import lk.uomcse.fs.messages.IMessage;

import java.util.concurrent.BlockingQueue;

public abstract class Receiver extends Thread {
    boolean running;

    /**
     * Constructor
     */
    Receiver() {
        this.running = false;
    }

    /**
     * Returns whether the Receiver instance is running
     *
     * @return whether the receiver is running or not
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets run status and interrupt current activities
     *
     * @param running value
     */
    public void setRunning(boolean running) {
        this.running = running;
        this.interrupt();
    }

}
