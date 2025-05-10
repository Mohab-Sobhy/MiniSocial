package com.example.minisocial.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    public enum Role { USER, ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*---  Fields mapped from JSON (case-insensitive via @JsonProperty)  ---*/

    @JsonProperty("Email")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty("Password")
    @Column(nullable = false)
    private String password;

    @JsonProperty("Name")
    @Column(nullable = false)
    private String name;

    @JsonProperty("Bio")
    private String bio;

    @JsonProperty("Role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    /*---  Getters / Setters  ---*/

    public Long getId()         { return id; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getName()     { return name; }
    public String getBio()      { return bio; }
    public Role getRole()       { return role; }

    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name)         { this.name = name; }
    public void setBio(String bio)           { this.bio = bio; }
    public void setRole(Role role)           { this.role = role; }
}
