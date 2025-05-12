package com.example.minisocial.api;

import com.example.minisocial.model.Post;
import com.example.minisocial.model.PostComment;
import com.example.minisocial.model.PostLike;
import com.example.minisocial.service.PostManagement.PostService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class PostResource {

    @Inject
    private PostService postService;

    @POST
    public Response createPost(@QueryParam("userId") Long userId, Post postData) {
        Post post = postService.createPost(userId, postData.getContent(), postData.getImageUrl(), postData.getLinkUrl());
        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    @Path("/feed")
    public List<Post> getFeed(@QueryParam("userId") Long userId) {
        return postService.getTimeline(userId);
    }

    @PUT
    @Path("/{id}")
    public Response updatePost(@PathParam("id") Long postId, @QueryParam("userId") Long userId, Post updatedPost) {
        Post post = postService.updatePost(postId, userId, updatedPost.getContent(), updatedPost.getImageUrl(), updatedPost.getLinkUrl());
        return Response.ok(post).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") Long postId, @QueryParam("userId") Long userId) {
        postService.deletePost(postId, userId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/like")
    public Response like(@PathParam("id") Long postId, @QueryParam("userId") Long userId) {
        PostLike like = postService.likePost(postId, userId);
        return Response.status(Response.Status.CREATED).entity(like).build();
    }

    @POST
    @Path("/{id}/comment")
    public Response comment(@PathParam("id") Long postId, @QueryParam("userId") Long userId, PostComment comment) {
        PostComment savedComment = postService.commentPost(postId, userId, comment.getText());
        return Response.status(Response.Status.CREATED).entity(savedComment).build();
    }
}
