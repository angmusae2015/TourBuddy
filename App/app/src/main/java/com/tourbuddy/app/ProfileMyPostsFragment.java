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
import com.tourbuddy.app.databinding.FragmentProfileTabBinding;

public class ProfileMyPostsFragment extends Fragment {
    FirebaseUser user;
    FirebaseFirestore db;
    private FragmentProfileTabBinding binding;

    public static ProfileMyPostsFragment newInstance() {
        ProfileMyPostsFragment fragment = new ProfileMyPostsFragment();
        fragment.user = FirebaseAuth.getInstance().getCurrentUser();
        fragment.db = FirebaseFirestore.getInstance();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileTabBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}
