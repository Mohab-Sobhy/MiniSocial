package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* -------- relationships -------- */
    @ManyToOne(optional = false)
    private User author;

    /* -------- payload -------- */
    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    /*  🔧 EXPLICIT COLUMN so MySQL has it  */
    @Column(name = "link_url", length = 512)   // DB column name exactly as DESCRIBE shows
    private String linkUrl;
    /* -------- timestamps -------- */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* -------- lifecycle hooks -------- */
    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* -------- getters / setters -------- */
    public Long getId()              { return id; }
    public User getAuthor()          { return author; }
    public String getContent()       { return content; }
    public String getImageUrl()      { return imageUrl; }
    public String getLinkUrl()       { return linkUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setAuthor(User author)      { this.author = author; }
    public void setContent(String content)  { this.content = content; }
    public void setImageUrl(String url)     { this.imageUrl = url; }
    public void setLinkUrl(String url)      { this.linkUrl = url; }
}
