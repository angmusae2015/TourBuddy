package com.tourbuddy.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class newPostTabFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private GridLayout gridLayout;
    private static final int GRID_COLUMNS = 3;
    private View view;
    private FirebaseAuth auth;
    private SharedPreferences userPreferences;
    private Button btnShare;
    private MaterialButton btnSelectPhotos;
    private EditText editCaption;
    private TextView textLocation, textTag;
    private MaterialButtonToggleGroup toggleButton;
    private LinearLayout photoContainer;

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private ArrayList<Uri> selectedImages = new ArrayList<>();
    public static newPostTabFragment newInstance() {
        newPostTabFragment fragment = new newPostTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daily_post, container, false);
        initializeViews();
        setupGridLayout();
        setupListeners();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        return view;
    }

    private void initializeViews() {
        btnShare = view.findViewById(R.id.btn_share);
        btnSelectPhotos = view.findViewById(R.id.btn_select_photos);
        editCaption = view.findViewById(R.id.edit_caption);

        toggleButton = view.findViewById(R.id.toggleButton);
        photoContainer = view.findViewById(R.id.photo_container);
    }

    private void setupGridLayout() {
        gridLayout = new GridLayout(requireContext());
        gridLayout.setColumnCount(GRID_COLUMNS);
        gridLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        photoContainer.addView(gridLayout);
    }

    private void setupListeners() {
        btnShare.setOnClickListener(v -> uploadPost());

        btnSelectPhotos.setOnClickListener(v -> openGallery());

        toggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_travel_diary) {

                } else if (checkedId == R.id.btn_travel_review) {

                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {

                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        addImageToGrid(imageUri);
                    }
                } else if (data.getData() != null) {

                    Uri imageUri = data.getData();
                    addImageToGrid(imageUri);
                }
            }
        }
    }

    private void addImageToGrid(Uri imageUri) {
        ImageView imageView = new ImageView(requireContext());
        int size = getResources().getDisplayMetrics().widthPixels / GRID_COLUMNS;
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = size;
        params.height = size;
        params.setMargins(2, 2, 2, 2);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(imageView);

        gridLayout.addView(imageView);
        selectedImages.add(imageUri);
    }

    private void uploadPost() {
        String caption = editCaption.getText().toString();
        String postType = toggleButton.getCheckedButtonId() == R.id.btn_travel_diary ? "diary" : "review";

        // 이미지 업로드
        ArrayList<String> imageUrls = new ArrayList<>();
        for (Uri imageUri : selectedImages) {
            String imageName = UUID.randomUUID().toString();
            StorageReference imageRef = storage.getReference().child("images/" + imageName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());


                            if (imageUrls.size() == selectedImages.size()) {
                                savePostToFirestore(caption, postType, imageUrls);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }

    private void savePostToFirestore(String caption, String postType, ArrayList<String> imageUrls) {
        String userId = userPreferences.getString("id", null);
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;

        if (userId == null || userEmail == null) {
            showToast("사용자 정보를 가져올 수 없습니다.");
            return;
        }
        Map<String, Object> post = new HashMap<>();
        post.put("caption", caption);
        post.put("type", postType);
        post.put("images", imageUrls);
        post.put("timestamp", com.google.firebase.Timestamp.now());
        post.put("userId", userId);
        post.put("userEmail", userEmail);
        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    showToast("게시물이 성공적으로 업로드되었습니다.");
                })
                .addOnFailureListener(e -> {
                    showToast("게시물 업로드에 실패했습니다. 다시 시도해주세요.");
                });
    }
    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

}