package com.tourbuddy.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        // TODO: 로그인 성공 후 실행할 코드
                    }
                }
            });

        if (!isLoggedIn()) {
            // 로그인 액티비티로 전환
            loginLauncher.launch(new Intent(MainActivity.this, LoginActivity.class));
        }

        // 로그인이 완료되면 앱 실행 화면으로 전환
        setContentView(R.layout.activity_main);
    }

    /**
     * 앱을 실행했을 때 로그인한 상태인지 확인하는 메소드
     *
     * @return 로그인 여부
     */
    private boolean isLoggedIn() {
        // TODO: 로그인 상태 확인 코드 구현

        return false;
    }
}