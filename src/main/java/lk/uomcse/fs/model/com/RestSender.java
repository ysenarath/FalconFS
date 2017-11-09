package lk.uomcse.fs.model.com;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import com.google.common.collect.Queues;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.IMessage;
import org.apache.log4j.Logger;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.BlockingQueue;

public class RestSender extends Sender {
    private final static Logger LOGGER = Logger.getLogger(RestSender.class.getName());

    private Client client;

    private Node self;

    private final BlockingQueue<RestMessage> messages;

    public RestSender(Node self) {
        this.self = self;
        ClientConfig configuration = new ClientConfig();
        configuration.property(ClientProperties.CONNECT_TIMEOUT, 800);
        configuration.property(ClientProperties.READ_TIMEOUT, 800);

        this.client = ClientBuilder.newClient(configuration);


        messages = Queues.newLinkedBlockingDeque();
    }

    @Override
    public void run() {
        WebTarget webTarget;
        RestMessage restRequest;
        IMessage request;
        Invocation.Builder builder;

        running = true;
        while (running) {
            try {
                restRequest = messages.take();
                request = restRequest.request;
                request.setSender(self); //set my address
            } catch (InterruptedException e) {
                continue;
            }
            try {
                webTarget = client.target(String.format("http://%s:%d", restRequest.ip, restRequest.port))
                        .path(String.format("/%s", request.getID()));
                builder = webTarget.request(MediaType.APPLICATION_JSON);
                Response response;
                response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
                LOGGER.info(String.format("Sending message %s to %s:%d", request.toString(), restRequest.ip, restRequest.port));
                if (response.getStatus() != 200) {
                    LOGGER.error(String.format("Sending to %s:%d failed :: msg %s", restRequest.ip, restRequest.port, request.toString()));
                }
            } catch (Exception ex) {
                LOGGER.error(String.format("Sending to %s:%d failed :: msg %s", restRequest.ip, restRequest.port, request.toString()));
            }
        }
    }

    @Override
    public void send(String ip, int port, IMessage request) {
        messages.add(new RestMessage(ip, port, request));
    }

    private class RestMessage {
        String ip;
        int port;
        IMessage request;

        RestMessage(String ip, int port, IMessage request) {
            this.ip = ip;
            this.port = port;
            this.request = request;
        }

    }


}
