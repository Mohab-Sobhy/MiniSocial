package com.example.minisocial.api;

import com.example.minisocial.DTO.FriendRequestDTO;
import com.example.minisocial.DTO.UserDTO;
import com.example.minisocial.service.ConnectionManagement.ConnectionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/connections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Friendship Management", description = "APIs for managing friend requests and connections")
public class FriendshipResource {

    @Inject
    ConnectionService connectionService;

    @POST
    @Path("/request")
    @Operation(
            summary = "Send friend request",
            description = "Sends a friend request from one user to another"
    )
    @APIResponse(
            responseCode = "201",
            description = "Friend request created successfully"
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid input data"
    )
    public Response sendFriendRequest(FriendRequestDTO dto) {
        //connectionService.sendFriendRequest(dto.getSenderId(), dto.getReceiverId());
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/pending")
    @Operation(
            summary = "Get pending friend requests",
            description = "Retrieves all incoming friend requests that haven't been responded to"
    )
    @APIResponse(
            responseCode = "200",
            description = "List of pending friend requests",
            content = @Content(schema = @Schema(implementation = FriendRequestDTO[].class))
    )
    public Response getPendingRequests(@Context HttpHeaders headers) {
        String token = extractToken(headers);
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //List<FriendRequestDTO> requests = connectionService.getPendingRequests(token);
        //return Response.ok(requests).build();
        return Response.ok("done").build();
    }

    @PUT
    @Path("/{requestId}/accept")
    @Operation(
            summary = "Accept friend request",
            description = "Accepts a pending friend request"
    )
    @APIResponse(
            responseCode = "200",
            description = "Friend request accepted successfully"
    )
    @APIResponse(
            responseCode = "404",
            description = "Friend request not found"
    )
    public Response accept(@PathParam("requestId") Long requestId) {
        //connectionService.acceptFriendRequest(requestId);
        return Response.ok().build();
    }

    @PUT
    @Path("/{requestId}/reject")
    @Operation(
            summary = "Reject friend request",
            description = "Rejects a pending friend request"
    )
    @APIResponse(
            responseCode = "200",
            description = "Friend request rejected successfully"
    )
    @APIResponse(
            responseCode = "404",
            description = "Friend request not found"
    )
    public Response reject(@PathParam("requestId") Long requestId) {
        //connectionService.rejectFriendRequest(requestId);
        return Response.ok().build();
    }

    @GET
    @Path("/friends")
    @Operation(
            summary = "Get friends list",
            description = "Retrieves all accepted friends of a user"
    )
    @APIResponse(
            responseCode = "200",
            description = "List of friends",
            content = @Content(schema = @Schema(implementation = UserDTO[].class))
    )
    public Response getFriends(@Context HttpHeaders headers) {
        String token = extractToken(headers);
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<UserDTO> friends = connectionService.getFriends(token);
        return Response.ok(friends).build();
    }

    // 🔒 أداة مساعدة لاستخراج التوكن من الهيدر
    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
