package com.example.minisocial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "group_memberships", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
public class GroupMembership {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Group group;

    @ManyToOne(optional = false)
    private User user;

    private boolean isAdmin = false;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, APPROVED
    }

    // Getters & Setters
}

/*
| Field      | Type              | Constraints                  | Description                                   |
|------------|-------------------|------------------------------|-----------------------------------------------|
| id         | Long              | Primary Key, Auto-generated  | GroupMembership ID                            |
| group      | Group (ManyToOne) | Not null                     | Group related to the membership               |
| user       | User (ManyToOne)  | Not null                     | User related to the membership                |
| isAdmin    | boolean           | Default: false               | Indicates if the user is an admin             |
| status     | Enum (Status)     | Default: PENDING             | Status of the membership (PENDING/APPROVED)   |
*/
