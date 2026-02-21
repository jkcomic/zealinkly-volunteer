package com.example.zealinkly_volunteer.models.request;

public class UpdateUserInfoRequest {
    private String realName;
    private String phone;
    
    public UpdateUserInfoRequest(String realName, String phone) {
        this.realName = realName;
        this.phone = phone;
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