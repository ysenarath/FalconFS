package lk.uomcse.fs.com;

import lk.uomcse.fs.messages.IMessage;

public abstract class Receiver extends Thread {
    protected boolean running;

    /**
     * Constructor
     */
    Receiver() {
        this.running = false;
    }

    /**
     * Takes Messages received from the queue
     *
     * @return received message
     */
    public abstract IMessage receive() throws InterruptedException;

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
