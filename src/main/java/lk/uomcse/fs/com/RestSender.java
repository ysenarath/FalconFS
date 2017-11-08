package lk.uomcse.fs.com;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.IMessage;
import lk.uomcse.fs.messages.JoinRequest;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestSender extends Sender {

    private Client client ;

    public RestSender(Node node){

        ClientConfig config = new ClientConfig();
        config.property(ClientProperties.PROXY_URI, String.format("%s:%d", node.getIp(), node.getPort()));
        client = ClientBuilder.newBuilder().withConfig(config).build();
    }

    @Override
    public void send(String ip, int port, IMessage request) {

        WebTarget webTarget = client
                .target(String.format("http://%s:%d", ip, port))
                .path(String.format("/%s", request.getID()));

        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("The request sending failed with status %s.", response.getStatusInfo()));
        }

    }
}
