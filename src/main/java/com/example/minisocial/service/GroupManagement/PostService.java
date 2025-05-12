package com.example.minisocial.service.GroupManagement;

import com.example.minisocial.model.Group;
import com.example.minisocial.model.GroupMembership;
import com.example.minisocial.model.GroupPost;
import com.example.minisocial.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class PostService {

    @PersistenceContext
    private EntityManager em;

    public void createPost(User author, Group group, String content) {
        // Check if user is a member
        TypedQuery<GroupMembership> query = em.createQuery(
                "SELECT m FROM GroupMembership m WHERE m.user = :user AND m.group = :group AND m.status = :status", GroupMembership.class);
        query.setParameter("user", author);
        query.setParameter("group", group);
        query.setParameter("status", GroupMembership.Status.APPROVED);

        if (query.getResultList().isEmpty()) {
            throw new SecurityException("User is not a member of the group");
        }

        GroupPost post = new GroupPost();
        post.setAuthor(author);
        post.setGroup(group);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        em.persist(post);
    }

    public List<GroupPost> getGroupPosts(User user, Group group) {
        // Ensure user is a member
        TypedQuery<GroupMembership> query = em.createQuery(
                "SELECT m FROM GroupMembership m WHERE m.user = :user AND m.group = :group AND m.status = :status", GroupMembership.class);
        query.setParameter("user", user);
        query.setParameter("group", group);
        query.setParameter("status", GroupMembership.Status.APPROVED);

        if (query.getResultList().isEmpty()) {
            throw new SecurityException("Access denied");
        }

        return em.createQuery("SELECT p FROM GroupPost p WHERE p.group = :group", GroupPost.class)
                .setParameter("group", group)
                .getResultList();
    }
}
