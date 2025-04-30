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
}

/*
| Field           | Type              | Constraints                      | Description                          |
|-----------------|-------------------|----------------------------------|--------------------------------------|
| id              | Long              | Primary Key, Auto-generated      | Friendship ID                        |
| user_id         | Long              | Not null                         | User ID                              |
| friend_id       | Long              | Not null                         | Friend ID                            |
| friendship_date | LocalDateTime     | Not null                         | Date when the friendship was created |
*/
