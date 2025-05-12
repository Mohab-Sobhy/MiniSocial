package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // Getters
    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
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