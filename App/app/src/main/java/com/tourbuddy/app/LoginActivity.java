package com.tourbuddy.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.tourbuddy.app.databinding.LoginBinding;

public class LoginActivity extends AppCompatActivity {
    private LoginBinding binding;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());

        Button loginButton = binding.loginButton;
        Button signInButton = binding.signUpButton;

        loginButton.setOnClickListener(new LoginButtonClickListener());
        signInButton.setOnClickListener(new SignInButtonClickListener());

        setContentView(binding.getRoot());
    }

    // 로그인 버튼에 등록할 OnClickListener
    class LoginButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 초기 화면에서 클릭한 경우
            if (state == 0) {
                this.animate();
                state = 1;
            }

            // 로그인 화면에서 클릭한 경우
            else if (state == 1) {
                int loginResult;
                TextInputLayout idField = binding.idField;
                TextInputLayout passwordField = binding.passwordField;

                String id = idField.getEditText().getText().toString();
                String password = passwordField.getEditText().getText().toString();

                if (id.isEmpty()) {
                    idField.setError("ID를 입력하세요.");
                    return;
                }

                if (password.isEmpty()) {
                    passwordField.setError("비밀번호를 입력하세요.");
                    return;
                }

                loginResult = login(id, password);

                // TODO: 로그인 결과에 따라 실행할 코드
            }

        }

        /**
         * 로그인 버튼을 눌렀을 때 요소들의 애니메이션을 실행하는 함수
         */
        private void animate() {
            LinearLayout buttonContainer = binding.buttonContainer;
            LinearLayout textFieldContainer = binding.textFieldContainer;

            // 버튼이 아래쪽으로 이동하는 애니메이션
            buttonContainer.animate()
                .translationY(250)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    // 버튼의 이동이 끝난 후 실행할 입력 필드 등장 애니메이션
                    textFieldContainer.setVisibility(View.VISIBLE);
                    textFieldContainer.setAlpha(0);
                    textFieldContainer.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start();
                }).start();
        }

        /**
         * 입력받은 ID와 비밀번호로 서버에 로그인을 시도하는 메소드. 결과에 따라 0, 1, 2 세 가지 값을 반환함
         * @param id 사용자가 입력한 ID
         * @param password 사용자가 입력한 비밀번호
         * @return 성공적으로 로그인한 경우 0, 존재하지 않는 ID일 경우 1, 비밀번호가 틀렸을 경우 2를 반환함
         */
        private int login(String id, String password) {
            // TODO: 서버와 통신해 로그인하는 코드

            return 0;
        }
    }

    // 회원 가입 버튼에 등록할 OnClickListener
    class SignInButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // TODO: 로그인 버튼 비활성화
            // TODO: 아이디 입력, 아이디 중복 확인 버튼, 비번 입력, 비번 확인 입력 활성화
        }
    }
}