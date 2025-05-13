package com.example.minisocial.api;

import com.example.minisocial.model.User;
import com.example.minisocial.service.UserManagement.UserService;
import com.example.minisocial.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
        securitySchemeName = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Tag(name = "User Management", description = "APIs for managing user accounts including registration, login, and profile updates")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    JwtUtil jwtUtil;

    @POST
    @Path("/register")
    @PermitAll
    @Operation(summary = "Register a new user")
    @APIResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = User.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Email already registered"
    )
    public Response register(User user) {
        User registered = userService.register(user);
        return Response.ok(registered).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Login and get JWT token")
    @APIResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(mediaType = MediaType.TEXT_PLAIN,
                    schema = @Schema(implementation = String.class))
    )
    @APIResponse(
            responseCode = "401",
            description = "Invalid credentials"
    )
    public Response login(User credentials) {
        String token = userService.login(credentials.getEmail(), credentials.getHashedPassword());
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        return Response.ok(token).build();
    }

    @GET
    @Path("/profile")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Get current user profile")
    @SecurityRequirement(name = "jwt")
    @APIResponse(
            responseCode = "200",
            description = "User profile retrieved",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = User.class))
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    public Response getProfile(@Context HttpHeaders headers) {
        String token = extractToken(headers);
        if (token == null || !jwtUtil.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid or missing token").build();
        }

        Long userId = jwtUtil.getIdFromToken(token);
        User user = userService.findById(userId);
        return Response.ok(user).build();
    }

    @PUT
    @Path("/profile")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Update user profile")
    @SecurityRequirement(name = "jwt")
    @APIResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = User.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid update data"
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    public Response updateProfile(@Context HttpHeaders headers, User updatedUser) {
        String token = extractToken(headers);
        if (token == null || !jwtUtil.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid or missing token").build();
        }

        Long userId = jwtUtil.getIdFromToken(token);
        User updated = userService.updateProfile(userId, updatedUser);
        return Response.ok(updated).build();
    }

    @GET
    @RolesAllowed("ADMIN")
    @Operation(summary = "Get all users (Admin only)")
    @SecurityRequirement(name = "jwt")
    @APIResponse(
            responseCode = "200",
            description = "List of all users",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = User.class, type = SchemaType.ARRAY))
    )
    @APIResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @APIResponse(
            responseCode = "403",
            description = "Forbidden - Admin only"
    )
    public Response getAllUsers() {
        return Response.ok(userService.findAll()).build();
    }

    // ========== Utility Method ==========

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring("Bearer ".length()).trim();
        }
        return null;
    }
}
