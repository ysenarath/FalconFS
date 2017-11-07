package lk.uomcse.fs.com;

import lk.uomcse.fs.messages.JoinRequest;
import lk.uomcse.fs.messages.JoinResponse;
import lk.uomcse.fs.messages.SearchRequest;
import lk.uomcse.fs.messages.SearchResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class RestReceiver extends Receiver {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Receiver activated!";
    }

    @POST
    @Path(JoinRequest.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response joinRequest(JoinRequest joinRequest, @Context HttpServletRequest req) {
        System.out.println(req.getRemoteHost());
        System.out.println(req.getRemoteAddr());
        System.out.println(req.getRemotePort());
        System.out.println(joinRequest.getID());
        messages.offer(joinRequest);
        return Response.status(200).entity("join request received").build();
    }

    @POST
    @Path(JoinResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response joinResponse(JoinResponse joinResponse) {
        messages.offer(joinResponse);
        return Response.status(200).entity("join response received").build();
    }

    @POST
    @Path(SearchRequest.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchRequest(SearchRequest searchRequest) {
        messages.offer(searchRequest);
        return Response.status(200).entity("search request received").build();
    }

    @POST
    @Path(SearchResponse.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchResponse(SearchRequest searchRequest) {
        messages.offer(searchRequest);
        return Response.status(200).entity("search response received").build();
    }
}
