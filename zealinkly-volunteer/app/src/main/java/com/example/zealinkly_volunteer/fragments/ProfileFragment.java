package com.example.zealinkly_volunteer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.zealinkly_volunteer.EditProfileActivity;
import com.example.zealinkly_volunteer.LoginActivity;
import com.example.zealinkly_volunteer.R;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.UserInfoResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView tvUsername;
    private TextView tvRealName;
    private TextView tvPhone;
    private TextView tvPoints;
    private Button btnEditInfo;
    private Button btnLogout;
    private ApiService apiService;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // 初始化视图
        tvUsername = view.findViewById(R.id.tv_username);
        tvRealName = view.findViewById(R.id.tv_real_name);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvPoints = view.findViewById(R.id.tv_points);
        btnEditInfo = view.findViewById(R.id.btn_edit_info);
        btnLogout = view.findViewById(R.id.btn_logout);
        
        // 初始化API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 设置按钮点击事件
        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到编辑个人信息界面
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, 1001);
            }
        });
        
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        
        // 加载个人信息
        loadUserInfo();
        
        return view;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 从编辑个人信息界面返回时，刷新个人信息
        if (requestCode == 1001) {
            loadUserInfo();
        }
    }
    
    private void loadUserInfo() {
        // 显示加载状态
        Toast.makeText(getContext(), "加载中...", Toast.LENGTH_SHORT).show();
        
        // 调用API获取个人信息
        apiService.getUserInfo().enqueue(new Callback<ApiResponse<UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInfoResponse>> call, Response<ApiResponse<UserInfoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserInfoResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        UserInfoResponse userInfoResponse = apiResponse.getData();
                        if (userInfoResponse != null) {
                            tvUsername.setText(userInfoResponse.getUsername());
                            tvRealName.setText(userInfoResponse.getRealName());
                            tvPhone.setText(userInfoResponse.getPhone());
                            tvPoints.setText(String.valueOf(userInfoResponse.getPoints()));
                        }
                    } else {
                        Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "加载失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<UserInfoResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "加载失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void logout() {
        // 清除token
        TokenManager.getInstance().clearToken();
        
        // 跳转到登录界面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}