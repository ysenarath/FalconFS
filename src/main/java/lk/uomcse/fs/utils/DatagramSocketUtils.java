package lk.uomcse.fs.utils;

import org.apache.log4j.Logger;

import java.net.DatagramSocket;
import java.net.SocketException;

public class DatagramSocketUtils {
    private final static Logger LOGGER = Logger.getLogger(DatagramSocketUtils.class.getName());

    public static synchronized DatagramSocket getSocket(int port) throws SocketException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            LOGGER.debug(String.format("Socket with port %s created.", socket.getLocalPort()));
            return socket;
        } catch (SocketException e) {
            throw new SocketException("There is an error creating or accessing a Socket.");
        }
    }
}

