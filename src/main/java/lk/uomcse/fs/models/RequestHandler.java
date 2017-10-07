package lk.uomcse.fs.models;

import lk.uomcse.fs.udp.UDPListener;

public class RequestHandler extends Thread {
    private boolean running = true;

    private final UDPListener listener;

    public RequestHandler(int port) {
        this.listener = new UDPListener(port);
        this.listener.start();
    }

    /**
     * Thread function
     */
    @Override
    public void run() {
        while (running) {
            while (!this.listener.getPackets().isEmpty()) {

            }
        }
        this.listener.setRunning(false);
    }
}
