package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Post post;

    @ManyToOne(optional = false)
    private User author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
}

/*
| Field      | Type              | Constraints                  | Description                   |
|------------|-------------------|------------------------------|-------------------------------|
| id         | Long              | Primary Key, Auto-generated  | Comment ID                    |
| post       | Post (ManyToOne)  | Not null                     | Post related to the comment   |
| author     | User (ManyToOne)  | Not null                     | User who authored the comment |
| content    | String (TEXT)     | Not null                     | Content of the comment        |
| createdAt  | LocalDateTime     | Default: now()               | Comment creation timestamp    |
*/