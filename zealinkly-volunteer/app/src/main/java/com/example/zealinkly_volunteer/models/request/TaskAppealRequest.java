package com.example.zealinkly_volunteer.models.request;

public class TaskAppealRequest {
    private String content;
    
    public TaskAppealRequest() {
    }
    
    public TaskAppealRequest(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}