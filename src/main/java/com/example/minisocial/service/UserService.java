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


    public User updateUserProfile(Long userId, String name, String bio, String password, String email) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            user.setName(name);
            user.setBio(bio);
            user.setEmail(email);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            entityManager.merge(user);
        }
        return user;
    }
    
}
