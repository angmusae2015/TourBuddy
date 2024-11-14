package com.tourbuddy.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.tourbuddy.app.databinding.SiginupBinding;

public class SignUpActivity extends AppCompatActivity {
    private SiginupBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        binding = SiginupBinding.inflate(getLayoutInflater());

        Button signUpButton = binding.signUpButton;

        signUpButton.setOnClickListener(new SignUpButtonClickListener());

        setContentView(binding.getRoot());
    }

    // 회원 가입 버튼에 등록할 OnClickListener
    class SignUpButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            validateAndSignUp();
        }

        /**
         * 회원 가입 버튼을 눌렀을 때 입력 필드의 값을 검증하고 signUp 메소드를 호출해 회원 가입을 시도하는 메소드
         */
        private void validateAndSignUp() {
            TextInputLayout emailField = binding.emailField;
            TextInputLayout passwordField = binding.passwordField;
            TextInputLayout passwordTeField = binding.passwordTe;

            String email = Util.getTextFromTextInputLayout(emailField);
            String password = Util.getTextFromTextInputLayout(passwordField);
            String passwordTe = Util.getTextFromTextInputLayout(passwordTeField);

            emailField.setError(null);
            passwordField.setError(null);
            passwordTeField.setError(null);

            if (email.isEmpty()) {
                emailField.setError("이메일 주소를 입력하세요.");
                return;
            }

            if (password.isEmpty()) {
                passwordField.setError("비밀번호를 입력하세요.");
                return;
            }

            if (!password.equals(passwordTe)) {
                passwordTeField.setError("비밀번호가 일치하지 않습니다.");
                return;
            }

            signUp(email, password);
        }

        /**
         * 주어진 이메일 주소와 비밀번호로 계정을 생성함
         * @param email 가입할 계정의 이메일 주소
         * @param password 가입할 계정의 비밀번호
         */
        private void signUp(String email, String password) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 비밀번호 강도가 너무 약한 경우 (6자리 이하)
                        if (e instanceof FirebaseAuthWeakPasswordException) {
                            handleWeakPasswordException();
                        }
                        // 자격 증명 관련 에러인 경우
                        else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            handleInvalidCredentialsException(((FirebaseAuthInvalidCredentialsException) e).getErrorCode());
                        }
                        // 이미 존재하는 계정인 경우
                        else if (e instanceof FirebaseAuthUserCollisionException) {
                            handleUserCollisionException();
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

                        if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                            errorTarget = binding.emailField;
                            errorText = "올바르지 않은 이메일 형식입니다.";
                        } else {
                            errorTarget = binding.emailField;
                            errorText = "문제가 발생했습니다. 잠시 후 다시 시도하세요.";
                        }

                        errorTarget.setError(errorText);
                    }

                    /**
                     * FirebaseAuthUserCollisionException 예외를 처리하는 메소드
                     */
                    private void handleUserCollisionException() {
                        TextInputLayout errorTarget = binding.emailField;
                        String errorText = "이미 존재하는 계정입니다.";

                        errorTarget.setError(errorText);
                    }

                    /**
                     * FirebaseAuthWeakPasswordException 예외를 처리하는 메소드
                     */
                    private void handleWeakPasswordException() {
                        TextInputLayout errorTarget = binding.passwordField;
                        String errorText = "최소 6자리 이상의 비밀번호를 입력하세요.";

                        errorTarget.setError(errorText);
                    }
                });
        }
    }
}