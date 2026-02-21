package com.example.zealinkly_volunteer.models.response;

import java.util.List;

public class TaskDetailResponse {
    private int id;
    private String taskType;
    private String status;
    private TaskListResponse.Elder elder;
    private TaskListResponse.Volunteer volunteer;
    private String content;
    private int pointsReward;
    private List<Evidence> evidences;
    private String createdAt;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTaskType() {
        return taskType;
    }
    
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public TaskListResponse.Elder getElder() {
        return elder;
    }
    
    public void setElder(TaskListResponse.Elder elder) {
        this.elder = elder;
    }
    
    public TaskListResponse.Volunteer getVolunteer() {
        return volunteer;
    }
    
    public void setVolunteer(TaskListResponse.Volunteer volunteer) {
        this.volunteer = volunteer;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getPointsReward() {
        return pointsReward;
    }
    
    public void setPointsReward(int pointsReward) {
        this.pointsReward = pointsReward;
    }
    
    public List<Evidence> getEvidences() {
        return evidences;
    }
    
    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public static class Evidence {
        private int id;
        private String evidenceType;
        private String fileUrl;
        private String createdAt;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getEvidenceType() {
            return evidenceType;
        }
        
        public void setEvidenceType(String evidenceType) {
            this.evidenceType = evidenceType;
        }
        
        public String getFileUrl() {
            return fileUrl;
        }
        
        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}