package com.tourbuddy.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourbuddy.app.databinding.FragmentMyPostsEmptyBinding;
import com.tourbuddy.app.databinding.FragmentProfileTabBinding;

public class ProfileTabFragment extends Fragment {
    FirebaseUser user;
    FirebaseFirestore db;
    private FragmentProfileTabBinding binding;

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

        FragmentMyPostsEmptyBinding emptyPostsBinding = FragmentMyPostsEmptyBinding.inflate(
                inflater, binding.myPostsFragmentContainer, true
        );

        Util.fetchUserDocument(db, user, userDocument -> {
            String name = userDocument.getString("name");
            String id = userDocument.getString("id");
            String followingNum = Long.toString(userDocument.getLong("following"));
            String followerNum =  Long.toString(userDocument.getLong("follower"));

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.nameText.setText(name);
                    binding.idText.setText(id);
                    binding.followNum.setText(String.format("%s\n팔로우", followingNum));
                    binding.followerNum.setText(String.format("%s\n팔로워", followerNum));
                }
            });
        });

        return binding.getRoot();
    }
}
