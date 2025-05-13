package com.example.minisocial.DTO;

public class FriendRequestDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String status;

    public FriendRequestDTO(Long id, Long senderId, Long receiverId, String status) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
