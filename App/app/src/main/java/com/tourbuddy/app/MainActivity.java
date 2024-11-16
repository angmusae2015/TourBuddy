package com.tourbuddy.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tourbuddy.app.databinding.MainBinding;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private MainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if (o.getResultCode() == RESULT_OK)
                        setHome();
                });

        // 현재 로그인한 유저가 없을 경우
        if (user == null) {
            // 로그인 액티비티로 전환
            loginLauncher.launch(new Intent(MainActivity.this, LoginActivity.class));
        } else
            setHome();
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