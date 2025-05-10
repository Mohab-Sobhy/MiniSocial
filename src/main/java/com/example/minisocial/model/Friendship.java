package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
public class Friendship {

    public enum Status { PENDING, ACCEPTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ----------- relationships ----------- */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;              // person who sent the request

    @ManyToOne(optional = false)
    @JoinColumn(name = "friend_id")
    private User friend;            // person who receives the request

    /* ----------- metadata ----------- */
    @Column(nullable = false)
    private LocalDateTime friendshipDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    /* ----------- lifecycle ----------- */
    @PrePersist
    private void onCreate() {
        friendshipDate = LocalDateTime.now();
    }

    /* ----------- getters / setters ----------- */
    public Long getId()               { return id; }
    public User getUser()             { return user; }
    public User getFriend()           { return friend; }
    public LocalDateTime getFriendshipDate() { return friendshipDate; }
    public Status getStatus()         { return status; }

    public void setUser(User user)         { this.user = user; }
    public void setFriend(User friend)     { this.friend = friend; }
    public void setStatus(Status status)   { this.status = status; }
}
