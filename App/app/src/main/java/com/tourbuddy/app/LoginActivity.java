package com.tourbuddy.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.tourbuddy.app.databinding.LoginBinding;

public class LoginActivity extends AppCompatActivity {
    private LoginBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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

            // 로그인 화면에서 클릭한 경우 입력한 이메일과 비밀번호의 유효성 검정
            else if (state == 1) {
                this.validateAndLogin();
            }
        }

        /**
         * 로그인 버튼을 눌렀을 때 요소들의 애니메이션을 실행하는 메소드
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
         * 로그인 버튼을 눌렀을 때 입력 필드의 값을 검증하고 login 메소드를 호출해 로그인을 시도하는 메소드
         */
        private void validateAndLogin() {
            TextInputLayout emailField = binding.emailField;
            TextInputLayout passwordField = binding.passwordField;

            // 입력 필드에 띄운 오류 메시지 초기화
            emailField.setError(null);
            passwordField.setError(null);

            String email = Util.getTextFromTextInputLayout(emailField);
            String password = Util.getTextFromTextInputLayout(passwordField);

            // 이메일 입력 필드가 비었을 경우 필드에 오류 메시지 출력
            if (email.isEmpty()) {
                emailField.setError("이메일 주소를 입력하세요.");
                return;
            }

            // 비밀번호 입력 필드가 비었을 경우 필드에 오류 메시지 출력
            if (password.isEmpty()) {
                passwordField.setError("비밀번호를 입력하세요.");
                return;
            }

            login(email, password);
        }

        /**
         * 입력받은 ID와 비밀번호로 서버에 로그인을 시도하는 메소드.
         * @param email 사용자가 입력한 이메일 주소
         * @param password 사용자가 입력한 비밀번호
         */
        private void login(String email, String password) {
            // 로그인 태스크와 결과에 따른 콜백 함수를 정의하고 태스크를 실행함
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    // 로그인에 성공했을 때 호출할 콜백 함수
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    // 로그인에 실패했을 때 호출할 콜백 함수
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 자격 증명 관련 에러인 경우
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            handleInvalidCredentialsException(((FirebaseAuthInvalidCredentialsException) e).getErrorCode());
                        }
                        // 존재하지 않는 계정 에러인 경우
                        else if (e instanceof FirebaseAuthInvalidUserException) {
                            handleInvalidUserException();
                        }
                    }

                    /**
                     * FirebaseAuthInvalidCredentialsException 예외를 처리하는 메소드
                     * @param errorCode 발생한 에러 코드
                     */
                    private void handleInvalidCredentialsException(String errorCode) {
                        // 에러를 표시할 필드와 표시할 에러 메시지
                        TextInputLayout errorTarget;
                        String errorText;

                        // 입력한 이메일의 형식이 올바르지 않을 경우
                        switch (errorCode) {
                            case "ERROR_INVALID_EMAIL":
                                errorTarget = binding.emailField;
                                errorText = "올바르지 않은 이메일 형식입니다.";
                                break;
                            // 비밀번호가 올바르지 않을 경우
                            case "ERROR_WRONG_PASSWORD":
                                errorTarget = binding.passwordField;
                                errorText = "틀린 비밀번호입니다.";
                                break;
                            default:
                                errorTarget = binding.emailField;
                                errorText = "문제가 발생했습니다. 잠시 후 다시 시도하세요.";
                                break;
                        }

                        errorTarget.setError(errorText);
                    }

                    /**
                     * FirebaseAuthInvalidUserException 예외를 처리하는 메소드
                     */
                    private void handleInvalidUserException() {
                        // 에러를 표시할 필드와 표시할 에러 메시지
                        TextInputLayout errorTarget = binding.emailField;
                        String errorText = "존재하지 않는 계정입니다.";

                        errorTarget.setError(errorText);
                    }
                });
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