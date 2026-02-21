package com.example.zealinkly_volunteer.models.response;

import java.util.List;

// API返回的直接是FileItem数组，不需要外层包装
public class FileListResponse {
    public static class FileItem {
        private int id;
        private String fileUrl;
        private String originalFilename;
        private long fileSize;
        private String contentType;
        private String fileType;
        private String createdAt;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getFileUrl() {
            return fileUrl;
        }
        
        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
        
        public String getOriginalFilename() {
            return originalFilename;
        }
        
        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }
        
        public long getFileSize() {
            return fileSize;
        }
        
        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }
        
        public String getContentType() {
            return contentType;
        }
        
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
        
        public String getFileType() {
            return fileType;
        }
        
        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}