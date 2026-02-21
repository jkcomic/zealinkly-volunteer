package com.example.zealinkly_volunteer.network;

import android.util.Log;

import com.example.zealinkly_volunteer.utils.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        // 每次都创建新的Retrofit实例，确保拦截器能获取最新的token
        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // 创建OkHttpClient
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
        
        // 添加认证拦截器
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                
                // 每次请求都重新获取最新的token
                String token = null;
                try {
                    TokenManager tokenManager = TokenManager.getInstance();
                    token = tokenManager.getToken();
                    Log.d(TAG, "Token found: " + (token != null ? "Yes" : "No"));
                } catch (Exception e) {
                    Log.d(TAG, "TokenManager not initialized: " + e.getMessage());
                }
                
                // 如果有token，添加到请求头
                if (token != null) {
                    Log.d(TAG, "Adding token to request: Bearer " + token.substring(0, Math.min(20, token.length())) + "...");
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
                
                Log.d(TAG, "No token available, proceeding without authentication");
                return chain.proceed(original);
            }
        });
        
        // 创建Retrofit实例
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        return retrofit;
    }
}