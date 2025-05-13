package com.example.minisocial.api;

import com.example.minisocial.DTO.GroupCreateDTO;
import com.example.minisocial.DTO.PostDTO;
import com.example.minisocial.model.Group;
import com.example.minisocial.service.GroupManagement.GroupService;
import com.example.minisocial.util.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Groups", description = "Operations related to groups, such as creating, joining, posting and managing roles.")
public class GroupResource {

    @Inject
    private GroupService groupService;

    @Inject
    private JwtUtil jwtUtil;

    @POST
    @Operation(summary = "Create a new group", description = "Allows any user to create a new group. The creator becomes the group admin.")
    public Response createGroup(@HeaderParam("Authorization") String authHeader, GroupCreateDTO dto) {
        String token = authHeader.replace("Bearer ", "");
        Group group = groupService.createGroup(token, dto.getName(), dto.getDescription(), dto.isOpen());
        return Response.status(Response.Status.CREATED).entity(group).build();
    }

    @POST
    @Path("/{groupId}/join")
    @Operation(summary = "Request to join a group", description = "Join directly if open, otherwise pending approval.")
    public Response requestToJoin(@HeaderParam("Authorization") String authHeader, @PathParam("groupId") Long groupId) {
        String token = authHeader.replace("Bearer ", "");
        groupService.requestToJoinGroup(token, groupId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{groupId}/leave")
    @Operation(summary = "Leave a group", description = "Allows a user to leave a group.")
    public Response leaveGroup(@HeaderParam("Authorization") String authHeader, @PathParam("groupId") Long groupId) {
        String token = authHeader.replace("Bearer ", "");
        groupService.leaveGroup(token, groupId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/approve/{membershipId}")
    @Operation(summary = "Approve membership request", description = "Group admin can approve pending requests.")
    public Response approve(@HeaderParam("Authorization") String authHeader, @PathParam("membershipId") Long membershipId) {
        String token = authHeader.replace("Bearer ", "");
        groupService.approveMembership(token, membershipId);
        return Response.ok().build();
    }

    @POST
    @Path("/{groupId}/posts")
    @Operation(summary = "Post in a group", description = "Allows approved members to post inside a group.")
    public Response post(@HeaderParam("Authorization") String authHeader, @PathParam("groupId") Long groupId, PostDTO dto) {
        String token = authHeader.replace("Bearer ", "");
        groupService.postInGroup(token, groupId, dto.getContent());
        return Response.status(Response.Status.CREATED).build();
    }
}
