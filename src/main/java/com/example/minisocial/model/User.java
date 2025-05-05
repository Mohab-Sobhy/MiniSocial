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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = Role.valueOf(role);
    }
    public enum Role {
        USER, ADMIN
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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