package com.example.zealinkly_volunteer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zealinkly_volunteer.models.request.UpdateUserInfoRequest;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.UserInfoResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etRealName;
    private EditText etPhone;
    private Button btnSave;
    private Button btnCancel;
    private ApiService apiService;
    private UserInfoResponse currentUserInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        // 初始化TokenManager
        TokenManager.init(this);
        
        // 初始化视图
        etRealName = findViewById(R.id.et_real_name);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        
        // 初始化API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 设置按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 加载当前个人信息
        loadCurrentUserInfo();
    }
    
    private void loadCurrentUserInfo() {
        // 显示加载状态
        Toast.makeText(this, "加载中...", Toast.LENGTH_SHORT).show();
        
        // 调用API获取个人信息
        apiService.getUserInfo().enqueue(new Callback<ApiResponse<UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInfoResponse>> call, Response<ApiResponse<UserInfoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserInfoResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        currentUserInfo = apiResponse.getData();
                        if (currentUserInfo != null) {
                            // 填充表单
                            etRealName.setText(currentUserInfo.getRealName() != null ? currentUserInfo.getRealName() : "");
                            etPhone.setText(currentUserInfo.getPhone() != null ? currentUserInfo.getPhone() : "");
                        } else {
                            Toast.makeText(EditProfileActivity.this, "用户信息异常", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "加载失败";
                    if (response.code() == 401) {
                        errorMsg = "登录已过期，请重新登录";
                    } else if (response.code() == 403) {
                        errorMsg = "权限不足";
                    } else if (response.code() == 404) {
                        errorMsg = "服务不存在";
                    } else if (response.code() == 500) {
                        errorMsg = "服务器内部错误";
                    } else {
                        errorMsg = "加载失败，请检查网络连接";
                    }
                    Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<UserInfoResponse>> call, Throwable t) {
                String errorMessage = "网络错误，请检查网络连接";
                if (t.getMessage() != null && t.getMessage().contains("ConnectException")) {
                    errorMessage = "无法连接到服务器，请检查网络连接";
                }
                Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateUserInfo() {
        // 获取表单数据
        String realName = etRealName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        
        // 验证输入
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
        
        // 创建请求对象
        UpdateUserInfoRequest request = new UpdateUserInfoRequest(realName, phone);
        
        // 显示加载状态
        Toast.makeText(this, "保存中...", Toast.LENGTH_SHORT).show();
        
        // 调用API更新个人信息
        apiService.updateUserInfo(request).enqueue(new Callback<ApiResponse<UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInfoResponse>> call, Response<ApiResponse<UserInfoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserInfoResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(EditProfileActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        // 设置结果码，通知调用方更新成功
                        setResult(RESULT_OK);
                        // 结束当前Activity，返回上一页
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "更新失败";
                    if (response.code() == 401) {
                        errorMsg = "登录已过期，请重新登录";
                    } else if (response.code() == 403) {
                        errorMsg = "权限不足";
                    } else if (response.code() == 404) {
                        errorMsg = "服务不存在";
                    } else if (response.code() == 500) {
                        errorMsg = "服务器内部错误";
                    } else {
                        errorMsg = "更新失败，请检查网络连接";
                    }
                    Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<UserInfoResponse>> call, Throwable t) {
                String errorMessage = "网络错误，请检查网络连接";
                if (t.getMessage() != null && t.getMessage().contains("ConnectException")) {
                    errorMessage = "无法连接到服务器，请检查网络连接";
                }
                Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean isValidPhone(String phone) {
        // 简单的手机号验证
        return phone.matches("1[3-9]\\d{9}");
    }
}
