package lk.uomcse.fs.models;

import lk.uomcse.fs.udp.UDPServer;

public class FileServer extends UDPServer {
    public FileServer(int port) {
        super(port);
    }

    /**
     * Handles requests made by clients
     *
     * @param request request sent by clients
     * @return reply sent to the request made
     */
    @Override
    protected String handleRequest(String request) {
        return "NO-REPLY";
    }
}
