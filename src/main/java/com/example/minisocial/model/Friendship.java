package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column(nullable = false)
    private LocalDateTime friendshipDate;

    private boolean accepted;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}

/*
| Field           | Type              | Constraints                      | Description                          |
|-----------------|-------------------|----------------------------------|--------------------------------------|
| id              | Long              | Primary Key, Auto-generated      | Friendship ID                        |
| user_id         | Long              | Not null                         | User ID                              |
| friend_id       | Long              | Not null                         | Friend ID                            |
| friendship_date | LocalDateTime     | Not null                         | Date when the friendship was created |
*/
