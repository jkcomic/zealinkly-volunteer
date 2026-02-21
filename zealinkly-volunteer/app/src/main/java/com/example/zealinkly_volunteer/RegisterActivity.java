package com.example.zealinkly_volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zealinkly_volunteer.models.request.RegisterVolunteerRequest;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.LoginResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etRealName;
    private EditText etPhone;
    private Button btnRegister;
    private Button btnBack;
    
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // 初始化TokenManager
        TokenManager.init(this);
        
        // 初始化视图
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etRealName = findViewById(R.id.et_real_name);
        etPhone = findViewById(R.id.et_phone);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);
        
        // 初始化API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 设置点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String realName = etRealName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        
        // 验证输入
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (realName.isEmpty()) {
            Toast.makeText(this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "请输入有效的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示加载状态
        btnRegister.setEnabled(false);
        
        try {
            // 创建注册请求
            RegisterVolunteerRequest request = new RegisterVolunteerRequest(username, password, realName, phone);
            
            // 调用注册接口
            apiService.registerVolunteer(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                    btnRegister.setEnabled(true);
                    
                    // 无论HTTP状态码如何，都尝试解析响应体
                    ApiResponse<LoginResponse> apiResponse = null;
                    try {
                        if (response.body() != null) {
                            apiResponse = response.body();
                        } else if (response.errorBody() != null) {
                            // 尝试解析错误响应体
                            Gson gson = new Gson();
                            apiResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {
                            // 注册成功，跳转到登录界面
                            Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "注册失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            // 捕获异常，确保按钮状态恢复
            btnRegister.setEnabled(true);
            Toast.makeText(this, "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 验证手机号是否有效
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhone(String phone) {
        // 简单的手机号验证
        return phone.matches("1[3-9]\\d{9}");
    }
}