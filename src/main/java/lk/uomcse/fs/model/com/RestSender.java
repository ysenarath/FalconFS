package lk.uomcse.fs.model.com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        this.client = ClientBuilder.newClient();
        messages = Queues.newLinkedBlockingDeque();
    }

    @Override
    public void run() {
        WebTarget webTarget;
        RestMessage restRequest;
        IMessage request;

        running = true;
        while (running) {
            try {
                restRequest = messages.take();
                request = restRequest.request;
            } catch (InterruptedException e) {
                continue;
            }

            webTarget = client.target(String.format("http://%s:%d", restRequest.ip, restRequest.port))
                    .path(String.format("/%s", request.getID()));

            Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
            Response response;
            try {
                response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
                if (response.getStatus() != 200) {
                    throw new RuntimeException(String.format("The request sending failed with status %s.", response.getStatusInfo()));
                }
            } catch (Exception ex) {
                // TODO: Is this way of handling ok @Nadheesh
                ObjectMapper ob = new ObjectMapper();
                try {
                    LOGGER.error(String.format("Sending failed to %s message %s", webTarget.getUri().getPath(), ob.writeValueAsString(request)));
                    LOGGER.error(ex.getMessage());
                } catch (JsonProcessingException e1) {
                    LOGGER.error(e1.getMessage());
                }
            }
        }
    }

    @Override
    public void send(String ip, int port, IMessage request) {
        //set my address
        request.setSender(self);
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
