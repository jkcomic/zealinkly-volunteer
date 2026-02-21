package com.example.zealinkly_volunteer.models.request;

import java.util.List;

public class TaskSubmitRequest {
    private String note;
    private List<String> evidences;
    
    public TaskSubmitRequest() {
    }
    
    public TaskSubmitRequest(String note, List<String> evidences) {
        this.note = note;
        this.evidences = evidences;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public List<String> getEvidences() {
        return evidences;
    }
    
    public void setEvidences(List<String> evidences) {
        this.evidences = evidences;
    }
}