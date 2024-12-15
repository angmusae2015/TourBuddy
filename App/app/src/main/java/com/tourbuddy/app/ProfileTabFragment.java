package com.tourbuddy.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tourbuddy.app.databinding.FragmentMyPostsEmptyBinding;
import com.tourbuddy.app.databinding.FragmentProfileTabBinding;

import java.util.ArrayList;
import java.util.List;

public class ProfileTabFragment extends Fragment {
    static String userId;
    FirebaseUser user;
    FirebaseFirestore db;
    private FragmentProfileTabBinding binding;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList;
    private SharedPreferences userPreferences;

    public static ProfileTabFragment newInstance() {
        ProfileTabFragment fragment = new ProfileTabFragment();
        fragment.user = FirebaseAuth.getInstance().getCurrentUser();
        fragment.db = FirebaseFirestore.getInstance();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileTabBinding.inflate(inflater, container, false);

        initRecyclerView();
        fetchUserData();
        fetchUserPosts();

        return binding.getRoot();
    }

    private void initRecyclerView() {
        recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postList = new ArrayList<>();
        adapter = new PostAdapter(postList);
        recyclerView.setAdapter(adapter);
        binding.myPostsFragmentContainer.addView(recyclerView);
    }

    private void fetchUserData() {
        Util.fetchUserDocument(db, user, userDocument -> {
            String name = userDocument.getString("name");
            String id = userDocument.getString("id");
            String followingNum = Long.toString(userDocument.getLong("following"));
            String followerNum = Long.toString(userDocument.getLong("follower"));

            requireActivity().runOnUiThread(() -> {
                binding.nameText.setText(name);
                binding.idText.setText(id);
                binding.followNum.setText(String.format("%s\n팔로우", followingNum));
                binding.followerNum.setText(String.format("%s\n팔로워", followerNum));
            });
        });
    }

    private void fetchUserPosts() {
        if (userPreferences != null) {
            userId = userPreferences.getString("id", null);
            if (userId != null) {
                db.collection("posts")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            postList.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String caption = document.getString("caption");
                                List<String> images = (List<String>) document.get("images");
                                Post post = new Post(caption, images);
                                postList.add(post);
                            }
                            adapter.notifyDataSetChanged();

                            if (postList.isEmpty()) {
                                showEmptyState();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "게시물을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            showEmptyState();
                        });
            }
        }
    }

    private void showEmptyState() {
        requireActivity().runOnUiThread(() -> {
            FragmentMyPostsEmptyBinding emptyPostsBinding = FragmentMyPostsEmptyBinding.inflate(
                    getLayoutInflater(), binding.myPostsFragmentContainer, true
            );
            binding.myPostsFragmentContainer.removeAllViews();
            binding.myPostsFragmentContainer.addView(emptyPostsBinding.getRoot());
        });
    }

    private static class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
        private List<Post> posts;

        PostAdapter(List<Post> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = posts.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        static class PostViewHolder extends RecyclerView.ViewHolder {
            TextView likeCountTextView;
            TextView usernameTextView;
            TextView commentTextView;
            ImageView profileImage;
            ConstraintLayout container;
            GridLayout imageGridLayout;
            private static final int GRID_COLUMNS = 3;

            PostViewHolder(@NonNull View itemView) {
                super(itemView);
                likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
                usernameTextView = itemView.findViewById(R.id.usernameTextView);
                commentTextView = itemView.findViewById(R.id.commentTextView);
                profileImage = itemView.findViewById(R.id.profileImage);
                container = (ConstraintLayout) itemView;

                // GridLayout 설정
                imageGridLayout = new GridLayout(itemView.getContext());
                imageGridLayout.setId(View.generateViewId());
                imageGridLayout.setColumnCount(GRID_COLUMNS);

                // GridLayout용 ConstraintLayout 파라미터 설정
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );

                // 기존 ImageView의 제약 조건을 가져와서 GridLayout에 적용
                View oldImageView = itemView.findViewById(R.id.postImageView);
                params.topToBottom = R.id.profileImage;
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                imageGridLayout.setLayoutParams(params);

                // 기존 ImageView 제거 및 GridLayout 추가
                container.removeView(oldImageView);
                container.addView(imageGridLayout);

                // likeCountTextView와 commentTextView의 제약 조건 업데이트
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(container);
                constraintSet.connect(R.id.likeCountTextView, ConstraintSet.TOP,
                        imageGridLayout.getId(), ConstraintSet.BOTTOM);
                constraintSet.applyTo(container);
            }

            void bind(Post post) {

                usernameTextView.setText(userId);
                likeCountTextView.setText(post.getCaption());

                imageGridLayout.removeAllViews();
                if (post.getImages() != null && !post.getImages().isEmpty()) {
                    int screenWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
                    int imageSize = (screenWidth - 32) / GRID_COLUMNS; // 패딩 고려

                    for (String imageUrl : post.getImages()) {
                        ImageView imageView = new ImageView(itemView.getContext());
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = imageSize;
                        params.height = imageSize;
                        params.setMargins(2, 2, 2, 2);
                        imageView.setLayoutParams(params);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        Glide.with(itemView.getContext())
                                .load(imageUrl)
                                .centerCrop()
                                .into(imageView);

                        imageGridLayout.addView(imageView);
                    }
                }
            }
        }

    }

    private static class Post {
        private String caption;
        private List<String> images;

        Post(String caption, List<String> images) {
            this.caption = caption;
            this.images = images;
        }

        String getCaption() { return caption; }
        List<String> getImages() { return images; }
    }
}
