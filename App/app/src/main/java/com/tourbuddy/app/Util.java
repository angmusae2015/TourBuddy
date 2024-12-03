package com.tourbuddy.app;

import android.util.Log;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Util {
    public interface OnUserDocumentFetchListener {
        void onUserDocumentFetch(DocumentSnapshot userDocument);
    }

    public interface OnUserIdFetchListener {
        void onUserIdFetch(String id);
    }

    /**
     * TextInputLayout 뷰에 입력한 텍스트를 반환하는 메소드
     * @param layout 텍스트를 불러올 TextInputLayout 뷰
     * @return 해당 TextInputLayout 뷰에 입력한 텍스트
     */
    public static String getTextFromTextInputLayout(TextInputLayout layout) {
        return layout.getEditText().getText().toString();
    }

    public static void fetchUserId(FirebaseFirestore db, FirebaseUser user, OnUserIdFetchListener listener) {
        String email = user.getEmail();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onUserIdFetch(null);
                    } else {
                        String id = queryDocumentSnapshots.getDocuments()
                                .get(0)
                                .getString("id");

                        listener.onUserIdFetch(id);
                    }
                });
    }

    public static void fetchUserDocument(FirebaseFirestore db, FirebaseUser user, OnUserDocumentFetchListener listener) {
        String email = user.getEmail();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onUserDocumentFetch(null);
                    } else {
                        listener.onUserDocumentFetch(queryDocumentSnapshots
                                .getDocuments()
                                .get(0)
                        );
                    }
                });
    }
}
