package com.tourbuddy.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tourbuddy.app.databinding.FragmentSearchResultEmptyBinding;

public class SearchResultEmptyFragment extends Fragment {
    private FragmentSearchResultEmptyBinding binding;

    public static SearchResultEmptyFragment newInstance() {
        SearchResultEmptyFragment fragment = new SearchResultEmptyFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchResultEmptyBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}