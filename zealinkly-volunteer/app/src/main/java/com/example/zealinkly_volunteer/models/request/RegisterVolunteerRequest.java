package com.example.zealinkly_volunteer.models.request;

public class RegisterVolunteerRequest {
    private String username;
    private String password;
    private String realName;
    private String phone;
    
    public RegisterVolunteerRequest(String username, String password, String realName, String phone) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
}