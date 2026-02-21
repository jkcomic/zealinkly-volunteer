package com.example.zealinkly_volunteer.models.request;

public class AudioRecognizeRequest {
    private String audioBase64;
    private String format;
    private int rate;
    
    public AudioRecognizeRequest(String audioBase64, String format, int rate) {
        this.audioBase64 = audioBase64;
        this.format = format;
        this.rate = rate;
    }
    
    // 默认构造方法，使用默认参数
    public AudioRecognizeRequest(String audioBase64) {
        this.audioBase64 = audioBase64;
        this.format = "wav";
        this.rate = 16000;
    }
    
    public String getAudioBase64() {
        return audioBase64;
    }
    
    public void setAudioBase64(String audioBase64) {
        this.audioBase64 = audioBase64;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public int getRate() {
        return rate;
    }
    
    public void setRate(int rate) {
        this.rate = rate;
    }
}