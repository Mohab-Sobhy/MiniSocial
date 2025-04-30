package com.example.minisocial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
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

    // Getters and Setters
}

/*
| Field     | Type            | Constraints                    | Description        |
|-----------|-----------------|--------------------------------|--------------------|
| id        | Long            | Primary Key, Auto-generated    | User ID            |
| name      | String          | None                           | User's full name   |
| email     | String          | Not null, Unique               | User's email       |
| password  | String          | None                           | User's password    |
| bio       | String          | None                           | User's bio         |
| role      | Enum (Role)     | Not null, Enum (USER, ADMIN)   | User's role        |
*/