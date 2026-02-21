package com.example.zealinkly_volunteer.models.response;

import java.util.List;

// API返回的直接是TaskItem数组，不需要外层包装
public class TaskListResponse {
    public static class TaskItem {
        private int id;
        private String taskType;
        private String status;
        private Elder elder;
        private Volunteer volunteer;
        private String content;
        private int pointsReward;
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
        
        public Elder getElder() {
            return elder;
        }
        
        public void setElder(Elder elder) {
            this.elder = elder;
        }
        
        public Volunteer getVolunteer() {
            return volunteer;
        }
        
        public void setVolunteer(Volunteer volunteer) {
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
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
    
    public static class Elder {
        private int id;
        private String realName;
        private String phone;
        private String address;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getRealName() {
            return realName;
        }
        
        public void setRealName(String realName) {
            this.realName = realName;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
    }
    
    public static class Volunteer {
        private int id;
        private String realName;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getRealName() {
            return realName;
        }
        
        public void setRealName(String realName) {
            this.realName = realName;
        }
    }
}