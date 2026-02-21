package com.example.zealinkly_volunteer.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zealinkly_volunteer.R;
import com.example.zealinkly_volunteer.models.response.PointsHistoryResponse;

import java.util.List;

public class PointsHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<PointsHistoryResponse.PointsRecord> records;
    
    public PointsHistoryAdapter(Context context, List<PointsHistoryResponse.PointsRecord> records) {
        this.context = context;
        this.records = records;
    }
    
    @Override
    public int getCount() {
        return records != null ? records.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return records.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_points_record, parent, false);
            holder = new ViewHolder();
            holder.tvAmount = convertView.findViewById(R.id.tv_amount);
            holder.tvBalance = convertView.findViewById(R.id.tv_balance);
            holder.tvReason = convertView.findViewById(R.id.tv_reason);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        PointsHistoryResponse.PointsRecord record = records.get(position);
        if (record != null) {
            // 设置积分变动金额，正数显示为+，负数显示为-
            int amount = record.getAmount();
            String amountText = amount >= 0 ? "+" + amount : String.valueOf(amount);
            holder.tvAmount.setText(amountText);
            holder.tvAmount.setTextColor(amount >= 0 ? 
                context.getResources().getColor(android.R.color.holo_green_dark) : 
                context.getResources().getColor(android.R.color.holo_red_dark)
            );
            
            // 设置变动后余额
            holder.tvBalance.setText("余额：" + record.getBalanceAfter());
            
            // 设置变动原因
            String reasonDescription = record.getReasonDescription();
            if (reasonDescription != null && !reasonDescription.isEmpty()) {
                holder.tvReason.setText(reasonDescription);
            } else {
                holder.tvReason.setText(getReasonText(record.getReason()));
            }
            
            // 设置变动时间
            holder.tvDate.setText(formatDate(record.getCreatedAt()));
        }
        
        return convertView;
    }
    
    /**
     * 根据reason获取中文描述
     */
    private String getReasonText(String reason) {
        if (reason == null) return "未知原因";
        
        switch (reason) {
            case "TASK_REWARD": return "任务奖励";
            case "TASK_COST": return "任务消耗";
            case "GIFT_EXCHANGE": return "礼品兑换";
            case "ADJUSTMENT": return "管理员调整";
            case "MONTHLY_GRANT": return "月度发放";
            case "ADMIN_GRANT": return "管理员发放";
            default: return reason;
        }
    }
    
    /**
     * 格式化日期时间
     */
    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        
        // 简化处理，只显示日期和时间部分
        // 输入格式：2026-02-09T12:00:00+08:00
        // 输出格式：2026-02-09 12:00:00
        try {
            return dateStr.replace("T", " ").split("\\+")[0];
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    private static class ViewHolder {
        TextView tvAmount;
        TextView tvBalance;
        TextView tvReason;
        TextView tvDate;
    }
}
