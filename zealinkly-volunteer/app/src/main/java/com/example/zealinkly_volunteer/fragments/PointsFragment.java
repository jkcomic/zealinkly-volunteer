package com.example.zealinkly_volunteer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.zealinkly_volunteer.R;
import com.example.zealinkly_volunteer.fragments.PointsHistoryAdapter;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.PointsTotalResponse;
import com.example.zealinkly_volunteer.models.response.PointsHistoryResponse;
import com.example.zealinkly_volunteer.models.response.TaskListResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointsFragment extends Fragment {
    private TextView tvPointsTotal;
    private ListView lvPointsHistory;
    private Button btnRefresh;
    private ApiService apiService;
    private List<PointsHistoryResponse.PointsRecord> pointsRecords;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_points, container, false);
        
        // 确保TokenManager已初始化
        if (getActivity() != null) {
            TokenManager.init(getActivity());
        }
        
        // 初始化视图
        tvPointsTotal = view.findViewById(R.id.tv_points_total);
        lvPointsHistory = view.findViewById(R.id.lv_points_history);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        
        // 初始化API服务
        try {
            apiService = ApiClient.getClient().create(ApiService.class);
        } catch (Exception e) {
            Toast.makeText(getContext(), "API服务初始化失败，请检查网络连接后重试", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        
        // 设置刷新按钮点击事件
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 禁用刷新按钮，防止重复点击
                btnRefresh.setEnabled(false);
                btnRefresh.setText("刷新中...");
                loadPointsData();
                // 2秒后重新启用刷新按钮
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnRefresh.setEnabled(true);
                        btnRefresh.setText("刷新");
                    }
                }, 2000);
            }
        });
        
        // 加载积分数据
        loadPointsData();
        
        return view;
    }
    
    private void loadPointsData() {
        // 显示加载状态
        if (getContext() != null) {
            Toast.makeText(getContext(), "加载中...", Toast.LENGTH_SHORT).show();
        }
        
        // 检查API服务状态
        if (apiService == null) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "API服务未初始化，请检查网络连接后重试", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        
        // 检查TokenManager状态
        TokenManager tokenManager = TokenManager.getInstance();
        if (tokenManager == null || tokenManager.getToken() == null) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        
        // 加载积分总数
        apiService.getPointsTotal().enqueue(new Callback<ApiResponse<PointsTotalResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PointsTotalResponse>> call, Response<ApiResponse<PointsTotalResponse>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<PointsTotalResponse> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            PointsTotalResponse pointsTotalResponse = apiResponse.getData();
                            if (pointsTotalResponse != null) {
                                tvPointsTotal.setText(String.valueOf(pointsTotalResponse.getTotal()));
                            } else {
                                tvPointsTotal.setText("0");
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "积分数据异常", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            tvPointsTotal.setText("0");
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "加载积分失败：" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        tvPointsTotal.setText("0");
                        if (getContext() != null) {
                            String errorMsg = "加载积分失败";
                            if (response.code() == 401) {
                                errorMsg = "登录已过期，请重新登录";
                            } else if (response.code() == 403) {
                                errorMsg = "权限不足";
                            } else if (response.code() == 404) {
                                errorMsg = "服务不存在";
                            } else if (response.code() == 500) {
                                errorMsg = "服务器内部错误";
                            }
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    tvPointsTotal.setText("0");
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "加载积分失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<PointsTotalResponse>> call, Throwable t) {
                tvPointsTotal.setText("0");
                if (getContext() != null) {
                    String errorMessage = "网络错误，请检查网络连接";
                    if (t.getMessage() != null && t.getMessage().contains("ConnectException")) {
                        errorMessage = "无法连接到服务器，请检查网络连接";
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                t.printStackTrace();
            }
        });
        
        // 同时加载积分历史和任务列表，为0积分的任务模拟积分流水记录
        loadPointsHistoryWithTasks();
    }
    
    /**
     * 加载积分历史并为0积分的任务模拟积分流水记录
     */
    private void loadPointsHistoryWithTasks() {
        // 加载积分历史
        apiService.getPointsHistory().enqueue(new Callback<ApiResponse<List<PointsHistoryResponse.PointsRecord>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PointsHistoryResponse.PointsRecord>>> call, Response<ApiResponse<List<PointsHistoryResponse.PointsRecord>>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<PointsHistoryResponse.PointsRecord>> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            pointsRecords = apiResponse.getData();
                            
                            // 加载用户的任务列表
                            loadTasksAndSimulateZeroPointsRecords();
                        } else {
                            lvPointsHistory.setAdapter(null);
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "加载积分历史失败：" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        lvPointsHistory.setAdapter(null);
                        if (getContext() != null) {
                            String errorMsg = "加载积分历史失败";
                            if (response.code() == 401) {
                                errorMsg = "登录已过期，请重新登录";
                            } else if (response.code() == 403) {
                                errorMsg = "权限不足";
                            } else if (response.code() == 404) {
                                errorMsg = "服务不存在";
                            } else if (response.code() == 500) {
                                errorMsg = "服务器内部错误";
                            }
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    lvPointsHistory.setAdapter(null);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "加载积分历史失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<PointsHistoryResponse.PointsRecord>>> call, Throwable t) {
                lvPointsHistory.setAdapter(null);
                if (getContext() != null) {
                    String errorMessage = "网络错误，请检查网络连接";
                    if (t.getMessage() != null && t.getMessage().contains("ConnectException")) {
                        errorMessage = "无法连接到服务器，请检查网络连接";
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                t.printStackTrace();
            }
        });
    }
    
    /**
     * 加载任务列表并为0积分的任务模拟积分流水记录
     */
    private void loadTasksAndSimulateZeroPointsRecords() {
        apiService.getMyTasks().enqueue(new Callback<ApiResponse<List<TaskListResponse.TaskItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TaskListResponse.TaskItem>>> call, Response<ApiResponse<List<TaskListResponse.TaskItem>>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<TaskListResponse.TaskItem>> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            List<TaskListResponse.TaskItem> tasks = apiResponse.getData();
                            
                            // 为0积分的任务模拟积分流水记录
                            if (tasks != null && !tasks.isEmpty()) {
                                simulateZeroPointsRecords(tasks);
                            }
                            
                            // 显示积分记录
                            displayPointsRecords();
                        } else {
                            // 任务加载失败，直接显示积分记录
                            displayPointsRecords();
                        }
                    } else {
                        // 任务加载失败，直接显示积分记录
                        displayPointsRecords();
                    }
                } catch (Exception e) {
                    // 任务加载失败，直接显示积分记录
                    displayPointsRecords();
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<TaskListResponse.TaskItem>>> call, Throwable t) {
                // 任务加载失败，直接显示积分记录
                displayPointsRecords();
                t.printStackTrace();
            }
        });
    }
    
    /**
     * 为0积分的任务模拟积分流水记录
     */
    private void simulateZeroPointsRecords(List<TaskListResponse.TaskItem> tasks) {
        if (pointsRecords == null) {
            pointsRecords = new java.util.ArrayList<>();
        }
        
        // 收集已有的任务ID，避免重复
        java.util.Set<Integer> existingTaskIds = new java.util.HashSet<>();
        for (PointsHistoryResponse.PointsRecord record : pointsRecords) {
            if (record.getTaskId() != null) {
                existingTaskIds.add(record.getTaskId());
            }
        }
        
        // 为0积分的已完成任务模拟积分流水记录
        for (TaskListResponse.TaskItem task : tasks) {
            if (task.getStatus() != null && task.getStatus().equals("COMPLETED") && 
                task.getPointsReward() == 0 && 
                !existingTaskIds.contains(task.getId())) {
                
                // 创建模拟的积分流水记录
                PointsHistoryResponse.PointsRecord zeroPointsRecord = new PointsHistoryResponse.PointsRecord();
                zeroPointsRecord.setId(-task.getId()); // 使用负的任务ID作为模拟记录的ID
                zeroPointsRecord.setAmount(0);
                zeroPointsRecord.setBalanceAfter(Integer.parseInt(tvPointsTotal.getText().toString()));
                zeroPointsRecord.setReason("TASK_REWARD");
                zeroPointsRecord.setReasonDescription("任务奖励");
                zeroPointsRecord.setTaskId(task.getId());
                zeroPointsRecord.setExchangeId(null);
                zeroPointsRecord.setCreatedAt(task.getCreatedAt());
                
                // 添加到积分记录列表
                pointsRecords.add(zeroPointsRecord);
                
                // 模拟创建通知
                simulateNotification(task);
            }
        }
        
        // 按时间倒序排序
        if (!pointsRecords.isEmpty()) {
            java.util.Collections.sort(pointsRecords, new java.util.Comparator<PointsHistoryResponse.PointsRecord>() {
                @Override
                public int compare(PointsHistoryResponse.PointsRecord r1, PointsHistoryResponse.PointsRecord r2) {
                    return r2.getCreatedAt().compareTo(r1.getCreatedAt());
                }
            });
        }
    }
    
    /**
     * 模拟创建通知
     */
    private void simulateNotification(TaskListResponse.TaskItem task) {
        // 这里可以实现模拟通知的逻辑
        // 例如，使用SharedPreferences存储模拟的通知
        // 或者直接更新通知界面
        
        // 通知用户有新的任务完成消息
        if (getContext() != null) {
            Toast.makeText(getContext(), "任务已完成！", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 显示积分记录
     */
    private void displayPointsRecords() {
        if (pointsRecords != null && !pointsRecords.isEmpty()) {
            // 创建并设置适配器
            PointsHistoryAdapter adapter = new PointsHistoryAdapter(getContext(), pointsRecords);
            lvPointsHistory.setAdapter(adapter);
            if (getContext() != null) {
                Toast.makeText(getContext(), "加载到 " + pointsRecords.size() + " 条积分记录", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 清空列表
            lvPointsHistory.setAdapter(null);
            if (getContext() != null) {
                Toast.makeText(getContext(), "暂无积分记录", Toast.LENGTH_SHORT).show();
            }
        }
    }
}