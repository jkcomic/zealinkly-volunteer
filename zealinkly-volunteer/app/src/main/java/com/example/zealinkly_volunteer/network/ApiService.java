package com.example.zealinkly_volunteer.network;

import com.example.zealinkly_volunteer.models.request.LoginRequest;
import com.example.zealinkly_volunteer.models.request.RegisterVolunteerRequest;
import com.example.zealinkly_volunteer.models.request.TaskSubmitRequest;
import com.example.zealinkly_volunteer.models.request.TaskAppealRequest;
import com.example.zealinkly_volunteer.models.request.UpdateUserInfoRequest;
import com.example.zealinkly_volunteer.models.request.AudioRecognizeRequest;
import com.example.zealinkly_volunteer.models.request.UploadBase64Request;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.LoginResponse;
import com.example.zealinkly_volunteer.models.response.TaskListResponse;
import com.example.zealinkly_volunteer.models.response.TaskDetailResponse;
import com.example.zealinkly_volunteer.models.response.PointsTotalResponse;
import com.example.zealinkly_volunteer.models.response.PointsHistoryResponse;
import com.example.zealinkly_volunteer.models.response.UserInfoResponse;
import com.example.zealinkly_volunteer.models.response.AudioRecognizeResponse;
import com.example.zealinkly_volunteer.models.response.FileUploadResponse;
import com.example.zealinkly_volunteer.models.response.FileListResponse;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PATCH;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

public interface ApiService {
    // 认证授权
    @POST("/api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("/api/auth/register/volunteer")
    Call<ApiResponse<LoginResponse>> registerVolunteer(@Body RegisterVolunteerRequest request);
    
    @POST("/api/auth/login-by-card")
    Call<ApiResponse<LoginResponse>> loginByCard(@Body Object request);
    
    // 任务管理
    @GET("/api/tasks/cooperation/available")
    Call<ApiResponse<List<TaskListResponse.TaskItem>>> getAvailableTasks();
    
    @GET("/api/tasks/cooperation/my-as-volunteer")
    Call<ApiResponse<List<TaskListResponse.TaskItem>>> getMyTasks();
    
    @GET("/api/tasks/cooperation/{taskId}")
    Call<ApiResponse<TaskDetailResponse>> getTaskDetail(@Path("taskId") int taskId);
    
    @POST("/api/tasks/cooperation/{taskId}/accept")
    Call<ApiResponse<TaskDetailResponse>> acceptTask(@Path("taskId") int taskId);
    
    @POST("/api/tasks/cooperation/{taskId}/start")
    Call<ApiResponse<TaskDetailResponse>> startTask(@Path("taskId") int taskId);
    
    @POST("/api/tasks/cooperation/{taskId}/submit")
    Call<ApiResponse<TaskDetailResponse>> submitTask(@Path("taskId") int taskId, @Body TaskSubmitRequest request);
    
    @POST("/api/tasks/cooperation/{taskId}/appeal")
    Call<ApiResponse<Void>> appealTask(@Path("taskId") int taskId, @Body TaskAppealRequest request);
    
    // 积分管理
    @GET("/api/points/total")
    Call<ApiResponse<PointsTotalResponse>> getPointsTotal();
    
    @GET("/api/points/history")
    Call<ApiResponse<List<PointsHistoryResponse.PointsRecord>>> getPointsHistory();
    
    // 个人信息
    @GET("/api/user/info")
    Call<ApiResponse<UserInfoResponse>> getUserInfo();
    
    @PUT("/api/user/info")
    Call<ApiResponse<UserInfoResponse>> updateUserInfo(@Body UpdateUserInfoRequest request);
    
    // 语音识别
    @POST("/api/asr/recognize")
    Call<ApiResponse<AudioRecognizeResponse>> recognizeAudio(@Body AudioRecognizeRequest request);
    
    // 文件管理
    @Multipart
    @POST("/api/files/upload")
    Call<ApiResponse<FileUploadResponse>> uploadFile(@Part MultipartBody.Part file, 
                                                     @Part("relatedType") String relatedType, 
                                                     @Part("relatedId") int relatedId);
    
    @POST("/api/files/upload-base64")
    Call<ApiResponse<FileUploadResponse>> uploadBase64(@Body UploadBase64Request request);
    
    @GET("/api/files/my")
    Call<ApiResponse<List<FileListResponse.FileItem>>> getMyFiles();
    
    @DELETE("/api/files/{fileId}")
    Call<ApiResponse<Void>> deleteFile(@Path("fileId") int fileId);
}