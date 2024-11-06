package com.tourbuddy.app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (!isLoggedIn()) {
            // TODO: 로그인 액티비티로 전환하는 코드
        }

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