package com.example.minisocial.service;

import com.example.minisocial.model.Friendship;
import com.example.minisocial.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.List;

@Stateless
public class FriendshipService {

    @PersistenceContext
    private EntityManager em;

    /* ------------ create ------------ */
    public Friendship sendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) return null;                        // cannot friend yourself

        // avoid duplicates (regardless of direction)
        boolean exists = em.createQuery("""
            SELECT COUNT(f) FROM Friendship f
            WHERE (f.user.id = :a AND f.friend.id = :b)
               OR (f.user.id = :b AND f.friend.id = :a)
            """, Long.class)
                .setParameter("a", senderId)
                .setParameter("b", receiverId)
                .getSingleResult() > 0;
        if (exists) return null;

        User sender   = em.find(User.class, senderId);
        User receiver = em.find(User.class, receiverId);
        if (sender == null || receiver == null) return null;

        Friendship fr = new Friendship();
        fr.setUser(sender);
        fr.setFriend(receiver);
        em.persist(fr);
        return fr;
    }

    /* ------------ update ------------ */
    public Friendship accept(Long friendshipId) {
        Friendship fr = em.find(Friendship.class, friendshipId);
        if (fr == null || fr.getStatus() == Friendship.Status.ACCEPTED) return null;
        fr.setStatus(Friendship.Status.ACCEPTED);
        return fr;    // managed entity
    }

    public boolean reject(Long friendshipId) {
        Friendship fr = em.find(Friendship.class, friendshipId);
        if (fr == null) return false;
        em.remove(fr);
        return true;
    }

    /* ------------ read ------------ */
    public List<User> listFriends(Long userId) {

        // friends where *I* am the sender
        List<User> outgoing = em.createQuery("""
        SELECT f.friend
        FROM Friendship f
        WHERE f.user.id   = :uid
          AND f.status     = :status
        """, User.class)
                .setParameter("uid",    userId)
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        // friends where *I* am the receiver
        List<User> incoming = em.createQuery("""
        SELECT f.user
        FROM Friendship f
        WHERE f.friend.id = :uid
          AND f.status     = :status
        """, User.class)
                .setParameter("uid",    userId)
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList();

        outgoing.addAll(incoming);
        return outgoing;   // UNIQUE constraint guarantees no duplicates
    }

    public List<Friendship> pendingRequests(Long userId) {
        return em.createQuery("""
            SELECT f FROM Friendship f
            WHERE f.friend.id = :uid
              AND f.status = com.example.minisocial.model.Friendship$Status.PENDING
            """, Friendship.class)
                .setParameter("uid", userId)
                .getResultList();
    }
}
