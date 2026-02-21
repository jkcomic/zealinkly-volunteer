package com.example.zealinkly_volunteer.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zealinkly_volunteer.R;
import com.example.zealinkly_volunteer.models.response.TaskListResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<TaskListResponse.TaskItem> tasks;
    private ApiService apiService;
    private OnTaskAcceptListener onTaskAcceptListener;
    
    /**
     * 接单成功回调接口
     */
    public interface OnTaskAcceptListener {
        void onTaskAccepted(int taskId);
    }
    
    /**
     * 设置接单成功监听器
     * @param listener 监听器
     */
    public void setOnTaskAcceptListener(OnTaskAcceptListener listener) {
        this.onTaskAcceptListener = listener;
    }
    
    public TaskAdapter(Context context, List<TaskListResponse.TaskItem> tasks) {
        this.context = context;
        this.tasks = tasks;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }
    
    @Override
    public int getCount() {
        return tasks != null ? tasks.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();
            holder.tvTaskContent = convertView.findViewById(R.id.tv_task_content);
            holder.tvElderName = convertView.findViewById(R.id.tv_elder_name);
            holder.tvTaskStatus = convertView.findViewById(R.id.tv_task_status);
            holder.tvPointsReward = convertView.findViewById(R.id.tv_points_reward);
            holder.tvCreatedAt = convertView.findViewById(R.id.tv_created_at);
            holder.btnAcceptTask = convertView.findViewById(R.id.btn_accept_task);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        TaskListResponse.TaskItem task = tasks.get(position);
        if (task != null) {
            holder.tvTaskContent.setText(task.getContent());
            if (task.getElder() != null) {
                holder.tvElderName.setText("发布人：" + task.getElder().getRealName());
            }
            
            // 显示任务状态
            String status = task.getStatus();
            holder.tvTaskStatus.setText(getStatusText(status));
            holder.tvTaskStatus.setTextColor(getStatusColor(status));
            
            // 显示积分奖励
            holder.tvPointsReward.setText("积分奖励：" + task.getPointsReward());
            holder.tvCreatedAt.setText(task.getCreatedAt());
            
            // 根据任务状态设置接单按钮
            if ("PENDING".equals(status)) {
                holder.btnAcceptTask.setVisibility(View.VISIBLE);
                holder.btnAcceptTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptTask(task.getId());
                    }
                });
            } else {
                holder.btnAcceptTask.setVisibility(View.GONE);
            }
            
            // 设置任务项点击事件（查看详情）
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到任务详情页面
                    android.content.Intent intent = new android.content.Intent(context, com.example.zealinkly_volunteer.TaskDetailActivity.class);
                    intent.putExtra("taskId", task.getId());
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).startActivity(intent);
                    } else if (context instanceof android.content.Context) {
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
        
        return convertView;
    }
    
    private static class ViewHolder {
        TextView tvTaskContent;
        TextView tvElderName;
        TextView tvTaskStatus;
        TextView tvPointsReward;
        TextView tvCreatedAt;
        Button btnAcceptTask;
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
    
    /**
     * 获取任务状态对应的颜色
     */
    private int getStatusColor(String status) {
        if (status == null) return android.graphics.Color.GRAY;
        
        switch (status) {
            case "PENDING": return android.graphics.Color.parseColor("#4CAF50");
            case "CLAIMED": return android.graphics.Color.parseColor("#FF9800");
            case "IN_PROGRESS": return android.graphics.Color.parseColor("#2196F3");
            case "SUBMITTED": return android.graphics.Color.parseColor("#9C27B0");
            case "COMPLETED": return android.graphics.Color.parseColor("#607D8B");
            case "CANCELLED": return android.graphics.Color.parseColor("#F44336");
            default: return android.graphics.Color.GRAY;
        }
    }
    
    private void acceptTask(int taskId) {
        // 显示加载状态
        Toast.makeText(context, "正在接单...", Toast.LENGTH_SHORT).show();
        
        // 调用接单API
        apiService.acceptTask(taskId).enqueue(new retrofit2.Callback<com.example.zealinkly_volunteer.models.response.ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.zealinkly_volunteer.models.response.ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>> call, retrofit2.Response<com.example.zealinkly_volunteer.models.response.ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>> response) {
                try {
                    // 检查响应状态
                    if (response.isSuccessful()) {
                        com.example.zealinkly_volunteer.models.response.ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse> apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.isSuccess()) {
                                // 接单成功
                                Toast.makeText(context, "接单成功！", Toast.LENGTH_SHORT).show();
                                
                                // 刷新任务列表
                                if (onTaskAcceptListener != null) {
                                    onTaskAcceptListener.onTaskAccepted(taskId);
                                }
                                
                                if (context instanceof android.app.Activity) {
                                    ((android.app.Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 刷新UI
                                        }
                                    });
                                }
                            } else {
                                // API错误
                                Toast.makeText(context, "接单失败：" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "接单失败：无响应数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // HTTP错误
                        Toast.makeText(context, "接单失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "接单失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<com.example.zealinkly_volunteer.models.response.ApiResponse<com.example.zealinkly_volunteer.models.response.TaskDetailResponse>> call, Throwable t) {
                Toast.makeText(context, "接单失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}