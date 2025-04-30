package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
}

/*
| Field      | Type              | Constraints                  | Description               |
|------------|-------------------|------------------------------|---------------------------|
| id         | Long              | Primary Key, Auto-generated  | Post ID                   |
| author     | User (ManyToOne)  | Not null                     | The user who made the post|
| content    | String (TEXT)     | None                         | Text content of the post  |
| imageUrl   | String            | None                         | Optional image URL        |
| createdAt  | LocalDateTime     | Default: now()               | Post creation timestamp   |
*/