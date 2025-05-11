package com.example.minisocial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)  @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(optional = false)  @JoinColumn(name = "user_id")
    private User user;

    // getters + setters
    public Long getId()   { return id; }
    public Post getPost() { return post; }
    public User getUser() { return user; }

    public void setPost(Post p)  { this.post = p; }
    public void setUser(User u)  { this.user = u; }
}
