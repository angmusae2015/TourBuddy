package com.tourbuddy.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tourbuddy.app.databinding.FragmentMyPostsEmptyBinding;
import com.tourbuddy.app.databinding.FragmentProfileTabBinding;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class ProfileMyPostsFragment extends Fragment {
    FirebaseUser user;
    FirebaseFirestore db;
    private FragmentProfileTabBinding binding;
    private RecyclerView recyclerView;
    private PostReviewAdapter adapter;
    private List<Post> postList;

    public static ProfileTabFragment newInstance() {
        ProfileTabFragment fragment = new ProfileTabFragment();
        fragment.user = FirebaseAuth.getInstance().getCurrentUser();
        fragment.db = FirebaseFirestore.getInstance();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileTabBinding.inflate(inflater, container, false);

        initializeRecyclerView();
        fetchUserData();
        fetchUserPosts();

        return binding.getRoot();
    }

    private void initializeRecyclerView() {
        recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postList = new ArrayList<>();
        adapter = new PostReviewAdapter(requireContext(), postList);
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
        db.collection("posts")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        postList.add(post);
                    }
                    adapter.notifyDataSetChanged();

                    if (postList.isEmpty()) {
                        showEmptyState();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "게시물을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showEmptyState() {
        FragmentMyPostsEmptyBinding emptyPostsBinding = FragmentMyPostsEmptyBinding.inflate(
                getLayoutInflater(), binding.myPostsFragmentContainer, true
        );
        binding.myPostsFragmentContainer.removeAllViews();
        binding.myPostsFragmentContainer.addView(emptyPostsBinding.getRoot());
    }

    // PostReviewAdapter 내부 클래스
    private class PostReviewAdapter extends RecyclerView.Adapter<PostReviewAdapter.PostViewHolder> {
        private List<Post> posts;
        private Context context;

        public PostReviewAdapter(Context context, List<Post> posts) {
            this.context = context;
            this.posts = posts;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_post_review, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = posts.get(position);

            if (post.getImages() != null && !post.getImages().isEmpty()) {
                Glide.with(context)
                        .load(post.getImages().get(0))
                        .centerCrop()
                        .into(holder.postImageView);
            }

            holder.usernameTextView.setText(post.getUserId());
            holder.likeCountTextView.setText("좋아요 0개");
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        class PostViewHolder extends RecyclerView.ViewHolder {
            ImageView profileImage;
            ImageView postImageView;
            TextView usernameTextView;
            TextView likeCountTextView;
            TextView commentTextView;

            PostViewHolder(@NonNull View itemView) {
                super(itemView);
                profileImage = itemView.findViewById(R.id.profileImage);
                postImageView = itemView.findViewById(R.id.postImageView);
                usernameTextView = itemView.findViewById(R.id.usernameTextView);
                likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
                commentTextView = itemView.findViewById(R.id.commentTextView);
            }
        }
    }

    // Post 데이터 클래스
    public class Post {
        private String userId;
        private String caption;
        private List<String> images;
        private String type;
        private Timestamp timestamp;

        public Post() {}

        public String getUserId() { return userId; }
        public String getCaption() { return caption; }
        public List<String> getImages() { return images; }
        public String getType() { return type; }
        public Timestamp getTimestamp() { return timestamp; }
    }
}

