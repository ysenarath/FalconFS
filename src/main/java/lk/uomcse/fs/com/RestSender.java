package lk.uomcse.fs.com;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.IMessage;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestSender extends Sender {

    private Client client;

    private Node self;

    public RestSender(Node self){
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
        Response response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("The request sending failed with status %s.", response.getStatusInfo()));
        }

    }

    public void stopWebSender(){
        this.client.close();
    }
}
