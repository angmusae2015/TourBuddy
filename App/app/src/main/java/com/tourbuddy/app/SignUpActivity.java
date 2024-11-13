package com.tourbuddy.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.tourbuddy.app.databinding.SiginupBinding;

public class SignUpActivity extends AppCompatActivity {
    private SiginupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SiginupBinding.inflate(getLayoutInflater());

        Button signUpButton = binding.signUpButton;

        signUpButton.setOnClickListener(new SignUpButtonClickListener());

        setContentView(binding.getRoot());
    }

    private void signUp(String id, String password) {
        // TODO: 서버와 통신해 회원가입하는 코드
    }

    // 회원 가입 버튼에 등록할 OnClickListener
    class SignUpButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextInputLayout idField = binding.idField;
            TextInputLayout passwordField = binding.passwordField;
            TextInputLayout passwordTeField = binding.passwordTe;

            String id = Util.getTextFromTextInputLayout(idField);
            String password = Util.getTextFromTextInputLayout(passwordField);
            String passwordTe = Util.getTextFromTextInputLayout(passwordTeField);

            // TODO: 토스트 알림으로 경고하는 방식을 텍스트 레이아웃의 속성 변경으로 대체할 예정
            Toast warningToast = new Toast(SignUpActivity.this);

            // 아이디 또는 비밀번호를 입력하지 않았을 경우
            if (id.isEmpty() || password.isEmpty()) {
                // 토스트 메시지로 경고
                String emptyFieldWarning = "아이디와 비밀번호를 모두 입력해주세요.";

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
        }
    }
}