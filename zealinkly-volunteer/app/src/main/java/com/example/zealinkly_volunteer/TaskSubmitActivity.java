package com.example.zealinkly_volunteer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.zealinkly_volunteer.models.request.TaskSubmitRequest;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.FileUploadResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TaskSubmitActivity extends AppCompatActivity {
    private EditText etNote;
    private Button btnAddEvidence;
    private Button btnSubmit;
    private Button btnBack;
    private LinearLayout llEvidences;
    private ApiService apiService;
    private int taskId;
    private List<String> evidenceUrls = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private String currentPhotoPath;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_submit);
        
        // 初始化TokenManager
        try {
            TokenManager.init(this);
        } catch (Exception e) {
            Toast.makeText(this, "TokenManager初始化失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        // 获取任务ID
        try {
            taskId = getIntent().getIntExtra("taskId", 0);
            Toast.makeText(this, "任务ID：" + taskId, Toast.LENGTH_SHORT).show();
            if (taskId == 0) {
                Toast.makeText(this, "无效的任务ID", Toast.LENGTH_SHORT).show();
                // 注释掉finish()，确保即使任务ID为0也能显示界面
                // finish();
                // return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "获取任务ID失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        // 初始化视图
        try {
            etNote = findViewById(R.id.et_note);
            btnAddEvidence = findViewById(R.id.btn_add_evidence);
            btnSubmit = findViewById(R.id.btn_submit);
            btnBack = findViewById(R.id.btn_back);
            llEvidences = findViewById(R.id.ll_evidences);
        } catch (Exception e) {
            Toast.makeText(this, "初始化视图失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        // 初始化API服务
        try {
            apiService = ApiClient.getClient().create(ApiService.class);
        } catch (Exception e) {
            Toast.makeText(this, "初始化API服务失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        // 设置返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 设置添加凭证按钮点击事件
        btnAddEvidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskSubmitActivity.this, "点击了添加凭证按钮", Toast.LENGTH_SHORT).show();
                // 打开图片选择器
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE_PICK);
                } catch (Exception e) {
                    Toast.makeText(TaskSubmitActivity.this, "打开图片选择器失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // 设置提交按钮点击事件
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTask();
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && currentPhotoPath != null) {
                File file = new File(currentPhotoPath);
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.zealinkly_volunteer.fileprovider", file);
                uploadImage(imageUri);
            }
        }
    }
    
    /**
     * 上传图片
     */
    private void uploadImage(Uri imageUri) {
        try {
            // 获取文件路径
            String filePath = getRealPathFromURI(imageUri);
            File file = new File(filePath);
            
            // 创建RequestBody
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            
            // 上传文件
            apiService.uploadFile(body, "TASK", taskId).enqueue(new retrofit2.Callback<ApiResponse<FileUploadResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse<FileUploadResponse>> call, retrofit2.Response<ApiResponse<FileUploadResponse>> response) {
                    try {
                        if (response.isSuccessful()) {
                            ApiResponse<FileUploadResponse> apiResponse = response.body();
                            if (apiResponse != null && apiResponse.isSuccess()) {
                                FileUploadResponse uploadResponse = apiResponse.getData();
                                if (uploadResponse != null && uploadResponse.getFileUrl() != null) {
                                    // 添加到凭证列表
                                    evidenceUrls.add(uploadResponse.getFileUrl());
                                    // 显示凭证
                                    addEvidenceView(uploadResponse.getFileUrl());
                                    Toast.makeText(TaskSubmitActivity.this, "凭证上传成功", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(TaskSubmitActivity.this, "凭证上传失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TaskSubmitActivity.this, "凭证上传失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(TaskSubmitActivity.this, "凭证上传失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(retrofit2.Call<ApiResponse<FileUploadResponse>> call, Throwable t) {
                    Toast.makeText(TaskSubmitActivity.this, "凭证上传失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "图片处理失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 添加凭证视图
     */
    private void addEvidenceView(String fileUrl) {
        TextView textView = new TextView(this);
        textView.setText(fileUrl);
        textView.setPadding(8, 8, 8, 8);
        llEvidences.addView(textView);
    }
    
    /**
     * 提交任务
     */
    private void submitTask() {
        String note = etNote.getText().toString().trim();
        
        // 创建提交请求
        TaskSubmitRequest request = new TaskSubmitRequest();
        request.setNote(note);
        request.setEvidences(evidenceUrls);
        
        // 提交任务
        apiService.submitTask(taskId, request).enqueue(new retrofit2.Callback<ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>> call, retrofit2.Response<ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            Toast.makeText(TaskSubmitActivity.this, "任务提交成功，等待老人确认", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(TaskSubmitActivity.this, "任务提交失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TaskSubmitActivity.this, "任务提交失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(TaskSubmitActivity.this, "任务提交失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>> call, Throwable t) {
                Toast.makeText(TaskSubmitActivity.this, "任务提交失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 获取URI的真实路径
     */
    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}
