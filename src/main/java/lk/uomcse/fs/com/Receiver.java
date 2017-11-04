package lk.uomcse.fs.com;

import lk.uomcse.fs.entity.Message;

public abstract class Receiver extends Thread {
    boolean running;

    Receiver() {
        this.running = false;
    }

    /**
     * Takes UDPMessages received from the queue
     *
     * @return received message
     */
    public abstract Message receive() throws InterruptedException;

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
