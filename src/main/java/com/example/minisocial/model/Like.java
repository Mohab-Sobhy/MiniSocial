package com.example.minisocial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class Like {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Post post;

    @ManyToOne(optional = false)
    private User user;

    // Getters & Setters
}

/*
| Field      | Type              | Constraints                  | Description                 |
|------------|-------------------|------------------------------|-----------------------------|
| id         | Long              | Primary Key, Auto-generated  | Like ID                     |
| post       | Post (ManyToOne)  | Not null                     | Post related to the like    |
| user       | User (ManyToOne)  | Not null                     | User who liked the post     |
*/