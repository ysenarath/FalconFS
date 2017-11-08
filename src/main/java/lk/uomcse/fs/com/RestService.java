package lk.uomcse.fs.com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

@Path("/")
public class RestService extends Receiver {

    private final ConcurrentMap<String, BlockingQueue<IMessage>> handle;

    public RestService(ConcurrentMap<String, BlockingQueue<IMessage>> handle) {
        this.handle = handle;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        System.out.println("came here");
        return "Receiver activated!";
    }

    @POST
    @Path(JoinRequest.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response joinRequest(JoinRequest joinRequest, @Context HttpServletRequest req) {
        collectInfo(joinRequest, req);
        return Response.status(200).entity("join request received").build();
    }

    @POST
    @Path(JoinResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response joinResponse(JoinResponse joinResponse, @Context HttpServletRequest req) {
        collectInfo(joinResponse, req);
        return Response.status(200).entity("join response received").build();
    }

    @POST
    @Path(SearchRequest.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchRequest(SearchRequest searchRequest, @Context HttpServletRequest req) {
        collectInfo(searchRequest, req);
        return Response.status(200).entity("search request received").build();
    }

    @POST
    @Path(SearchResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchResponse(String response, @Context HttpServletRequest req) {
        ObjectMapper ob = new ObjectMapper();
        SearchResponse searchResponse;
        try {
            searchResponse = ob.readValue(response, SearchResponse.class);
            collectInfo(searchResponse, req);
            return Response.status(200).entity("search response received").build();

        } catch (IOException e) {
//            TODO handle error
            e.printStackTrace();
            return Response.status(400).entity("Can not parse response").build();
        }
    }

    @POST
    @Path(LeaveRequest.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response leaveRequest(LeaveRequest leaveRequest, @Context HttpServletRequest req) {
        collectInfo(leaveRequest, req);
        return Response.status(200).entity("leave request received").build();
    }

    //    TODO Test Leave request/response
    @POST
    @Path(LeaveResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response leaveResponse(LeaveResponse leaveResponse, @Context HttpServletRequest req) {
        collectInfo(leaveResponse, req);
        return Response.status(200).entity("leave response received").build();
    }

    @POST
    @Path(HeartbeatPulse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response heartbeatPulse(HeartbeatPulse heartbeatPulse, @Context HttpServletRequest req) {
        collectInfo(heartbeatPulse, req);
        return Response.status(200).entity("heartbeat pulse received").build();
    }


    private void collectInfo(IMessage message, HttpServletRequest req) {
        message.setReceivedTime(System.currentTimeMillis());

        handle.putIfAbsent(message.getID(), new LinkedBlockingQueue<>());
        BlockingQueue<IMessage> messages = handle.get(message.getID());
        messages.add(message);

        System.out.println(message.getSender().toString());
        ObjectMapper ob = new ObjectMapper();
        try {
            System.out.println(ob.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
