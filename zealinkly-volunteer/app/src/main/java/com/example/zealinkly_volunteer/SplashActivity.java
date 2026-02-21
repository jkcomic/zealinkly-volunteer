package com.example.zealinkly_volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zealinkly_volunteer.network.TokenManager;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 2000;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // 初始化TokenManager
        TokenManager.init(this);
        
        // 延迟2秒后检查登录状态
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginStatus();
            }
        }, SPLASH_DELAY);
    }
    
    private void checkLoginStatus() {
        // 检查是否已登录
        if (TokenManager.getInstance().isLoggedIn()) {
            // 已登录，跳转到主界面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // 未登录，跳转到登录界面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}