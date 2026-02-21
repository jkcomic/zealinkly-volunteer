package com.example.zealinkly_volunteer.models.request;

public class LoginRequest {
    private String username;
    private String password;
    private String userType;
    private String cardImageBase64;
    private String cardType;
    
    // 用于账号密码登录
    public LoginRequest(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }
    
    // 用于卡片登录
    public LoginRequest(String cardImageBase64, String cardType) {
        this.cardImageBase64 = cardImageBase64;
        this.cardType = cardType;
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
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getCardImageBase64() {
        return cardImageBase64;
    }
    
    public void setCardImageBase64(String cardImageBase64) {
        this.cardImageBase64 = cardImageBase64;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}