package com.example.zealinkly_volunteer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.zealinkly_volunteer.R;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.TaskListResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTasksFragment extends Fragment {
    private ListView lvMyTasks;
    private Button btnRefresh;
    private ApiService apiService;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tasks, container, false);
        
        // 确保TokenManager已初始化
        if (getActivity() != null) {
            try {
                TokenManager.init(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 初始化视图
        lvMyTasks = view.findViewById(R.id.lv_my_tasks);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        
        // 初始化API服务
        try {
            apiService = ApiClient.getClient().create(ApiService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 设置刷新按钮点击事件
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMyTasks();
            }
        });
        
        // 加载我的任务列表
        loadMyTasks();
        
        return view;
    }
    
    private void loadMyTasks() {
        // 显示加载状态
        Toast.makeText(getContext(), "加载中...", Toast.LENGTH_SHORT).show();
        
        try {
            // 检查API服务状态
            if (apiService == null) {
                return;
            }
            
            // 检查TokenManager状态
            TokenManager tokenManager = TokenManager.getInstance();
            if (tokenManager == null) {
                return;
            }
            
            // 检查网络连接状态（仅作为参考，不影响实际API请求）
            try {
                java.net.InetAddress address = java.net.InetAddress.getByName("192.168.31.48");
                boolean reachable = address.isReachable(5000);
                // 静默处理网络连接状态，不显示给用户
            } catch (Exception e) {
                // 静默处理网络连接检查失败，因为isReachable()在某些网络环境下不可靠
            }
            // 直接尝试API请求，不依赖网络连接检查结果
            
            // 创建API调用对象
            Call<ApiResponse<List<TaskListResponse.TaskItem>>> call = null;
            try {
                call = apiService.getMyTasks();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            
            // 执行网络请求
            call.enqueue(new Callback<ApiResponse<List<TaskListResponse.TaskItem>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<TaskListResponse.TaskItem>>> call, Response<ApiResponse<List<TaskListResponse.TaskItem>>> response) {
                    try {
                        // 尝试读取响应体
                        String responseBody = "";
                        try {
                            if (response.body() != null) {
                                // 成功响应，使用body
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                responseBody = gson.toJson(response.body());
                            } else if (response.errorBody() != null) {
                                // 错误响应，使用errorBody
                                responseBody = response.errorBody().string();
                            } else {
                                responseBody = "无响应体";
                            }
                        } catch (Exception e) {
                            responseBody = "读取响应体失败：" + e.getMessage();
                        }
                        
                        // 尝试解析响应数据
                        try {
                            // 直接使用响应体
                            ApiResponse<List<TaskListResponse.TaskItem>> apiResponse = response.body();
                            
                            if (apiResponse != null) {
                                if (apiResponse.isSuccess()) {
                                    List<TaskListResponse.TaskItem> tasks = apiResponse.getData();
                                    if (tasks != null) {
                                        // 对任务列表进行排序：进行中 > 已接单 > 已提交 > 已完成
                                        java.util.Collections.sort(tasks, new java.util.Comparator<TaskListResponse.TaskItem>() {
                                            @Override
                                            public int compare(TaskListResponse.TaskItem task1, TaskListResponse.TaskItem task2) {
                                                String status1 = task1.getStatus();
                                                String status2 = task2.getStatus();
                                                
                                                // 定义状态优先级
                                                java.util.Map<String, Integer> statusPriority = new java.util.HashMap<>();
                                                statusPriority.put("IN_PROGRESS", 1);  // 最高优先级
                                                statusPriority.put("CLAIMED", 2);
                                                statusPriority.put("SUBMITTED", 3);
                                                statusPriority.put("COMPLETED", 4);
                                                statusPriority.put("CANCELLED", 5);
                                                statusPriority.put("PENDING", 6);
                                                
                                                // 获取优先级值，默认最低
                                                int priority1 = statusPriority.getOrDefault(status1, 10);
                                                int priority2 = statusPriority.getOrDefault(status2, 10);
                                                
                                                // 按优先级排序
                                                return Integer.compare(priority1, priority2);
                                            }
                                        });
                                        
                                        // 创建任务适配器并显示任务列表
                                        MyTaskAdapter adapter = new MyTaskAdapter(getContext(), tasks);
                                        // 设置任务操作监听器
                                        adapter.setOnTaskActionListener(new MyTaskAdapter.OnTaskActionListener() {
                                            @Override
                                            public void onTaskAction(int taskId, String action) {
                                                // 任务操作后刷新任务列表
                                                loadMyTasks();
                                            }
                                        });
                                        lvMyTasks.setAdapter(adapter);
                                    } else {
                                        Toast.makeText(getContext(), "暂无任务", Toast.LENGTH_SHORT).show();
                                        // 清空列表
                                        lvMyTasks.setAdapter(null);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "API错误：" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    // 清空列表
                                    lvMyTasks.setAdapter(null);
                                }
                            } else {
                                // 如果body为null，尝试从responseBody字符串解析
                                try {
                                    com.google.gson.Gson gson = new com.google.gson.Gson();
                                    // 正确指定泛型类型，包含TaskListResponse.TaskItem
                                    java.lang.reflect.Type listType = com.google.gson.reflect.TypeToken.getParameterized(java.util.List.class, TaskListResponse.TaskItem.class).getType();
                                    java.lang.reflect.Type apiResponseType = com.google.gson.reflect.TypeToken.getParameterized(ApiResponse.class, listType).getType();
                                    ApiResponse<List<TaskListResponse.TaskItem>> parsedResponse = gson.fromJson(responseBody, apiResponseType);
                                    if (parsedResponse != null) {
                                        if (parsedResponse.isSuccess()) {
                                            List<TaskListResponse.TaskItem> tasks = parsedResponse.getData();
                                            if (tasks != null) {
                                                // 对任务列表进行排序：进行中 > 已接单 > 已提交 > 已完成
                                                java.util.Collections.sort(tasks, new java.util.Comparator<TaskListResponse.TaskItem>() {
                                                    @Override
                                                    public int compare(TaskListResponse.TaskItem task1, TaskListResponse.TaskItem task2) {
                                                        String status1 = task1.getStatus();
                                                        String status2 = task2.getStatus();
                                                        
                                                        // 定义状态优先级
                                                        java.util.Map<String, Integer> statusPriority = new java.util.HashMap<>();
                                                        statusPriority.put("IN_PROGRESS", 1);  // 最高优先级
                                                        statusPriority.put("CLAIMED", 2);
                                                        statusPriority.put("SUBMITTED", 3);
                                                        statusPriority.put("COMPLETED", 4);
                                                        statusPriority.put("CANCELLED", 5);
                                                        statusPriority.put("PENDING", 6);
                                                        
                                                        // 获取优先级值，默认最低
                                                        int priority1 = statusPriority.getOrDefault(status1, 10);
                                                        int priority2 = statusPriority.getOrDefault(status2, 10);
                                                        
                                                        // 按优先级排序
                                                        return Integer.compare(priority1, priority2);
                                                    }
                                                });
                                                
                                                // 创建任务适配器并显示任务列表
                                                MyTaskAdapter adapter = new MyTaskAdapter(getContext(), tasks);
                                                // 设置任务操作监听器
                                                adapter.setOnTaskActionListener(new MyTaskAdapter.OnTaskActionListener() {
                                                    @Override
                                                    public void onTaskAction(int taskId, String action) {
                                                        // 任务操作后刷新任务列表
                                                        loadMyTasks();
                                                    }
                                                });
                                                lvMyTasks.setAdapter(adapter);
                                            } else {
                                                Toast.makeText(getContext(), "暂无任务", Toast.LENGTH_SHORT).show();
                                                // 清空列表
                                                lvMyTasks.setAdapter(null);
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "API错误：" + parsedResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                            // 清空列表
                                            lvMyTasks.setAdapter(null);
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "无法解析响应数据", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<List<TaskListResponse.TaskItem>>> call, Throwable t) {
                    Toast.makeText(getContext(), "网络请求失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}