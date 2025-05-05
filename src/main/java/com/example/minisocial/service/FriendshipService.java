package com.example.minisocial.service;

import com.example.minisocial.model.User;
import com.example.minisocial.model.Friendship;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Stateless
public class FriendshipService {

    @PersistenceContext
    private EntityManager entityManager;
    private FriendshipService friendshipService;

    public Friendship sendFriendRequest(Long userId, Long friendId) {
        User user = entityManager.find(User.class, userId);
        User friend = entityManager.find(User.class, friendId);

        if (user != null && friend != null) {
            Friendship friendship = new Friendship();
            friendship.setUser(user);
            friendship.setFriend(friend);
            friendship.setAccepted(false);

            entityManager.persist(friendship);
            return friendship;
        }

        return null;
    }

    public Friendship acceptFriendRequest(Long friendshipId) {
        Friendship friendship = entityManager.find(Friendship.class, friendshipId);
        if (friendship != null) {
            friendship.setAccepted(true);
            entityManager.merge(friendship);
            return friendship;
        }
        return null;
    }

    public boolean rejectFriendRequest(Long friendshipId) {
        Friendship friendship = entityManager.find(Friendship.class, friendshipId);
        if (friendship != null) {
            entityManager.remove(friendship);
            return true;
        }
        return false;
    }

    // Get all friends of a user (accepted friendships)
    /*public List<User> getFriends(Long userId) {
        String query = "SELECT f.friend FROM Friendship f WHERE f.user.id = :userId AND f.accepted = true";
        TypedQuery<User> typedQuery = entityManager.createQuery(query, User.class);
        typedQuery.setParameter("userId", userId);
        return typedQuery.getResultList(); // Return the list of friends
    }*/

    public List<Friendship> getPendingFriendRequests(Long userId) {
        String query = "SELECT f FROM Friendship f WHERE f.friend.id = :userId AND f.accepted = false";
        TypedQuery<Friendship> typedQuery = entityManager.createQuery(query, Friendship.class);
        typedQuery.setParameter("userId", userId);
        return typedQuery.getResultList();
    }
    @GET
    @Path("/{userId}/friends")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriends(@PathParam("userId") Long userId) {
        // Fetch the list of friends (User objects)
        Response friends = friendshipService.getFriends(userId);

        // Return the Response with the List<User> as the entity (the response body)
        return Response.status(Response.Status.OK)  // HTTP status code 200 OK
                .entity(friends)                   // The actual data (List<User>) as the response body
                .build();                           // Build the Response object
    }

}