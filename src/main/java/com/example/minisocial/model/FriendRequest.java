package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User sender;

    @ManyToOne(optional = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    // Getters & Setters
}

/*
| Field      | Type              | Constraints                  | Description                                       |
|------------|-------------------|------------------------------|---------------------------------------------------|
| id         | Long              | Primary Key, Auto-generated  | FriendRequest ID                                  |
| sender     | User (ManyToOne)  | Not null                     | User who sent the friend request                  |
| receiver   | User (ManyToOne)  | Not null                     | User who received the request                     |
| status     | Enum (Status)     | Default: PENDING             | Status of the request (PENDING/ACCEPTED/REJECTED) |
| timestamp  | LocalDateTime     | Default: now()               | Time when the request was created                 |
*/