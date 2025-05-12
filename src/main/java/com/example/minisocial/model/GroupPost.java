package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_posts")
public class GroupPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Group group;

    @ManyToOne(optional = false)
    private User author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

/*
| Field      | Type              | Constraints                  | Description                |
|------------|-------------------|------------------------------|----------------------------|
| id         | Long              | Primary Key, Auto-generated  | GroupPost ID               |
| group      | Group (ManyToOne) | Not null                     | Group related to the post  |
| author     | User (ManyToOne)  | Not null                     | User who authored the post |
| content    | String (TEXT)     | Not null                     | Content of the group post  |
| createdAt  | LocalDateTime     | Default: now()               | Post creation timestamp    |
*/