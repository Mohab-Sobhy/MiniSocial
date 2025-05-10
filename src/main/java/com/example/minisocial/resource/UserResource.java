package com.example.minisocial.resource;

import com.example.minisocial.model.User;
import com.example.minisocial.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    /* ----------  POST /users/register  ---------- */
    @POST
    @Path("/register")
    public Response register(User user) {
        User created = userService.registerUser(
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getBio(),
                user.getRole()
        );
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /* ----------  POST /users/login  ---------- */
    @POST
    @Path("/login")
    public Response login(User credentials) {
        User found = userService.loginUser(
                credentials.getEmail(),
                credentials.getPassword()
        );
        return found == null
                ? Response.status(Response.Status.UNAUTHORIZED)
                .entity("Invalid email or password").build()
                : Response.ok(found).build();
    }

    /* ----------  GET /users/{id}  ---------- */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        User user = userService.findUserById(id);
        return user == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(user).build();
    }

    /* ----------  PUT /users/{id}  ---------- */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, User updates) {
        User updated = userService.updateUserProfile(
                id,
                updates.getName(),
                updates.getBio(),
                updates.getPassword(),
                updates.getEmail()
        );
        return updated == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(updated).build();
    }

    /* ----------  DELETE /users/{id}  ---------- */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return userService.deleteUser(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
