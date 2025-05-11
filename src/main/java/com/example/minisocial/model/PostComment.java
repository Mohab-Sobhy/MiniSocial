package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)  @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(optional = false)
    private User author;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() { createdAt = LocalDateTime.now(); }

    // getters + setters
    public Long getId()           { return id; }
    public Post getPost()         { return post; }
    public User getAuthor()       { return author; }
    public String getText()       { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setPost(Post post)       { this.post = post; }
    public void setAuthor(User author)   { this.author = author; }
    public void setText(String text)     { this.text = text; }
}
