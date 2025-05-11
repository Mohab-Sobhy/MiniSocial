package com.example.minisocial.resource;

import com.example.minisocial.model.*;
import com.example.minisocial.service.PostService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    public static final class NewPost {
        public Long authorId;
        public String content;
        public String imageUrl;
        public String linkUrl;
    }
    public static final class EditPost {
        public String content;
        public String imageUrl;
        public String linkUrl;
    }
    public static final class CommentDTO {
        public Long authorId;
        public String text;
    }

    @Inject
    private PostService service;

    @POST
    public Response create(NewPost dto) {
        if (dto == null || dto.authorId == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        Post p = service.create(dto.authorId, dto.content, dto.imageUrl, dto.linkUrl);
        return p == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.status(Response.Status.CREATED).entity(p).build();
    }

    @GET
    @Path("/feed/{userId}")
    public Response feed(@PathParam("userId") Long userId,
                         @QueryParam("max") @DefaultValue("50") int max) {
        List<Post> posts = service.timeline(userId, max);
        return Response.ok(posts).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Post p = service.find(id);
        return p == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(p).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, EditPost dto) {
        Post p = service.update(id, dto.content, dto.imageUrl, dto.linkUrl);
        return p == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(p).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/like")
    public Response like(@PathParam("id") Long postId,
                         @QueryParam("userId") Long userId) {
        return service.toggleLike(postId, userId)
                ? Response.ok().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/comments")
    public Response comment(@PathParam("id") Long postId, CommentDTO dto) {
        PostComment c = service.addComment(postId, dto.authorId, dto.text);
        return c == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.status(Response.Status.CREATED).entity(c).build();
    }

    @DELETE
    @Path("/comments/{commentId}")
    public Response deleteComment(@PathParam("commentId") Long commentId,
                                  @QueryParam("requesterId") Long requesterId) {
        return service.deleteComment(commentId, requesterId)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
