package com.example.zealinkly_volunteer.network;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_TYPE = "user_type";
    
    private static TokenManager instance;
    private SharedPreferences sharedPreferences;
    
    private TokenManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TokenManager not initialized. Call init() first.");
        }
        return instance;
    }
    
    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
    }
    
    public void saveToken(String token, int userId, String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();
    }
    
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }
    
    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, null);
    }
    
    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_TYPE);
        editor.apply();
    }
    
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}