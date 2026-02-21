package com.example.zealinkly_volunteer.utils;

public class Constants {
    // API基础URL
    // 开发环境: http://localhost:8080 (Android设备使用局域网IP)
    public static final String BASE_URL = "http://192.168.31.48:8080";
    
    // API端点
    public static final String API_LOGIN = "/api/auth/login";
    public static final String API_REGISTER_VOLUNTEER = "/api/auth/register/volunteer";
    public static final String API_LOGIN_BY_CARD = "/api/auth/login-by-card";
    
    public static final String API_TASKS_AVAILABLE = "/api/tasks/cooperation/available";
    public static final String API_TASKS_MY = "/api/tasks/cooperation/my-as-volunteer";
    public static final String API_TASK_DETAIL = "/api/tasks/cooperation/{taskId}";
    public static final String API_TASK_ACCEPT = "/api/tasks/cooperation/{taskId}/accept";
    public static final String API_TASK_START = "/api/tasks/cooperation/{taskId}/start";
    public static final String API_TASK_SUBMIT = "/api/tasks/cooperation/{taskId}/submit";
    public static final String API_TASK_APPEAL = "/api/tasks/cooperation/{taskId}/appeal";
    
    public static final String API_POINTS_TOTAL = "/api/points/total";
    public static final String API_POINTS_HISTORY = "/api/points/history";
    
    public static final String API_USER_INFO = "/api/user/info";
    
    public static final String API_NOTIFICATIONS = "/api/notifications";
    public static final String API_NOTIFICATIONS_UNREAD_COUNT = "/api/notifications/unread-count";
    public static final String API_NOTIFICATION_READ = "/api/notifications/{id}/read";
    public static final String API_NOTIFICATIONS_READ_ALL = "/api/notifications/read-all";
    
    public static final String API_ASR_RECOGNIZE = "/api/asr/recognize";
    
    public static final String API_FILE_UPLOAD = "/api/files/upload";
    public static final String API_FILE_UPLOAD_BASE64 = "/api/files/upload-base64";
    public static final String API_FILES_MY = "/api/files/my";
    public static final String API_FILE_DELETE = "/api/files/{fileId}";
    
    // 任务状态
    public static final String TASK_STATUS_PENDING = "PENDING";
    public static final String TASK_STATUS_CLAIMED = "CLAIMED";
    public static final String TASK_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String TASK_STATUS_SUBMITTED = "SUBMITTED";
    public static final String TASK_STATUS_COMPLETED = "COMPLETED";
    public static final String TASK_STATUS_CANCELLED = "CANCELLED";
    
    // 积分变动原因
    public static final String POINTS_REASON_TASK_REWARD = "TASK_REWARD";
    public static final String POINTS_REASON_TASK_COST = "TASK_COST";
    public static final String POINTS_REASON_GIFT_EXCHANGE = "GIFT_EXCHANGE";
    public static final String POINTS_REASON_ADJUSTMENT = "ADJUSTMENT";
    public static final String POINTS_REASON_MONTHLY_GRANT = "MONTHLY_GRANT";
    public static final String POINTS_REASON_ADMIN_GRANT = "ADMIN_GRANT";
    
    // 请求码
    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_GALLERY = 1002;
    public static final int REQUEST_CODE_AUDIO = 1003;
    
    // 权限请求码
    public static final int PERMISSION_CODE_CAMERA = 2001;
    public static final int PERMISSION_CODE_STORAGE = 2002;
    public static final int PERMISSION_CODE_RECORD_AUDIO = 2003;
}