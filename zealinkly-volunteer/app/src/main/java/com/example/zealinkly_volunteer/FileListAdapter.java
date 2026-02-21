package com.example.zealinkly_volunteer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zealinkly_volunteer.models.response.FileListResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;

import java.util.List;

public class FileListAdapter extends BaseAdapter {
    private Context context;
    private List<FileListResponse.FileItem> files;
    private ApiService apiService;
    private OnFileDeleteListener onFileDeleteListener;
    
    /**
     * 文件删除成功回调接口
     */
    public interface OnFileDeleteListener {
        void onFileDeleted(int fileId);
    }
    
    /**
     * 设置文件删除成功监听器
     * @param listener 监听器
     */
    public void setOnFileDeleteListener(OnFileDeleteListener listener) {
        this.onFileDeleteListener = listener;
    }
    
    public FileListAdapter(Context context, List<FileListResponse.FileItem> files, OnFileDeleteListener listener) {
        this.context = context;
        this.files = files;
        this.onFileDeleteListener = listener;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }
    
    @Override
    public int getCount() {
        return files != null ? files.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return files.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
            holder = new ViewHolder();
            holder.tvFilename = convertView.findViewById(R.id.tv_filename);
            holder.tvFileSize = convertView.findViewById(R.id.tv_file_size);
            holder.tvFileType = convertView.findViewById(R.id.tv_file_type);
            holder.tvCreatedAt = convertView.findViewById(R.id.tv_created_at);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        FileListResponse.FileItem file = files.get(position);
        if (file != null) {
            holder.tvFilename.setText(file.getOriginalFilename());
            holder.tvFileSize.setText(formatFileSize(file.getFileSize()));
            holder.tvFileType.setText(file.getFileType());
            holder.tvCreatedAt.setText(file.getCreatedAt());
            
            // 设置删除按钮点击事件
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFile(file.getId());
                }
            });
        }
        
        return convertView;
    }
    
    private static class ViewHolder {
        TextView tvFilename;
        TextView tvFileSize;
        TextView tvFileType;
        TextView tvCreatedAt;
        Button btnDelete;
    }
    
    /**
     * 格式化文件大小
     */
    private String formatFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return (fileSize / 1024) + " KB";
        } else {
            return (fileSize / (1024 * 1024)) + " MB";
        }
    }
    
    /**
     * 删除文件
     */
    private void deleteFile(int fileId) {
        // 显示加载状态
        Toast.makeText(context, "正在删除文件...", Toast.LENGTH_SHORT).show();
        
        // 调用删除文件API
        apiService.deleteFile(fileId).enqueue(new retrofit2.Callback<com.example.zealinkly_volunteer.models.response.ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.zealinkly_volunteer.models.response.ApiResponse<Void>> call, retrofit2.Response<com.example.zealinkly_volunteer.models.response.ApiResponse<Void>> response) {
                try {
                    if (response.isSuccessful()) {
                        com.example.zealinkly_volunteer.models.response.ApiResponse<Void> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            // 删除成功
                            Toast.makeText(context, "文件删除成功", Toast.LENGTH_SHORT).show();
                            
                            // 通知监听器
                            if (onFileDeleteListener != null) {
                                onFileDeleteListener.onFileDeleted(fileId);
                            }
                        } else {
                            // API错误
                            Toast.makeText(context, "文件删除失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // HTTP错误
                        Toast.makeText(context, "文件删除失败，错误码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "文件删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<com.example.zealinkly_volunteer.models.response.ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(context, "文件删除失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
