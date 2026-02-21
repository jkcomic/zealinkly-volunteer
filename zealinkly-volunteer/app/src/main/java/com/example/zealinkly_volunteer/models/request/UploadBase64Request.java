package com.example.zealinkly_volunteer.models.request;

public class UploadBase64Request {
    private String base64Data;
    private String filename;
    private String contentType;
    private String relatedType;
    private int relatedId;
    
    public UploadBase64Request(String base64Data, String filename, String contentType, String relatedType, int relatedId) {
        this.base64Data = base64Data;
        this.filename = filename;
        this.contentType = contentType;
        this.relatedType = relatedType;
        this.relatedId = relatedId;
    }
    
    public String getBase64Data() {
        return base64Data;
    }
    
    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getRelatedType() {
        return relatedType;
    }
    
    public void setRelatedType(String relatedType) {
        this.relatedType = relatedType;
    }
    
    public int getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }
}