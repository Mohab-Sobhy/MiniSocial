package com.example.minisocial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(columnDefinition = "TEXT")
    private String eventData;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EventType {
        FRIEND_REQUEST, COMMENT, LIKE, GROUP_JOIN, GROUP_APPROVED
    }

    // Getters & Setters
}

/*
| Field      | Type              | Constraints                  | Description                           |
|------------|-------------------|------------------------------|---------------------------------------|
| id         | Long              | Primary Key, Auto-generated  | Notification ID                       |
| user       | User (ManyToOne)  | Not null                     | User related to the notification      |
| eventType  | Enum (EventType)  | Not null                     | Type of event (e.g., FRIEND_REQUEST)  |
| eventData  | String (TEXT)     | None                         | Additional event-related data         |
| createdAt  | LocalDateTime     | Default: now()               | Notification creation timestamp       |
*/