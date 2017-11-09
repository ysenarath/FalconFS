package lk.uomcse.fs.model.com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.uomcse.fs.model.FalconFS;
import lk.uomcse.fs.model.messages.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

@Path("/")
public class RestService extends Receiver {

    private final static Logger LOGGER = Logger.getLogger(RestService.class.getName());

    private final ConcurrentMap<String, BlockingQueue<IMessage>> handle;

    RestService(ConcurrentMap<String, BlockingQueue<IMessage>> handle) {
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
        collectInfo(joinRequest);
        return Response.status(200).entity("join request received").build();
    }

    @POST
    @Path(JoinResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response joinResponse(JoinResponse joinResponse) {
        collectInfo(joinResponse);
        return Response.status(200).entity("join response received").build();
    }

    @POST
    @Path(SearchRequest.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchRequest(SearchRequest searchRequest) {
        collectInfo(searchRequest);
        return Response.status(200).entity("search request received").build();
    }

    @POST
    @Path(SearchResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchResponse(String response) {
        ObjectMapper ob = new ObjectMapper();
        SearchResponse searchResponse;
        try {
            searchResponse = ob.readValue(response, SearchResponse.class);
            collectInfo(searchResponse);
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
    public Response leaveRequest(LeaveRequest leaveRequest) {
        collectInfo(leaveRequest);
        return Response.status(200).entity("leave request received").build();
    }

    //    TODO Test Leave request/response
    @POST
    @Path(LeaveResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response leaveResponse(LeaveResponse leaveResponse) {
        collectInfo(leaveResponse);
        return Response.status(200).entity("leave response received").build();
    }

    @POST
    @Path(HeartbeatPulse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response heartbeatPulse(HeartbeatPulse heartbeatPulse) {
        collectInfo(heartbeatPulse);
        return Response.status(200).entity("heartbeat pulse received").build();
    }


    private void collectInfo(IMessage message) {

        ObjectMapper ob = new ObjectMapper();

        try {
            LOGGER.info(ob.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        handle.putIfAbsent(message.getID(), new LinkedBlockingQueue<>());
        BlockingQueue<IMessage> messages = handle.get(message.getID());
        messages.add(message);


        try {
            LOGGER.info(ob.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
