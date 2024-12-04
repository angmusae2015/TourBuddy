package com.tourbuddy.app;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourbuddy.app.databinding.ActivitySignupAskNameBinding;

public class SignUpAskNameActivity extends AppCompatActivity {
    private ActivitySignupAskNameBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        binding = ActivitySignupAskNameBinding.inflate(getLayoutInflater());

        binding.nextButton.setOnClickListener(v -> {
            EditText nameEditText = binding.nameEditText;
            String name = nameEditText.getText().toString();

            Util.fetchUserDocument(db, user, userDocument -> {
                userDocument.getReference()
                        .update("name", name)
                        .addOnSuccessListener(unused -> {
                            finish();
                        });
            });
        });

        setContentView(binding.getRoot());
    }
}