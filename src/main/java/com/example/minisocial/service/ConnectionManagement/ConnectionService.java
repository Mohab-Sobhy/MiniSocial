package com.example.minisocial.service.ConnectionManagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.example.minisocial.model.User;
import com.example.minisocial.model.Friendship;
import com.example.minisocial.DTO.UserDTO;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConnectionService {

    @PersistenceContext
    EntityManager em;

    private static final String SECRET_KEY = "yourSecretKey"; // Replace with your actual secret

    // 🔓 استخراج Claims من JWT
    private Claims decodeJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(jwt)
                .getBody();
    }

    private User getUserFromToken(String token) {
        String email = decodeJWT(token).getSubject(); // هنا الـ sub هو الإيميل
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public List<UserDTO> getFriends(String token) {
        User user = getUserFromToken(token);

        return em.createQuery("SELECT f.friend FROM Friendship f WHERE f.user.id = :uid AND f.status = :status", User.class)
                .setParameter("uid", user.getId())
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList()
                .stream()
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setBio(u.getBio());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Search users by name or email (excluding current user and existing friends)
    public List<UserDTO> searchUsers(String token, String keyword) {
        User currentUser = getUserFromToken(token);

        // Get all friends' ids
        List<Long> friendIds = em.createQuery(
                        "SELECT f.friend.id FROM Friendship f WHERE f.user.id = :uid AND f.status = :status", Long.class)
                .setParameter("uid", currentUser.getId())
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        // Add current user id to the exclusion list
        friendIds.add(currentUser.getId());

        // Search
        return em.createQuery(
                        "SELECT u FROM User u WHERE (LOWER(u.name) LIKE :kw OR LOWER(u.email) LIKE :kw) AND u.id NOT IN :excludedIds", User.class)
                .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                .setParameter("excludedIds", friendIds)
                .getResultList()
                .stream()
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setBio(u.getBio());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Suggest friends based on mutual connections
    public List<UserDTO> suggestFriends(String token) {
        User user = getUserFromToken(token);

        // Get user's friends
        List<Long> myFriendIds = em.createQuery(
                        "SELECT f.friend.id FROM Friendship f WHERE f.user.id = :uid AND f.status = :status", Long.class)
                .setParameter("uid", user.getId())
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        if (myFriendIds.isEmpty()) return List.of();

        // Get friends of friends not already friends or the user themselves
        List<Object[]> suggestions = em.createQuery(
                        "SELECT f.friend.id, COUNT(f.friend.id) as mutualCount " +
                                "FROM Friendship f " +
                                "WHERE f.user.id IN :myFriends " +
                                "AND f.friend.id NOT IN :excluded " +
                                "AND f.status = :status " +
                                "GROUP BY f.friend.id " +
                                "ORDER BY mutualCount DESC", Object[].class)
                .setParameter("myFriends", myFriendIds)
                .setParameter("excluded", myFriendIds.stream().collect(Collectors.toSet()).stream()
                        .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                            list.add(user.getId());
                            return list;
                        })))
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        if (suggestions.isEmpty()) return List.of();

        // Fetch users by IDs
        List<Long> suggestedUserIds = suggestions.stream()
                .map(o -> (Long) o[0])
                .collect(Collectors.toList());

        List<User> suggestedUsers = em.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
                .setParameter("ids", suggestedUserIds)
                .getResultList();

        return suggestedUsers.stream().map(u -> {
            UserDTO dto = new UserDTO();
            dto.setId(u.getId());
            dto.setName(u.getName());
            dto.setEmail(u.getEmail());
            dto.setBio(u.getBio());
            return dto;
        }).collect(Collectors.toList());
    }

}
