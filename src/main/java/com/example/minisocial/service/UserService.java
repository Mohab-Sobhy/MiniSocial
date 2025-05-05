package com.example.minisocial.service;

import com.example.minisocial.model.Friendship;
import com.example.minisocial.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;


    public User registerUser(String email, String password, String name, String bio, User.Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setBio(bio);
        user.setRole(String.valueOf(role));

        entityManager.persist(user);
        return user;
    }


    public User loginUser(String email, String password) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    public User updateUserProfile(Long userId, String name, String bio, String password) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            user.setName(name);
            user.setBio(bio);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            entityManager.merge(user);
        }
        return user;
    }

    public Friendship sendFriendRequest(Long userId, Long friendId) {
        User user = entityManager.find(User.class, userId);
        User friend = entityManager.find(User.class, friendId);

        if (user != null && friend != null) {
            Friendship friendship = new Friendship();
            friendship.setUser(user);
            friendship.setFriend(friend);
            friendship.setAccepted(false); // Initially, the friendship is pending

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
}
