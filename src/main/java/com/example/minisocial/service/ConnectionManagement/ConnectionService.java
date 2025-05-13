package com.example.minisocial.service.ConnectionManagement;

import com.example.minisocial.DTO.FriendRequestDTO;
import com.example.minisocial.DTO.UserDTO;
import com.example.minisocial.model.FriendRequest;
import com.example.minisocial.model.Friendship;
import com.example.minisocial.model.User;
import com.example.minisocial.util.JwtUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConnectionService {

    @PersistenceContext
    EntityManager em;

    public List<UserDTO> getFriends(Long userId) {
        return em.createQuery("SELECT f.friend FROM Friendship f WHERE f.user.id = :uid AND f.status = :status", User.class)
                .setParameter("uid", userId)
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList()
                .stream()
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getBio()))
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(Long userId, String keyword) {
        List<Long> friendIds = em.createQuery(
                        "SELECT f.friend.id FROM Friendship f WHERE f.user.id = :uid AND f.status = :status", Long.class)
                .setParameter("uid", userId)
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        friendIds.add(userId);

        return em.createQuery(
                        "SELECT u FROM User u WHERE (LOWER(u.name) LIKE :kw OR LOWER(u.email) LIKE :kw) AND u.id NOT IN :excludedIds", User.class)
                .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                .setParameter("excludedIds", friendIds)
                .getResultList()
                .stream()
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getBio()))
                .collect(Collectors.toList());
    }

    public List<UserDTO> suggestFriends(Long userId) {
        List<Long> myFriendIds = em.createQuery(
                        "SELECT f.friend.id FROM Friendship f WHERE f.user.id = :uid AND f.status = :status", Long.class)
                .setParameter("uid", userId)
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        if (myFriendIds.isEmpty()) return List.of();

        List<Object[]> suggestions = em.createQuery(
                        "SELECT f.friend.id, COUNT(f.friend.id) as mutualCount " +
                                "FROM Friendship f " +
                                "WHERE f.user.id IN :myFriends " +
                                "AND f.friend.id NOT IN :excluded " +
                                "AND f.status = :status " +
                                "GROUP BY f.friend.id " +
                                "ORDER BY mutualCount DESC", Object[].class)
                .setParameter("myFriends", myFriendIds)
                .setParameter("excluded", new ArrayList<>(myFriendIds) {{
                    add(userId);
                }})
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        if (suggestions.isEmpty()) return List.of();

        List<Long> suggestedUserIds = suggestions.stream()
                .map(o -> (Long) o[0])
                .collect(Collectors.toList());

        List<User> suggestedUsers = em.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
                .setParameter("ids", suggestedUserIds)
                .getResultList();

        return suggestedUsers.stream().map(u ->
                new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getBio())
        ).collect(Collectors.toList());
    }

    @Transactional
    public void sendFriendRequest(Long senderId, Long receiverId) {
        Long count = em.createQuery(
                        "SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.sender.id = :sender AND fr.receiver.id = :receiver AND fr.status = :status",
                        Long.class)
                .setParameter("sender", senderId)
                .setParameter("receiver", receiverId)
                .setParameter("status", FriendRequest.Status.PENDING)
                .getSingleResult();

        if (count == 0) {
            FriendRequest request = new FriendRequest();
            request.setSender(em.find(User.class, senderId));
            request.setReceiver(em.find(User.class, receiverId));
            request.setStatus(FriendRequest.Status.PENDING);
            em.persist(request);
        }
    }

    public List<FriendRequestDTO> getPendingRequests(Long userId) {
        return em.createQuery(
                        "SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :uid AND fr.status = :status", FriendRequest.class)
                .setParameter("uid", userId)
                .setParameter("status", FriendRequest.Status.PENDING)
                .getResultList()
                .stream()
                .map(fr -> new FriendRequestDTO(
                        fr.getId(),
                        fr.getSender().getId(),
                        fr.getReceiver().getId(),
                        fr.getStatus().name()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(Long userId, Long requestId) {
        FriendRequest request = em.find(FriendRequest.class, requestId);
        if (request != null && request.getReceiver().getId().equals(userId)) {
            request.setStatus(FriendRequest.Status.ACCEPTED);

            Friendship friendship = new Friendship();
            friendship.setUser(request.getSender());
            friendship.setFriend(request.getReceiver());
            friendship.setStatus(Friendship.Status.ACCEPTED);
            em.persist(friendship);

            Friendship reciprocal = new Friendship();
            reciprocal.setUser(request.getReceiver());
            reciprocal.setFriend(request.getSender());
            reciprocal.setStatus(Friendship.Status.ACCEPTED);
            em.persist(reciprocal);
        }
    }

    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        FriendRequest request = em.find(FriendRequest.class, requestId);
        if (request != null && request.getReceiver().getId().equals(userId)) {
            request.setStatus(FriendRequest.Status.REJECTED);
        }
    }
}
