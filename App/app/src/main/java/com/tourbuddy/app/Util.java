package com.tourbuddy.app;

import com.google.android.material.textfield.TextInputLayout;

public class Util {
    /**
     * TextInputLayout 뷰에 입력한 텍스트를 반환하는 메소드
     * @param layout 텍스트를 불러올 TextInputLayout 뷰
     * @return 해당 TextInputLayout 뷰에 입력한 텍스트
     */
    public static String getTextFromTextInputLayout(TextInputLayout layout) {
        return layout.getEditText().getText().toString();
    }
}
