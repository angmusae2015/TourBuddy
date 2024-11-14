package com.tourbuddy.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.tourbuddy.app.databinding.ActivityMainBinding;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 버튼 클릭 리스너 설정
        binding.loginButton.setOnClickListener(v -> {
            // 버튼 내려가는 애니메이션 로드 및 적용
            Animation moveDownAnim = AnimationUtils.loadAnimation(this, R.anim.move_down_animation);
            binding.loginField.startAnimation(moveDownAnim);

            // 이미지가 보이도록 설정하고 페이드 인 애니메이션 적용
            binding.textField.setVisibility(View.VISIBLE); // 이미지 보이게 설정
            Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);
            binding.textField.startAnimation(fadeInAnim);
        });
    }
}