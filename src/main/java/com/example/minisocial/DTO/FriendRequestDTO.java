package com.example.minisocial.DTO;

public class FriendRequestDTO {
    private Long senderId;
    private Long receiverId;
    private Long id;
    private String status;

    // Getters
    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}