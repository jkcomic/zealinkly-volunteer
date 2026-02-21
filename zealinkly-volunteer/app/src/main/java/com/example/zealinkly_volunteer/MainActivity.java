package com.example.zealinkly_volunteer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.zealinkly_volunteer.databinding.ActivityMainBinding;
import com.example.zealinkly_volunteer.fragments.TaskHallFragment;
import com.example.zealinkly_volunteer.fragments.MyTasksFragment;
import com.example.zealinkly_volunteer.fragments.PointsFragment;
import com.example.zealinkly_volunteer.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 初始化TokenManager
        com.example.zealinkly_volunteer.network.TokenManager.init(this);
        
        // 检查登录状态
        try {
            com.example.zealinkly_volunteer.network.TokenManager tokenManager = com.example.zealinkly_volunteer.network.TokenManager.getInstance();
            if (!tokenManager.isLoggedIn()) {
                // 用户未登录，跳转到登录界面
                android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } catch (Exception e) {
            // TokenManager未初始化，跳转到登录界面
            android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        // 初始化默认显示任务大厅Fragment
        replaceFragment(new TaskHallFragment());
        
        // 设置底部导航栏点击事件
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_task_hall) {
                replaceFragment(new TaskHallFragment());
                return true;
            } else if (itemId == R.id.navigation_my_tasks) {
                replaceFragment(new MyTasksFragment());
                return true;
            } else if (itemId == R.id.navigation_points) {
                replaceFragment(new PointsFragment());
                return true;
            } else if (itemId == R.id.navigation_profile) {
                replaceFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }
    
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}