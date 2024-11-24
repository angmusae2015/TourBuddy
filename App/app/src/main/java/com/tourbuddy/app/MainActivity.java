package com.tourbuddy.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tourbuddy.app.databinding.MainBinding;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    private MainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // 로그인 화면 액티비티로 전환하는 launcher
        ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    // 로그인 액티비티가 종료되면 홈 화면으로 전환하고 user 필드를 갱신함
                    setHome();
                    user = auth.getCurrentUser();
                });

        // 현재 로그인한 인증 정보가 캐시에 없을 경우
        if (user == null) {
            // 로그인 액티비티로 전환
            loginLauncher.launch(new Intent(MainActivity.this, LoginActivity.class));
        }
        // 현재 로그인한 인증 정보가 캐시에 있을 경우
        else {
            // 인증 정보를 갱신해 유효한지 확인
            user.reload()
                    // 캐시된 인증 정보가 유효할 경우
                    .addOnSuccessListener(reloadResult -> setHome())    // 홈 화면으로 전환
                    // 캐시된 인증 정보가 유효하지 않을 경우
                    .addOnFailureListener(exception -> {
                        // 로그아웃 후 로그인 액티비티로 전환
                        auth.signOut();
                        loginLauncher.launch(new Intent(MainActivity.this, LoginActivity.class));
                    });
        }
    }

    /**
     * 로그인이 완료되면 홈 화면으로 전환하고 각 탭의 화면을 표시하는 fragmentContainer에 홈 탭의 fragment를 채우는 메소드
     */
    private void setHome() {
        binding = MainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, HomeTabFragment.newInstance())
                .commit();
    }
}