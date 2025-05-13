package com.example.minisocial.api;

import com.example.minisocial.DTO.GroupCreateDTO;
import com.example.minisocial.DTO.GroupPostDTO;
import com.example.minisocial.model.Group;
import com.example.minisocial.model.GroupPost;
import com.example.minisocial.model.User;
import com.example.minisocial.service.GroupManagement.GroupService;
import com.example.minisocial.util.JwtUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "jwt")
@Tag(name = "Group Management", description = "APIs for creating and managing groups, group memberships, and group posts.")
public class GroupResource {

    @Inject
    private GroupService groupService;

    @Inject
    private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager em;

    private User getUserFromToken(HttpHeaders headers) {
        String token = extractToken(headers);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new WebApplicationException("Invalid or missing token", Response.Status.UNAUTHORIZED);
        }
        Long userId = jwtUtil.getIdFromToken(token);
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.UNAUTHORIZED);
        }
        return user;
    }

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring("Bearer ".length());
        }
        return null;
    }

    @POST
    @RolesAllowed({"USER", "ADMIN"})
    @Path("/")
    @Operation(summary = "Create a new group")
    @APIResponse(responseCode = "200", description = "Group created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Group.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response createGroup(@Context HttpHeaders headers, GroupCreateDTO dto) {
        User user = getUserFromToken(headers);
        Group group = groupService.createGroup(user, dto);
        return Response.ok(group).build();
    }

    @POST
    @Path("/{groupId}/promote/{userId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Promote user to admin")
    @APIResponse(responseCode = "200", description = "User promoted")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response promoteToAdmin(@Context HttpHeaders headers,
                                   @PathParam("groupId") Long groupId,
                                   @PathParam("userId") Long userId) {
        User user = getUserFromToken(headers);
        groupService.promoteToAdmin(user, groupId, userId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/posts/{postId}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Delete a group post")
    @APIResponse(responseCode = "204", description = "Post deleted")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response removeGroupPost(@Context HttpHeaders headers,
                                    @PathParam("postId") Long postId) {
        User user = getUserFromToken(headers);
        groupService.removeGroupPost(user, postId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{groupId}/users/{userId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Remove user from group")
    @APIResponse(responseCode = "204", description = "User removed from group")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response removeUserFromGroup(@Context HttpHeaders headers,
                                        @PathParam("groupId") Long groupId,
                                        @PathParam("userId") Long userId) {
        User user = getUserFromToken(headers);
        groupService.removeUserFromGroup(user, groupId, userId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{groupId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Delete group")
    @APIResponse(responseCode = "204", description = "Group deleted")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response deleteGroup(@Context HttpHeaders headers,
                                @PathParam("groupId") Long groupId) {
        User user = getUserFromToken(headers);
        groupService.deleteGroup(user, groupId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{groupId}/join")
    @RolesAllowed({"USER"})
    @Operation(summary = "Request to join group")
    @APIResponse(responseCode = "200", description = "Join request sent")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response requestToJoinGroup(@Context HttpHeaders headers,
                                       @PathParam("groupId") Long groupId) {
        User user = getUserFromToken(headers);
        groupService.requestToJoinGroup(user, groupId);
        return Response.ok().build();
    }

    @POST
    @Path("/{groupId}/approve/{userId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Approve join request")
    @APIResponse(responseCode = "200", description = "Request approved")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response approveJoinRequest(@Context HttpHeaders headers,
                                       @PathParam("groupId") Long groupId,
                                       @PathParam("userId") Long userId) {
        User user = getUserFromToken(headers);
        groupService.approveJoinRequest(user, groupId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{groupId}/reject/{userId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Reject join request")
    @APIResponse(responseCode = "200", description = "Request rejected")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response rejectJoinRequest(@Context HttpHeaders headers,
                                      @PathParam("groupId") Long groupId,
                                      @PathParam("userId") Long userId) {
        User user = getUserFromToken(headers);
        groupService.rejectJoinRequest(user, groupId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{groupId}/posts")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Create group post")
    @APIResponse(responseCode = "200", description = "Post created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GroupPost.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    public Response createGroupPost(@Context HttpHeaders headers,
                                    @PathParam("groupId") Long groupId,
                                    GroupPostDTO postDTO) {
        User user = getUserFromToken(headers);
        GroupPost post = groupService.createGroupPost(user, groupId, postDTO);
        return Response.ok(post).build();
    }

//    @GET
//    @Path("/{groupId}/posts")
//    @Operation(summary = "Get group posts")
//    @APIResponse(responseCode = "200", description = "Posts retrieved",
//            content = @Content(mediaType = MediaType.APPLICATION_JSON,
//                    schema = @Schema(implementation = GroupPost.class)))
//    public Response getGroupPosts(@PathParam("groupId") Long groupId) {
//        List<GroupPost> posts = groupService.getGroupPosts(groupId);
//        return Response.ok(posts).build();
//    }
}
