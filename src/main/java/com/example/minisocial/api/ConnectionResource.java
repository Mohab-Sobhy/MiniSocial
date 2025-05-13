package com.example.minisocial.api;

import com.example.minisocial.DTO.FriendRequestDTO;
import com.example.minisocial.DTO.UserDTO;
import com.example.minisocial.service.ConnectionManagement.ConnectionService;
import com.example.minisocial.util.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/connections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
        securitySchemeName = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "jwt")
@Tag(name = "Connection Management", description = "APIs for managing friend requests and connections")
public class ConnectionResource {

    @Inject
    ConnectionService connectionService;

    @Inject
    JwtUtil jwtUtil;

    @POST
    @Path("/request")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response sendFriendRequest(String receiverIdStr, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            Long receiverId = Long.parseLong(receiverIdStr.trim());
            connectionService.sendFriendRequest(userId, receiverId);
            return Response.status(Response.Status.CREATED).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid receiver ID").build();
        }
    }

    @GET
    @Path("/pending")
    public Response getPendingRequests(@Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<FriendRequestDTO> requests = connectionService.getPendingRequests(userId);
        return Response.ok(requests).build();
    }

    @PUT
    @Path("/{requestId}/accept")
    public Response accept(@PathParam("requestId") Long requestId, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        connectionService.acceptFriendRequest(userId, requestId);
        return Response.ok().build();
    }

    @PUT
    @Path("/{requestId}/reject")
    public Response reject(@PathParam("requestId") Long requestId, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        connectionService.rejectFriendRequest(userId, requestId);
        return Response.ok().build();
    }

    @GET
    @Path("/friends")
    public Response getFriends(@Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<UserDTO> friends = connectionService.getFriends(userId);
        return Response.ok(friends).build();
    }

    @GET
    @Path("/search")
    public Response searchUsers(@QueryParam("q") String keyword, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null || keyword == null || keyword.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid token or query").build();
        }

        List<UserDTO> users = connectionService.searchUsers(userId, keyword);
        return Response.ok(users).build();
    }

    @GET
    @Path("/suggestions")
    public Response suggestFriends(@Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<UserDTO> suggestions = connectionService.suggestFriends(userId);
        return Response.ok(suggestions).build();
    }

    private Long extractUserId(HttpHeaders headers) {
        String token = extractToken(headers);
        if (token == null || !jwtUtil.validateToken(token)) {
            return null;
        }
        return jwtUtil.getIdFromToken(token);
    }

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
