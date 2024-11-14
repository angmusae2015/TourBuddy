package com.tourbuddy.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

    /**
     * 주어진 이메일 주소와 비밀번호로 계정을 생성함
     * @param email 가입할 계정의 이메일 주소
     * @param password 가입할 계정의 비밀번호
     */
    private void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast resultToast = new Toast(SignUpActivity.this);

                    if (task.isSuccessful()) {
                        resultToast.setText("회원가입에 성공했습니다.");
                        resultToast.show();

                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });
    }

    // 회원 가입 버튼에 등록할 OnClickListener
    class SignUpButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextInputLayout emailField = binding.emailField;
            TextInputLayout passwordField = binding.passwordField;
            TextInputLayout passwordTeField = binding.passwordTe;

            String email = Util.getTextFromTextInputLayout(emailField);
            String password = Util.getTextFromTextInputLayout(passwordField);
            String passwordTe = Util.getTextFromTextInputLayout(passwordTeField);

            // TODO: 토스트 알림으로 경고하는 방식을 텍스트 레이아웃의 속성 변경으로 대체할 예정
            Toast warningToast = new Toast(SignUpActivity.this);

            // 아이디 또는 비밀번호를 입력하지 않았을 경우
            if (email.isEmpty() || password.isEmpty()) {
                // 토스트 메시지로 경고
                String emptyFieldWarning = "이메일과 비밀번호를 모두 입력해주세요.";

                warningToast.setText(emptyFieldWarning);
                warningToast.show();

                return;
            }

            // 비밀번호 다시 입력이 올바르게 되지 않았을 경우
            if (!password.equals(passwordTe)) {
                // 토스트 메시지로 경고
                String passwordNotMatchWarning = "비밀번호가 동일하지 않습니다.";

                warningToast.setText(passwordNotMatchWarning);
                warningToast.show();

                return;
            }

            // 주어진 이메일과 비밀번호로 회원가입
            signUp(email, password);
        }
    }
}