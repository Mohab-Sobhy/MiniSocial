package com.example.minisocial.service.GroupManagement;

import com.example.minisocial.model.Group;
import com.example.minisocial.model.GroupMembership;
import com.example.minisocial.model.GroupPost;
import com.example.minisocial.model.User;
import com.example.minisocial.util.JwtUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class GroupService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private JwtUtil jwtUtil;

    public Group createGroup(String token, String name, String description, boolean isOpen) {
        Long userId = jwtUtil.getIdFromToken(token);
        User creator = em.find(User.class, userId);

        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreator(creator);
        group.setOpen(isOpen);
        em.persist(group);

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(creator);
        membership.setAdmin(true);
        membership.setStatus(GroupMembership.Status.APPROVED);
        em.persist(membership);

        return group;
    }

    public void requestToJoinGroup(String token, Long groupId) {
        Long userId = jwtUtil.getIdFromToken(token);
        User user = em.find(User.class, userId);
        Group group = em.find(Group.class, groupId);

        boolean alreadyRequested = em.createQuery("SELECT COUNT(m) FROM GroupMembership m WHERE m.group = :group AND m.user = :user", Long.class)
                .setParameter("group", group)
                .setParameter("user", user)
                .getSingleResult() > 0;

        if (alreadyRequested) throw new WebApplicationException("Already requested or member", 400);

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setStatus(group.isOpen() ? GroupMembership.Status.APPROVED : GroupMembership.Status.PENDING);
        em.persist(membership);

        // TODO: Send JMS notification
    }

    public void leaveGroup(String token, Long groupId) {
        Long userId = jwtUtil.getIdFromToken(token);
        GroupMembership membership = em.createQuery(
                        "SELECT m FROM GroupMembership m WHERE m.group.id = :groupId AND m.user.id = :userId", GroupMembership.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult();
        em.remove(membership);
    }

    public void approveMembership(String token, Long membershipId) {
        Long adminId = jwtUtil.getIdFromToken(token);
        GroupMembership membership = em.find(GroupMembership.class, membershipId);

        boolean isAdmin = em.createQuery("SELECT COUNT(m) FROM GroupMembership m WHERE m.group = :group AND m.user.id = :adminId AND m.isAdmin = true", Long.class)
                .setParameter("group", membership.getGroup())
                .setParameter("adminId", adminId)
                .getSingleResult() > 0;

        if (!isAdmin) throw new WebApplicationException("Only admins can approve", 403);

        membership.setStatus(GroupMembership.Status.APPROVED);
        em.merge(membership);

        // TODO: Send JMS notification
    }

    public void postInGroup(String token, Long groupId, String content) {
        Long userId = jwtUtil.getIdFromToken(token);
        boolean isMember = em.createQuery(
                        "SELECT COUNT(m) FROM GroupMembership m WHERE m.group.id = :groupId AND m.user.id = :userId AND m.status = 'APPROVED'", Long.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult() > 0;

        if (!isMember) throw new WebApplicationException("Only members can post", 403);

        GroupPost post = new GroupPost();
        post.setAuthor(em.find(User.class, userId));
        post.setGroup(em.find(Group.class, groupId));
        post.setContent(content);
        em.persist(post);
    }
}
