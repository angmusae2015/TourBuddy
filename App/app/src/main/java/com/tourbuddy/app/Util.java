package com.tourbuddy.app;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Util {
    private static List<DocumentSnapshot> fetchedCityList = new ArrayList<>();

    public static final String DEBUG_TAG = "AppDebug";

    public interface OnDocumentFetchListener {
        void onDocumentFetch(DocumentSnapshot userDocument);
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

    public static void fetchUserDocument(FirebaseFirestore db, FirebaseUser user, OnDocumentFetchListener listener) {
        String email = user.getEmail();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onDocumentFetch(null);
                    } else {
                        listener.onDocumentFetch(queryDocumentSnapshots
                                .getDocuments()
                                .get(0)
                        );
                    }
                });
    }

    public static void fetchCityDocument(FirebaseFirestore db) {
        db.collection("domestic")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Util.fetchedCityList.addAll(queryDocumentSnapshots.getDocuments());
                });
    }

    public static ArrayList<DocumentSnapshot> searchCity(String query) {
        ArrayList<DocumentSnapshot> resultCityList = new ArrayList<>();

        for (DocumentSnapshot cityDoc : fetchedCityList) {
            if (cityDoc.getString("name").startsWith(query)) {
                resultCityList.add(cityDoc);
            }
        }

        return resultCityList;
    }
}
