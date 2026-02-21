package com.example.zealinkly_volunteer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.FileListResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import java.util.List;

public class FileManagerActivity extends AppCompatActivity {
    private ListView lvFiles;
    private Button btnRefresh;
    private Button btnBack;
    private TextView tvEmpty;
    private ApiService apiService;
    private FileListAdapter fileAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        
        // 初始化TokenManager
        TokenManager.init(this);
        
        // 初始化视图
        lvFiles = findViewById(R.id.lv_files);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnBack = findViewById(R.id.btn_back);
        tvEmpty = findViewById(R.id.tv_empty);
        
        // 初始化API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 设置返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 设置刷新按钮点击事件
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMyFiles();
            }
        });
        
        // 加载文件列表
        loadMyFiles();
    }
    
    /**
     * 加载我的文件列表
     */
    private void loadMyFiles() {
        Toast.makeText(this, "加载文件列表...", Toast.LENGTH_SHORT).show();
        
        apiService.getMyFiles().enqueue(new retrofit2.Callback<ApiResponse<List<FileListResponse.FileItem>>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<List<FileListResponse.FileItem>>> call, retrofit2.Response<ApiResponse<List<FileListResponse.FileItem>>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<List<FileListResponse.FileItem>> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            List<FileListResponse.FileItem> files = apiResponse.getData();
                            if (files != null && !files.isEmpty()) {
                                // 显示文件列表
                                fileAdapter = new FileListAdapter(FileManagerActivity.this, files, new FileListAdapter.OnFileDeleteListener() {
                                    @Override
                                    public void onFileDeleted(int fileId) {
                                        // 文件删除成功后刷新列表
                                        loadMyFiles();
                                    }
                                });
                                lvFiles.setAdapter(fileAdapter);
                                tvEmpty.setVisibility(View.GONE);
                                lvFiles.setVisibility(View.VISIBLE);
                            } else {
                                // 无文件
                                tvEmpty.setVisibility(View.VISIBLE);
                                lvFiles.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(FileManagerActivity.this, "加载文件失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FileManagerActivity.this, "加载文件失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(FileManagerActivity.this, "加载文件失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<List<FileListResponse.FileItem>>> call, Throwable t) {
                Toast.makeText(FileManagerActivity.this, "加载文件失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
