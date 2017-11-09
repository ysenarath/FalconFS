package lk.uomcse.fs.model.com;

import lk.uomcse.fs.model.messages.IMessage;

public abstract class Sender extends Thread {
    protected boolean running;

    Sender() {
        this.running = false;
    }

    public abstract void send(String ip, int port, IMessage request);

    /**
     * Sets run status and interrupt current activities
     *
     * @param running value
     */
    public void setRunning(boolean running) {
        this.running = running;
        this.interrupt();
    }

    /**
     * Returns whether the Receiver instance is running
     *
     * @return whether the receiver is running or not
     */
    public boolean isRunning() {
        return running;
    }
}
