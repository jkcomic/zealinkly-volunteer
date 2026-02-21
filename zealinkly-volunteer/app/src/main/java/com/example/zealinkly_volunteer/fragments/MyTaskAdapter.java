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
import com.example.zealinkly_volunteer.models.request.TaskSubmitRequest;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.TaskDetailResponse;
import com.example.zealinkly_volunteer.models.response.TaskListResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;

import java.util.List;

public class MyTaskAdapter extends BaseAdapter {
    private Context context;
    private List<TaskListResponse.TaskItem> tasks;
    private ApiService apiService;
    private OnTaskActionListener onTaskActionListener;
    
    /**
     * 任务操作回调接口
     */
    public interface OnTaskActionListener {
        void onTaskAction(int taskId, String action);
    }
    
    /**
     * 设置任务操作监听器
     * @param listener 监听器
     */
    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.onTaskActionListener = listener;
    }
    
    public MyTaskAdapter(Context context, List<TaskListResponse.TaskItem> tasks) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_task, parent, false);
            holder = new ViewHolder();
            holder.tvTaskContent = convertView.findViewById(R.id.tv_task_content);
            holder.tvElderName = convertView.findViewById(R.id.tv_elder_name);
            holder.tvPointsReward = convertView.findViewById(R.id.tv_points_reward);
            holder.tvCreatedAt = convertView.findViewById(R.id.tv_created_at);
            holder.tvStatus = convertView.findViewById(R.id.tv_status);
            holder.btnStartTask = convertView.findViewById(R.id.btn_start_task);
            holder.btnSubmitTask = convertView.findViewById(R.id.btn_submit_task);
            holder.btnViewDetail = convertView.findViewById(R.id.btn_view_detail);
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
            holder.tvPointsReward.setText("积分奖励：" + task.getPointsReward());
            holder.tvCreatedAt.setText(task.getCreatedAt());
            
            // 设置任务状态
            String status = task.getStatus();
            holder.tvStatus.setText("状态：" + getStatusText(status));
            
            // 根据任务状态显示不同的操作按钮
            updateActionButtons(holder, status);
            
            // 显式设置按钮颜色为#A4D1F2
            holder.btnStartTask.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A4D1F2")));
            holder.btnSubmitTask.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A4D1F2")));
            holder.btnViewDetail.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A4D1F2")));
            
            // 设置开始服务按钮点击事件
            holder.btnStartTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTask(task.getId());
                }
            });
            
            // 设置提交完成按钮点击事件
            holder.btnSubmitTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitTask(task.getId());
                }
            });
            
            // 设置查看详情按钮点击事件
            holder.btnViewDetail.setOnClickListener(new View.OnClickListener() {
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
    
    /**
     * 根据任务状态显示不同的操作按钮
     */
    private void updateActionButtons(ViewHolder holder, String status) {
        // 重置所有按钮的可见性
        holder.btnStartTask.setVisibility(View.GONE);
        holder.btnSubmitTask.setVisibility(View.GONE);
        holder.btnViewDetail.setVisibility(View.GONE);
        
        // 根据状态显示按钮
        if (status != null) {
            switch (status) {
                case "CLAIMED": // 已接单
                    holder.btnStartTask.setVisibility(View.VISIBLE);
                    holder.btnViewDetail.setVisibility(View.VISIBLE);
                    break;
                case "IN_PROGRESS": // 进行中
                    holder.btnSubmitTask.setVisibility(View.VISIBLE);
                    holder.btnViewDetail.setVisibility(View.VISIBLE);
                    break;
                case "SUBMITTED": // 已提交
                case "COMPLETED": // 已完成
                case "CANCELLED": // 已取消
                    holder.btnViewDetail.setVisibility(View.VISIBLE);
                    break;
            }
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
    
    /**
     * 开始服务
     */
    private void startTask(int taskId) {
        Toast.makeText(context, "正在开始服务...", Toast.LENGTH_SHORT).show();
        
        apiService.startTask(taskId).enqueue(new retrofit2.Callback<ApiResponse<TaskDetailResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, retrofit2.Response<ApiResponse<TaskDetailResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<TaskDetailResponse> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            Toast.makeText(context, "服务已开始！", Toast.LENGTH_SHORT).show();
                            // 通知刷新任务列表
                            if (onTaskActionListener != null) {
                                onTaskActionListener.onTaskAction(taskId, "start");
                            }
                        } else {
                            Toast.makeText(context, "开始服务失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "开始服务失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "开始服务失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, Throwable t) {
                Toast.makeText(context, "开始服务失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 提交完成
     */
    private void submitTask(int taskId) {
        // 这里简化处理，实际应用中需要上传凭证
        TaskSubmitRequest request = new TaskSubmitRequest("任务已完成", null);
        
        Toast.makeText(context, "正在提交完成...", Toast.LENGTH_SHORT).show();
        
        apiService.submitTask(taskId, request).enqueue(new retrofit2.Callback<ApiResponse<TaskDetailResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, retrofit2.Response<ApiResponse<TaskDetailResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<TaskDetailResponse> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            Toast.makeText(context, "任务已提交，等待老人确认！", Toast.LENGTH_SHORT).show();
                            // 通知刷新任务列表
                            if (onTaskActionListener != null) {
                                onTaskActionListener.onTaskAction(taskId, "submit");
                            }
                        } else {
                            Toast.makeText(context, "提交失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "提交失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "提交失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<TaskDetailResponse>> call, Throwable t) {
                Toast.makeText(context, "提交失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private static class ViewHolder {
        TextView tvTaskContent;
        TextView tvElderName;
        TextView tvPointsReward;
        TextView tvCreatedAt;
        TextView tvStatus;
        Button btnStartTask;
        Button btnSubmitTask;
        Button btnViewDetail;
    }
}