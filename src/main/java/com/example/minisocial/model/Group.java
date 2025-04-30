package com.example.minisocial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "groups")
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(optional = false)
    private User creator;

    private boolean isOpen = true;

    // Getters & Setters
}

/*
| Field      | Type              | Constraints                  | Description                   |
|------------|-------------------|------------------------------|-------------------------------|
| id         | Long              | Primary Key, Auto-generated  | Group ID                      |
| name       | String            | Not null                     | Name of the group             |
| description| String            | None                         | Description of the group      |
| creator    | User (ManyToOne)  | Not null                     | User who created the group    |
| isOpen     | boolean           | Default: true                | Indicates if the group is open|
*/