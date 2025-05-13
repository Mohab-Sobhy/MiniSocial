package com.example.minisocial.service.GroupManagement;

import com.example.minisocial.DTO.GroupCreateDTO;
import com.example.minisocial.DTO.GroupPostDTO;
import com.example.minisocial.model.*;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
@Transactional
public class GroupService {

    @PersistenceContext
    private EntityManager em;

    public Group createGroup(User user, GroupCreateDTO dto) {
        Group group = new Group();
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setOpen(dto.isOpen());
        group.setCreator(user);
        em.persist(group);

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setAdmin(true);
        membership.setStatus(GroupMembership.Status.APPROVED);
        em.persist(membership);

        return group;
    }

    public void promoteToAdmin(User requester, Long groupId, Long userId) {
        if (!isUserAdmin(groupId, requester.getId())) {
            throw new SecurityException("Only admins can promote others.");
        }

        GroupMembership membership = getMembership(groupId, userId);
        membership.setAdmin(true);
        em.merge(membership);
    }

    public void removeGroupPost(User requester, Long postId) {
        GroupPost post = em.find(GroupPost.class, postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found.");
        }

        boolean isAdmin = isUserAdmin(post.getGroup().getId(), requester.getId());
        boolean isAuthor = post.getAuthor().getId().equals(requester.getId());

        if (!isAdmin && !isAuthor) {
            throw new SecurityException("Only authors or group admins can remove posts.");
        }

        em.remove(post);
    }

    public void removeUserFromGroup(User requester, Long groupId, Long userId) {
        if (!isUserAdmin(groupId, requester.getId())) {
            throw new SecurityException("Only admins can remove users.");
        }

        GroupMembership membership = getMembership(groupId, userId);
        em.remove(membership);
    }

    public void deleteGroup(User requester, Long groupId) {
        Group group = em.find(Group.class, groupId);
        if (group == null || !group.getCreator().getId().equals(requester.getId())) {
            throw new SecurityException("Only the creator can delete the group.");
        }

        em.createQuery("DELETE FROM GroupPost p WHERE p.group.id = :groupId")
                .setParameter("groupId", groupId).executeUpdate();

        em.createQuery("DELETE FROM GroupMembership m WHERE m.group.id = :groupId")
                .setParameter("groupId", groupId).executeUpdate();

        em.remove(group);
    }

    public void requestToJoinGroup(User user, Long groupId) {
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }

        Long existing = em.createQuery("SELECT COUNT(m) FROM GroupMembership m WHERE m.group.id = :groupId AND m.user.id = :userId", Long.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", user.getId())
                .getSingleResult();

        if (existing > 0) {
            throw new IllegalStateException("Already requested or joined.");
        }

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setStatus(group.isOpen() ? GroupMembership.Status.APPROVED : GroupMembership.Status.PENDING);
        em.persist(membership);
    }

    public void approveJoinRequest(User admin, Long groupId, Long userId) {
        if (!isUserAdmin(groupId, admin.getId())) {
            throw new SecurityException("Only admins can approve requests.");
        }

        GroupMembership membership = getMembership(groupId, userId);
        membership.setStatus(GroupMembership.Status.APPROVED);
        em.merge(membership);
    }

    public void rejectJoinRequest(User admin, Long groupId, Long userId) {
        if (!isUserAdmin(groupId, admin.getId())) {
            throw new SecurityException("Only admins can reject requests.");
        }

        GroupMembership membership = getMembership(groupId, userId);
        em.remove(membership);
    }

    public GroupPost createGroupPost(User user, Long groupId, GroupPostDTO dto) {
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }

        if (!isUserApprovedMember(groupId, user.getId())) {
            throw new SecurityException("Not a member of the group.");
        }

        GroupPost post = new GroupPost();
        post.setGroup(group);
        post.setAuthor(user);
        post.setContent(dto.getContent());
        em.persist(post);

        return post;
    }

    private boolean isUserAdmin(Long groupId, Long userId) {
        Long count = em.createQuery(
                        "SELECT COUNT(m) FROM GroupMembership m WHERE m.group.id = :groupId AND m.user.id = :userId AND m.isAdmin = true",
                        Long.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult();
        return count > 0;
    }

    private boolean isUserApprovedMember(Long groupId, Long userId) {
        Long count = em.createQuery(
                        "SELECT COUNT(m) FROM GroupMembership m WHERE m.group.id = :groupId AND m.user.id = :userId AND m.status = :status",
                        Long.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .setParameter("status", GroupMembership.Status.APPROVED)
                .getSingleResult();
        return count > 0;
    }

    private GroupMembership getMembership(Long groupId, Long userId) {
        return em.createQuery(
                        "SELECT m FROM GroupMembership m WHERE m.group.id = :groupId AND m.user.id = :userId", GroupMembership.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
