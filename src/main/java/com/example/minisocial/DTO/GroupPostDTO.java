package com.example.minisocial.DTO;

public class GroupPostDTO {
    private Long groupId;
    private String content;

    public GroupPostDTO() {
    }

    public GroupPostDTO(Long groupId, String content) {
        this.groupId = groupId;
        this.content = content;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
