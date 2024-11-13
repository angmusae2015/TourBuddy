package com.tourbuddy.app;

import com.google.android.material.textfield.TextInputLayout;

public class Util {
    public static String getTextFromTextInputLayout(TextInputLayout layout) {
        return layout.getEditText().getText().toString();
    }
}
