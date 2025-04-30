package com.example.minisocial.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")// indexing in email
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        USER, ADMIN
    }

    // ✅ علاقات الأصدقاء
    @OneToMany(mappedBy = "user")
    private Set<Friendship> friendships = new HashSet<>();
}

/*
| Field           | Type              | Constraints                      | Description                           |
|-----------------|-------------------|----------------------------------|---------------------------------------|
| id              | Long              | Primary Key, Auto-generated      | User ID                               |
| name            | String            | None                             | User's name                           |
| email           | String            | Not null, Unique                 | User's email                          |
| password        | String            | None                             | User's password                       |
| bio             | String            | None                             | User's bio                            |
| role            | Enum (Role)       | Not null                         | User's role (USER, ADMIN)             |
*/