package com.example.minisocial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(optional = false)
    private User creator;

    private boolean isOpen = true;

    // Getters & Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public User getCreator() {return creator;}
    public void setCreator(User creator) {this.creator = creator;}
    public boolean isOpen() {return isOpen;}
    public void setOpen(boolean isOpen) {this.isOpen = isOpen;}
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