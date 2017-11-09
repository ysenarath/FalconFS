package lk.uomcse.fs.model.com;

import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.IMessage;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestSender extends Sender {

    private Client client;

    private Node self;

    public RestSender(Node self) {
        this.self = self;
        this.client = ClientBuilder.newClient();
    }

    @Override
    public void send(String ip, int port, IMessage request) {

        //set my address
        request.setSender(self);

        WebTarget webTarget = client
                .target(String.format("http://%s:%d", ip, port))
                .path(String.format("/%s", request.getID()));

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response;
        try {
            response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
            if (response.getStatus() != 200) {
                throw new RuntimeException(String.format("The request sending failed with status %s.", response.getStatusInfo()));
            }
        } catch (Exception e) {
            // TODO: Is this way of handling ok @Nadheesh
            // Ignore
        }

    }

    public void stopWebSender() {
        this.client.close();
    }
}
