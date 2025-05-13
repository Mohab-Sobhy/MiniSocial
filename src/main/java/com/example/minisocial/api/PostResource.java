package com.example.minisocial.api;

import com.example.minisocial.model.Post;
import com.example.minisocial.model.PostComment;
import com.example.minisocial.model.PostLike;
import com.example.minisocial.service.PostManagement.PostService;
import com.example.minisocial.util.JwtUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Tag(name = "Post Management", description = "APIs for creating, retrieving, and managing posts.")
@SecurityScheme(
        securitySchemeName = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "jwt")
public class PostResource {

    @Inject
    private PostService postService;

    @Inject
    private JwtUtil jwtUtil;

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new WebApplicationException("Authorization header must be provided", Response.Status.UNAUTHORIZED);
        }

        String token = authHeader.substring("Bearer ".length());

        if (!jwtUtil.validateToken(token)) {
            throw new WebApplicationException("Invalid or expired token", Response.Status.UNAUTHORIZED);
        }

        try {
            return jwtUtil.getIdFromToken(token);
        } catch (Exception e) {
            e.printStackTrace(); // أو استخدم Logger
            throw new WebApplicationException("Failed to extract userId from token", Response.Status.UNAUTHORIZED);
        }
    }


    @POST
    @Operation(summary = "Create a new post", description = "Creates a new post for the authenticated user.")
    public Response createPost(@HeaderParam("Authorization") String authHeader, Post postData) {
        Long userId = extractUserId(authHeader);
        Post post = postService.createPost(userId, postData.getContent(), postData.getImageUrl(), postData.getLinkUrl());
        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    @Path("/feed")
    @Operation(summary = "Get user feed", description = "Retrieves the timeline feed for the authenticated user.")
    public List<Post> getFeed(@HeaderParam("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return postService.getTimeline(userId);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a post", description = "Updates an existing post if the authenticated user is the owner.")
    public Response updatePost(@HeaderParam("Authorization") String authHeader, @PathParam("id") Long postId, Post updatedPost) {
        Long userId = extractUserId(authHeader);
        Post post = postService.updatePost(postId, userId, updatedPost.getContent(), updatedPost.getImageUrl(), updatedPost.getLinkUrl());
        return Response.ok(post).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a post", description = "Deletes a post by ID if the authenticated user is the owner.")
    public Response deletePost(@HeaderParam("Authorization") String authHeader, @PathParam("id") Long postId) {
        Long userId = extractUserId(authHeader);
        postService.deletePost(postId, userId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/like")
    @Operation(summary = "Like a post", description = "Adds a like to the specified post by the authenticated user.")
    public Response like(@HeaderParam("Authorization") String authHeader, @PathParam("id") Long postId) {
        Long userId = extractUserId(authHeader);
        PostLike like = postService.likePost(postId, userId);
        return Response.status(Response.Status.CREATED).entity(like).build();
    }

    @POST
    @Path("/{id}/comment")
    @Operation(summary = "Comment on a post", description = "Adds a comment to the specified post by the authenticated user.")
    public Response comment(@HeaderParam("Authorization") String authHeader, @PathParam("id") Long postId, PostComment comment) {
        Long userId = extractUserId(authHeader);
        PostComment savedComment = postService.commentPost(postId, userId, comment.getText());
        return Response.status(Response.Status.CREATED).entity(savedComment).build();
    }
}
