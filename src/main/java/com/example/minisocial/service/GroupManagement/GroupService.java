package com.example.minisocial.service.GroupManagement;

import com.example.minisocial.model.Group;
import com.example.minisocial.model.GroupMembership;
import com.example.minisocial.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class GroupService {

    @PersistenceContext
    private EntityManager em;

    public Group createGroup(String name, String description, User creator) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreator(creator);
        group.setOpen(true);
        em.persist(group);

        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(creator);
        membership.setAdmin(true);
        membership.setStatus(GroupMembership.Status.APPROVED);
        em.persist(membership);

        return group;
    }

}