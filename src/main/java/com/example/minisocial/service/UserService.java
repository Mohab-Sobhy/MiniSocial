package com.example.minisocial.service;

import com.example.minisocial.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

    /* ----------  Create  ---------- */
    public User registerUser(String email, String password,
                             String name, String bio, User.Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setBio(bio);
        user.setRole(role);
        em.persist(user);
        return user;
    }

    /* ----------  Read (login)  ---------- */
    public User loginUser(String email, String password) {
        try {
            return em.createQuery("""
                    SELECT u FROM User u
                    WHERE u.email = :email AND u.password = :password
                    """, User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /* ----------  Read (by id)  ---------- */
    public User findUserById(Long id) {
        return em.find(User.class, id);
    }

    /* ----------  Update  ---------- */
    public User updateUserProfile(Long id, String name,
                                  String bio, String password, String email) {
        User user = em.find(User.class, id);
        if (user == null) return null;

        if (name != null)     user.setName(name);
        if (bio != null)      user.setBio(bio);
        if (email != null)    user.setEmail(email);
        if (password != null && !password.isBlank())
            user.setPassword(password);

        return user;  // managed entity – changes auto-flushed
    }

    /* ----------  Delete  ---------- */
    public boolean deleteUser(Long id) {
        User user = em.find(User.class, id);
        if (user == null) return false;
        em.remove(user);
        return true;
    }
}
