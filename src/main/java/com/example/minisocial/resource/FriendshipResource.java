package com.example.minisocial.resource;

import com.example.minisocial.model.Friendship;
import com.example.minisocial.model.User;
import com.example.minisocial.service.FriendshipService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/friendships")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FriendshipResource {

    @Inject
    private FriendshipService service;

    /* -------- POST /friendships/request -------- */
    public static final class FriendRequestDTO {
        public Long senderId;
        public Long receiverId;
    }

    @POST
    @Path("/request")
    public Response requestFriendship(FriendRequestDTO dto) {
        if (dto == null || dto.senderId == null || dto.receiverId == null)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("senderId and receiverId are required").build();

        Friendship fr = service.sendRequest(dto.senderId, dto.receiverId);
        return fr == null
                ? Response.status(Response.Status.CONFLICT)
                .entity("Friendship already exists or invalid users").build()
                : Response.status(Response.Status.CREATED).entity(fr).build();
    }

    /* -------- PUT /friendships/{id}/accept -------- */
    @PUT
    @Path("/{id}/accept")
    public Response accept(@PathParam("id") Long id) {
        Friendship fr = service.accept(id);
        return fr == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(fr).build();
    }

    /* -------- DELETE /friendships/{id}  (reject) -------- */
    @DELETE
    @Path("/{id}")
    public Response reject(@PathParam("id") Long id) {
        return service.reject(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    /* -------- GET /friendships/{userId}/friends -------- */
    @GET
    @Path("/{userId}/friends")
    public Response listFriends(@PathParam("userId") Long userId) {
        List<User> friends = service.listFriends(userId);
        return Response.ok(friends).build();
    }

    /* -------- GET /friendships/{userId}/pending -------- */
    @GET
    @Path("/{userId}/pending")
    public Response pending(@PathParam("userId") Long userId) {
        List<Friendship> pending = service.pendingRequests(userId);
        return Response.ok(pending).build();
    }
}
