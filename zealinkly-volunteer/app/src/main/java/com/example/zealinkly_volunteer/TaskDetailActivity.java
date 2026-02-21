package com.example.zealinkly_volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zealinkly_volunteer.models.request.TaskAppealRequest;
import com.example.zealinkly_volunteer.models.request.TaskSubmitRequest;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.TaskDetailResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {
    private TextView tvTaskContent;
    private TextView tvElderInfo;
    private TextView tvVolunteerInfo;
    private TextView tvPointsReward;
    private TextView tvStatus;
    private TextView tvCreatedAt;
    private TextView tvEvidences;
    private Button btnBack;
    private Button btnAccept;
    private Button btnStart;
    private Button btnSubmit;
    private Button btnAppeal;
    private LinearLayout llTaskActions;
    private ApiService apiService;
    private int taskId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        
        // 初始化TokenManager
        TokenManager.init(this);
        
        // 获取任务ID
        taskId = getIntent().getIntExtra("taskId", 0);
        if (taskId == 0) {
            Toast.makeText(this, "无效的任务ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 初始化视图
        tvTaskContent = findViewById(R.id.tv_task_content);
        tvElderInfo = findViewById(R.id.tv_elder_info);
        tvVolunteerInfo = findViewById(R.id.tv_volunteer_info);
        tvPointsReward = findViewById(R.id.tv_points_reward);
        tvStatus = findViewById(R.id.tv_status);
        tvCreatedAt = findViewById(R.id.tv_created_at);
        tvEvidences = findViewById(R.id.tv_evidences);
        btnBack = findViewById(R.id.btn_back);
        btnAccept = findViewById(R.id.btn_accept);
        btnStart = findViewById(R.id.btn_start);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAppeal = findViewById(R.id.btn_appeal);
        llTaskActions = findViewById(R.id.ll_task_actions);
        
        // 初始化API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 显式设置按钮颜色为#A4D1F2
        btnStart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A4D1F2")));
        btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A4D1F2")));
        btnAppeal.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A4D1F2")));
        
        // 设置返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 设置任务操作按钮点击事件
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTask();
            }
        });
        
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
            }
        });
        
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(TaskDetailActivity.this, "点击了提交完成按钮，准备跳转到提交页面", Toast.LENGTH_SHORT).show();
                    // 跳转到任务提交页面
                    Intent intent = new Intent(TaskDetailActivity.this, TaskSubmitActivity.class);
                    intent.putExtra("taskId", taskId);
                    Toast.makeText(TaskDetailActivity.this, "任务ID：" + taskId, Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 100);
                } catch (Exception e) {
                    Toast.makeText(TaskDetailActivity.this, "跳转到提交页面失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        btnAppeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示申诉对话框
                showAppealDialog();
            }
        });
        
        // 加载任务详情
        loadTaskDetail();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // 任务提交成功，重新加载任务详情
            loadTaskDetail();
        }
    }
    
    /**
     * 加载任务详情
     */
    private void loadTaskDetail() {
        Toast.makeText(this, "加载任务详情...", Toast.LENGTH_SHORT).show();
        
        apiService.getTaskDetail(taskId).enqueue(new retrofit2.Callback<ApiResponse<TaskDetailResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, retrofit2.Response<ApiResponse<TaskDetailResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<TaskDetailResponse> apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.isSuccess()) {
                                TaskDetailResponse taskDetail = apiResponse.getData();
                                if (taskDetail != null) {
                                    // 显示任务详情
                                    displayTaskDetail(taskDetail);
                                    // 更新任务操作按钮
                                    updateTaskActions(taskDetail.getStatus());
                                } else {
                                    Toast.makeText(TaskDetailActivity.this, "任务详情为空", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(TaskDetailActivity.this, "获取任务详情失败：" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "获取任务详情失败：无响应数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "获取任务详情失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(TaskDetailActivity.this, "获取任务详情失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this, "获取任务详情失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 显示任务详情
     */
    private void displayTaskDetail(TaskDetailResponse taskDetail) {
        // 任务内容
        tvTaskContent.setText(taskDetail.getContent());
        
        // 老人信息
        if (taskDetail.getElder() != null) {
            StringBuilder elderInfo = new StringBuilder();
            elderInfo.append("姓名：").append(taskDetail.getElder().getRealName()).append("\n");
            if (taskDetail.getElder().getPhone() != null) {
                elderInfo.append("电话：").append(taskDetail.getElder().getPhone()).append("\n");
            }
            if (taskDetail.getElder().getAddress() != null) {
                elderInfo.append("地址：").append(taskDetail.getElder().getAddress());
            }
            tvElderInfo.setText(elderInfo.toString());
        }
        
        // 志愿者信息
        if (taskDetail.getVolunteer() != null) {
            tvVolunteerInfo.setText("姓名：" + taskDetail.getVolunteer().getRealName());
        } else {
            tvVolunteerInfo.setText("暂无");
        }
        
        // 积分奖励
        tvPointsReward.setText("" + taskDetail.getPointsReward());
        
        // 任务状态
        tvStatus.setText(getStatusText(taskDetail.getStatus()));
        
        // 创建时间
        tvCreatedAt.setText(taskDetail.getCreatedAt());
        
        // 凭证信息
        if (taskDetail.getEvidences() != null && !taskDetail.getEvidences().isEmpty()) {
            StringBuilder evidences = new StringBuilder();
            for (TaskDetailResponse.Evidence evidence : taskDetail.getEvidences()) {
                evidences.append(evidence.getFileUrl()).append("\n");
            }
            tvEvidences.setText(evidences.toString());
        } else {
            tvEvidences.setText("暂无凭证");
        }
    }
    
    /**
     * 更新任务操作按钮
     */
    private void updateTaskActions(String status) {
        // 隐藏所有按钮
        llTaskActions.setVisibility(View.GONE);
        btnAccept.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        btnAppeal.setVisibility(View.GONE);
        
        if (status == null) return;
        
        llTaskActions.setVisibility(View.VISIBLE);
        
        switch (status) {
            case "PENDING":
                btnAccept.setVisibility(View.VISIBLE);
                break;
            case "CLAIMED":
                btnStart.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                btnAppeal.setVisibility(View.VISIBLE);
                break;
            case "IN_PROGRESS":
                btnSubmit.setVisibility(View.VISIBLE);
                btnAppeal.setVisibility(View.VISIBLE);
                break;
            case "SUBMITTED":
            case "COMPLETED":
            case "CANCELLED":
                // 已完成或已取消的任务不需要操作按钮
                break;
        }
    }
    
    /**
     * 接取任务
     */
    private void acceptTask() {
        apiService.acceptTask(taskId).enqueue(new retrofit2.Callback<ApiResponse<TaskDetailResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, retrofit2.Response<ApiResponse<TaskDetailResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<TaskDetailResponse> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            Toast.makeText(TaskDetailActivity.this, "接单成功", Toast.LENGTH_SHORT).show();
                            loadTaskDetail();
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "接单失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "接单失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(TaskDetailActivity.this, "接单失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this, "接单失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 开始服务
     */
    private void startTask() {
        apiService.startTask(taskId).enqueue(new retrofit2.Callback<ApiResponse<TaskDetailResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, retrofit2.Response<ApiResponse<TaskDetailResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<TaskDetailResponse> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            Toast.makeText(TaskDetailActivity.this, "已开始服务", Toast.LENGTH_SHORT).show();
                            loadTaskDetail();
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "开始服务失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "开始服务失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(TaskDetailActivity.this, "开始服务失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this, "开始服务失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 提交申诉
     */
    private void appealTask(TaskAppealRequest request) {
        apiService.appealTask(taskId, request).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<Void>> call, retrofit2.Response<ApiResponse<Void>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<Void> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            Toast.makeText(TaskDetailActivity.this, "申诉已提交，请等待管理员处理", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "申诉提交失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "申诉提交失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(TaskDetailActivity.this, "申诉提交失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this, "申诉提交失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 显示申诉对话框
     */
    private void showAppealDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_appeal, null);
        builder.setView(dialogView);
        
        final android.app.AlertDialog dialog = builder.create();
        
        // 获取对话框中的控件
        EditText etAppealContent = dialogView.findViewById(R.id.et_appeal_content);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        
        // 设置取消按钮点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        // 设置提交按钮点击事件
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etAppealContent.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(TaskDetailActivity.this, "请输入申诉内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 创建申诉请求
                TaskAppealRequest request = new TaskAppealRequest();
                request.setContent(content);
                
                // 提交申诉
                appealTask(request);
                
                // 关闭对话框
                dialog.dismiss();
            }
        });
        
        // 显示对话框
        dialog.show();
        
        // 设置对话框的宽度
        android.view.WindowManager.LayoutParams layoutParams = new android.view.WindowManager.LayoutParams();
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
    }
    
    /**
     * 获取任务状态的中文文本
     */
    private String getStatusText(String status) {
        if (status == null) return "未知";
        
        switch (status) {
            case "PENDING": return "待接取";
            case "CLAIMED": return "已接单";
            case "IN_PROGRESS": return "进行中";
            case "SUBMITTED": return "已提交";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }
}