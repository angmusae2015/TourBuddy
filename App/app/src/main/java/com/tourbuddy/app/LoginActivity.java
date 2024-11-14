package com.tourbuddy.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.tourbuddy.app.databinding.LoginBinding;

public class LoginActivity extends AppCompatActivity {
    private LoginBinding binding;
    private int state;

    // 회원가입 액티비티를 실행하는 Launcher
    private ActivityResultLauncher<Intent> signupLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());

        signupLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    // 회원 가입 성공 시
                    if (o.getResultCode() == RESULT_OK) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });

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
                TextInputLayout emailField = binding.emailField;
                TextInputLayout passwordField = binding.passwordField;

                String email = Util.getTextFromTextInputLayout(emailField);
                String password = Util.getTextFromTextInputLayout(passwordField);

                if (email.isEmpty()) {
                    emailField.setError("이메일 주소를 입력하세요.");
                    return;
                }

                if (password.isEmpty()) {
                    passwordField.setError("비밀번호를 입력하세요.");
                    return;
                }
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
         * 입력받은 ID와 비밀번호로 서버에 로그인을 시도하는 메소드.
         * @param email 사용자가 입력한 이메일 주소
         * @param password 사용자가 입력한 비밀번호
         */
        private void login(String email, String password) {
            // TODO: 서버와 통신해 로그인하는 코드
        }
    }

    // 회원 가입 버튼에 등록할 OnClickListener
    class SignInButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 회원가입 액티비티로 전환
            signupLauncher.launch(new Intent(LoginActivity.this, SignUpActivity.class));
        }
    }
}