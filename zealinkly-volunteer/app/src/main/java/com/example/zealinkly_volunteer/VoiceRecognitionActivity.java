package com.example.zealinkly_volunteer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.zealinkly_volunteer.models.request.AudioRecognizeRequest;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.AudioRecognizeResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;
import com.example.zealinkly_volunteer.utils.VoiceRecognitionHelper;

public class VoiceRecognitionActivity extends AppCompatActivity {
    private TextView tvResult;
    private Button btnRecord;
    private Button btnBack;
    private VoiceRecognitionHelper voiceHelper;
    private ApiService apiService;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);
        
        // 初始化TokenManager
        TokenManager.init(this);
        
        // 初始化视图
        tvResult = findViewById(R.id.tv_result);
        btnRecord = findViewById(R.id.btn_record);
        btnBack = findViewById(R.id.btn_back);
        
        // 初始化API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 初始化语音识别助手
        voiceHelper = new VoiceRecognitionHelper(this);
        
        // 检查录音权限
        checkAudioPermission();
        
        // 设置返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 设置录音按钮点击事件
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    // 停止录制
                    stopRecording();
                } else {
                    // 开始录制
                    startRecording();
                }
            }
        });
    }
    
    /**
     * 检查录音权限
     */
    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.RECORD_AUDIO}, 
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限授予成功
                Toast.makeText(this, "录音权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                // 权限授予失败
                Toast.makeText(this, "需要录音权限才能使用语音识别功能", Toast.LENGTH_SHORT).show();
                btnRecord.setEnabled(false);
            }
        }
    }
    
    /**
     * 开始录制音频
     */
    private void startRecording() {
        if (voiceHelper.startRecording()) {
            isRecording = true;
            btnRecord.setText("停止录制");
            tvResult.setText("正在录制...");
            Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "开始录制失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 停止录制音频并发送到服务器进行识别
     */
    private void stopRecording() {
        String filePath = voiceHelper.stopRecording();
        if (filePath != null) {
            isRecording = false;
            btnRecord.setText("开始录制");
            tvResult.setText("正在识别...");
            Toast.makeText(this, "正在识别...", Toast.LENGTH_SHORT).show();
            
            // 将音频文件转换为Base64
            String audioBase64 = voiceHelper.audioFileToBase64(filePath);
            if (audioBase64 != null) {
                // 发送到服务器进行识别
                recognizeAudio(audioBase64);
            } else {
                Toast.makeText(this, "音频转换失败", Toast.LENGTH_SHORT).show();
                tvResult.setText("识别失败：音频转换失败");
            }
        } else {
            Toast.makeText(this, "停止录制失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 发送音频到服务器进行识别
     */
    private void recognizeAudio(String audioBase64) {
        AudioRecognizeRequest request = new AudioRecognizeRequest(audioBase64);
        
        apiService.recognizeAudio(request).enqueue(new retrofit2.Callback<ApiResponse<AudioRecognizeResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<AudioRecognizeResponse>> call, retrofit2.Response<ApiResponse<AudioRecognizeResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        ApiResponse<AudioRecognizeResponse> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.isSuccess()) {
                            AudioRecognizeResponse recognizeResponse = apiResponse.getData();
                            if (recognizeResponse != null && recognizeResponse.getText() != null) {
                                tvResult.setText("识别结果：" + recognizeResponse.getText());
                                Toast.makeText(VoiceRecognitionActivity.this, "识别成功", Toast.LENGTH_SHORT).show();
                            } else {
                                tvResult.setText("识别失败：无识别结果");
                            }
                        } else {
                            tvResult.setText("识别失败：" + (apiResponse != null ? apiResponse.getMessage() : "未知错误"));
                        }
                    } else {
                        tvResult.setText("识别失败，错误码：" + response.code());
                    }
                } catch (Exception e) {
                    tvResult.setText("识别失败：" + e.getMessage());
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<ApiResponse<AudioRecognizeResponse>> call, Throwable t) {
                tvResult.setText("识别失败：" + t.getMessage());
                Toast.makeText(VoiceRecognitionActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
