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
    @Operation(
            summary = "Send friend request",
            description = "Sends a friend request from one user to another"
    )
    @APIResponse(responseCode = "201", description = "Friend request created successfully")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response sendFriendRequest(FriendRequestDTO dto, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //connectionService.sendFriendRequest(userId, dto.getReceiverId());
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/pending")
    @Operation(summary = "Get pending friend requests", description = "Retrieves all incoming friend requests that haven't been responded to")
    @APIResponse(
            responseCode = "200",
            description = "List of pending friend requests",
            content = @Content(schema = @Schema(implementation = FriendRequestDTO[].class))
    )
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response getPendingRequests(@Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //List<FriendRequestDTO> requests = connectionService.getPendingRequests(userId);
        return Response.ok("").build();
    }

    @PUT
    @Path("/{requestId}/accept")
    @Operation(summary = "Accept friend request", description = "Accepts a pending friend request")
    @APIResponse(responseCode = "200", description = "Friend request accepted successfully")
    @APIResponse(responseCode = "404", description = "Friend request not found")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response accept(@PathParam("requestId") Long requestId, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //connectionService.acceptFriendRequest(userId, requestId);
        return Response.ok().build();
    }

    @PUT
    @Path("/{requestId}/reject")
    @Operation(summary = "Reject friend request", description = "Rejects a pending friend request")
    @APIResponse(responseCode = "200", description = "Friend request rejected successfully")
    @APIResponse(responseCode = "404", description = "Friend request not found")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response reject(@PathParam("requestId") Long requestId, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //connectionService.rejectFriendRequest(userId, requestId);
        return Response.ok().build();
    }

    @GET
    @Path("/friends")
    @Operation(summary = "Get friends list", description = "Retrieves all accepted friends of a user")
    @APIResponse(
            responseCode = "200",
            description = "List of friends",
            content = @Content(schema = @Schema(implementation = UserDTO[].class))
    )
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response getFriends(@Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //List<UserDTO> friends = connectionService.getFriends(userId);
        return Response.ok("").build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search users", description = "Search users by name or email")
    @APIResponse(
            responseCode = "200",
            description = "List of matching users",
            content = @Content(schema = @Schema(implementation = UserDTO[].class))
    )
    @APIResponse(responseCode = "400", description = "Invalid token or query")
    public Response searchUsers(@QueryParam("q") String keyword, @Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null || keyword == null || keyword.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid token or query").build();
        }

        //List<UserDTO> users = connectionService.searchUsers(userId, keyword);
        return Response.ok("").build();
    }

    @GET
    @Path("/suggestions")
    @Operation(summary = "Suggest friends", description = "Suggest friends based on mutual connections")
    @APIResponse(
            responseCode = "200",
            description = "List of suggested users",
            content = @Content(schema = @Schema(implementation = UserDTO[].class))
    )
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response suggestFriends(@Context HttpHeaders headers) {
        Long userId = extractUserId(headers);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //List<UserDTO> suggestions = connectionService.suggestFriends(userId);
        return Response.ok("").build();
    }

    // 🔒 أداة مساعدة لاستخراج userId من JWT
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
