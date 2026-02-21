package com.example.zealinkly_volunteer.models.request;

public class CardLoginRequest {
    private String cardImageBase64;
    private String cardType;
    
    public CardLoginRequest(String cardImageBase64, String cardType) {
        this.cardImageBase64 = cardImageBase64;
        this.cardType = cardType;
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