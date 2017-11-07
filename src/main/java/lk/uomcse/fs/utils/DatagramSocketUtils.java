package lk.uomcse.fs.utils;

import lk.uomcse.fs.FalconFS;
import org.apache.log4j.Logger;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DatagramSocketUtils {
    private final static Logger LOGGER = Logger.getLogger(DatagramSocketUtils.class.getName());

    private static final ConcurrentMap<Integer, DatagramSocket> sockets = new ConcurrentHashMap<>();

    public static synchronized DatagramSocket getSocket(int port) throws SocketException {
        if (!sockets.containsKey(port)) {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(port);
            } catch (SocketException e) {
                throw new SocketException("There is an error creating or accessing a Socket.");
            }
            LOGGER.debug(String.format("Socket with port %s created.", socket.getLocalPort()));
            sockets.put(port, socket);
        }
        return sockets.get(port);
    }
}

