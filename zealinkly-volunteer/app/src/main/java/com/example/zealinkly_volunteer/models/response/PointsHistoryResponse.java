package com.example.zealinkly_volunteer.models.response;

import java.util.List;

// API返回的直接是PointsRecord数组，不需要外层包装
public class PointsHistoryResponse {
    public static class PointsRecord {
        private int id;
        private int amount;
        private int balanceAfter;
        private String reason;
        private String reasonDescription;
        private Integer taskId;
        private Integer exchangeId;
        private String createdAt;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public int getAmount() {
            return amount;
        }
        
        public void setAmount(int amount) {
            this.amount = amount;
        }
        
        public int getBalanceAfter() {
            return balanceAfter;
        }
        
        public void setBalanceAfter(int balanceAfter) {
            this.balanceAfter = balanceAfter;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public String getReasonDescription() {
            return reasonDescription;
        }
        
        public void setReasonDescription(String reasonDescription) {
            this.reasonDescription = reasonDescription;
        }
        
        public Integer getTaskId() {
            return taskId;
        }
        
        public void setTaskId(Integer taskId) {
            this.taskId = taskId;
        }
        
        public Integer getExchangeId() {
            return exchangeId;
        }
        
        public void setExchangeId(Integer exchangeId) {
            this.exchangeId = exchangeId;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}