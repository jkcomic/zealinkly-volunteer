package com.example.zealinkly_volunteer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.zealinkly_volunteer.models.request.LoginRequest;
import com.example.zealinkly_volunteer.models.request.CardLoginRequest;
import com.example.zealinkly_volunteer.models.response.ApiResponse;
import com.example.zealinkly_volunteer.models.response.LoginResponse;
import com.example.zealinkly_volunteer.network.ApiClient;
import com.example.zealinkly_volunteer.network.ApiService;
import com.example.zealinkly_volunteer.network.TokenManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardLoginActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_GALLERY = 1002;
    private static final int PERMISSION_CAMERA = 2001;
    private static final int PERMISSION_STORAGE = 2002;
    
    private Button btnTakePhoto;
    private Button btnSelectPhoto;
    private Button btnLogin;
    private Button btnBack;
    private ImageView ivCardImage;
    
    private ApiService apiService;
    private String currentPhotoPath;
    private Bitmap cardBitmap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_login);
        
        // 初始化TokenManager
        TokenManager.init(this);
        
        // 初始化视图
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnSelectPhoto = findViewById(R.id.btn_select_photo);
        btnLogin = findViewById(R.id.btn_login);
        btnBack = findViewById(R.id.btn_back);
        ivCardImage = findViewById(R.id.iv_card_image);
        
        // 初始化API服务
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 设置返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 设置拍照按钮点击事件
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
        
        // 设置选择图片按钮点击事件
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });
        
        // 设置登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginByCard();
            }
        });
    }
    
    /**
     * 检查相机权限
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {
            takePhoto();
        }
    }
    
    /**
     * 检查存储权限
     */
    private void checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+，使用READ_MEDIA_IMAGES权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_STORAGE);
            } else {
                selectPhoto();
            }
        } else {
            // Android 12及以下，使用READ_EXTERNAL_STORAGE权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
            } else {
                selectPhoto();
            }
        }
    }
    
    /**
     * 拍照
     */
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "创建照片文件失败", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.zealinkly_volunteer.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }
    
    /**
     * 选择图片
     */
    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }
    
    /**
     * 创建图片文件
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                // 处理拍照结果
                cardBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ivCardImage.setImageBitmap(cardBitmap);
                Toast.makeText(this, "拍照成功", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_GALLERY) {
                // 处理选择图片结果
                Uri selectedImage = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    cardBitmap = BitmapFactory.decodeStream(imageStream);
                    ivCardImage.setImageBitmap(cardBitmap);
                    Toast.makeText(this, "选择图片成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "读取图片失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPhoto();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    Toast.makeText(this, "需要读取图片权限才能选择图片", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    /**
     * 卡片登录
     */
    private void loginByCard() {
        if (cardBitmap == null) {
            Toast.makeText(this, "请先拍摄或选择卡片照片", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示加载状态
        btnLogin.setEnabled(false);
        Toast.makeText(this, "正在登录...", Toast.LENGTH_SHORT).show();
        
        // 将Bitmap转换为Base64
        String cardImageBase64 = bitmapToBase64(cardBitmap);
        if (cardImageBase64 == null) {
            btnLogin.setEnabled(true);
            Toast.makeText(this, "图片转换失败", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 创建登录请求 - 使用JSONObject确保所有字段都被正确序列化
        try {
            // 创建一个JSONObject来存储请求参数 - 根据后端API要求
            org.json.JSONObject requestObject = new org.json.JSONObject();
            requestObject.put("imageBase64", cardImageBase64);
            requestObject.put("userType", "VOLUNTEER");
            
            // 将JSONObject转换为字符串
            String requestBody = requestObject.toString();
            
            // 创建一个RequestBody对象
            okhttp3.RequestBody body = okhttp3.RequestBody.create(
                    requestBody,
                    okhttp3.MediaType.parse("application/json; charset=utf-8")
            );
            
            // 创建一个请求
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://192.168.31.48:8080/api/auth/login-by-card")
                    .post(body)
                    .build();
            
            // 发送请求
            new okhttp3.OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnLogin.setEnabled(true);
                            
                            try {
                                // 检查响应是否成功
                                if (!response.isSuccessful()) {
                                    Toast.makeText(CardLoginActivity.this, "登录失败：服务器返回错误码 " + response.code(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                
                                // 尝试解析响应体
                                String responseBody = response.body().string();
                                if (responseBody == null || responseBody.isEmpty()) {
                                    Toast.makeText(CardLoginActivity.this, "登录失败：服务器返回空响应", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                
                                // 打印响应体，方便调试
                                android.util.Log.d("CardLoginActivity", "响应体: " + responseBody);
                                
                                // 尝试解析响应体
                                try {
                                    com.google.gson.Gson gson = new com.google.gson.Gson();
                                    com.google.gson.reflect.TypeToken<ApiResponse<LoginResponse>> typeToken = new com.google.gson.reflect.TypeToken<ApiResponse<LoginResponse>>() {};
                                    ApiResponse<LoginResponse> apiResponse = gson.fromJson(responseBody, typeToken.getType());
                                
                                    if (apiResponse != null) {
                                        if (apiResponse.isSuccess()) {
                                            // 登录成功，保存token
                                            try {
                                                LoginResponse loginResponse = apiResponse.getData();
                                                if (loginResponse == null) {
                                                    Toast.makeText(CardLoginActivity.this, "登录失败：返回数据为空", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                
                                                TokenManager tokenManager = TokenManager.getInstance();
                                                if (tokenManager != null) {
                                                    tokenManager.saveToken(
                                                            loginResponse.getToken(),
                                                            loginResponse.getUserId(),
                                                            loginResponse.getUserType()
                                                    );
                                                    
                                                    // 跳转到主界面
                                                    Intent intent = new Intent(CardLoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(CardLoginActivity.this, "TokenManager未初始化", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                String errorMessage = e.getMessage() != null ? e.getMessage() : "未知错误";
                                                Toast.makeText(CardLoginActivity.this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "登录失败，未知原因";
                                            Toast.makeText(CardLoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(CardLoginActivity.this, "登录失败，无法解析响应", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    String errorMessage = e.getMessage() != null ? e.getMessage() : "响应解析失败";
                                    Toast.makeText(CardLoginActivity.this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                String errorMessage = e.getMessage() != null ? e.getMessage() : "未知错误";
                                Toast.makeText(CardLoginActivity.this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnLogin.setEnabled(true);
                            e.printStackTrace();
                            String errorMessage = e.getMessage() != null ? e.getMessage() : "网络连接失败";
                            Toast.makeText(CardLoginActivity.this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
                btnLogin.setEnabled(true);
                String errorMessage = e.getMessage() != null ? e.getMessage() : "未知错误";
                Toast.makeText(this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
            }
    }
    
    /**
     * 将Bitmap转换为Base64，添加data URI前缀
     */
    private String bitmapToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String base64String = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
            // 添加data URI前缀
            return "data:image/jpeg;base64," + base64String;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}