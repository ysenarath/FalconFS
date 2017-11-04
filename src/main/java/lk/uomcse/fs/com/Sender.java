package lk.uomcse.fs.com;

import lk.uomcse.fs.entity.Message;
import lk.uomcse.fs.messages.IMessage;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Sender extends Thread {
    boolean running;

    Sender() {
        this.running = false;
    }

    public void setRunning(boolean running) {
        this.running = running;
        this.interrupt();
    }


    public abstract void send(String ip, int port, IMessage request) ;

}
