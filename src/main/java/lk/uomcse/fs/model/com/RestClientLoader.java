package lk.uomcse.fs.model.com;


import lk.uomcse.fs.model.messages.IMessage;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClientLoader implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(RestClientLoader.class.getName());

    private Client client;

    private String ip;

    private int port;

    private IMessage request;

    RestClientLoader(String ip, int port, IMessage request) {
        this.ip = ip;
        this.port = port;
        this.request = request;

        ClientConfig configuration = new ClientConfig();
        configuration.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        configuration.property(ClientProperties.READ_TIMEOUT, 1000);

        this.client = ClientBuilder.newClient(configuration);

    }

    @Override
    public void run() {
        WebTarget webTarget;
        Invocation.Builder builder;
        Response response;

        webTarget = client.target(String.format("http://%s:%d", ip, port))
                .path(String.format("/%s", request.getID()));
        builder = webTarget.request(MediaType.APPLICATION_JSON);
        try {
            response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
            LOGGER.info(String.format("Sending message %s to %s:%d", request.toString(), ip, port));
            if (response.getStatus() != 200) {
                LOGGER.error(String.format("Sending to %s:%d failed :: msg %s", ip, port, request.toString()));
            }
        }catch (Exception ex){
            LOGGER.error(String.format("Sending to %s:%d failed :: msg %s", ip, port, request.toString()));

        }

    }
}
