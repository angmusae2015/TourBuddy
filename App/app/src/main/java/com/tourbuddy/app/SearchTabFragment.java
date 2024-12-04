package com.tourbuddy.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tourbuddy.app.databinding.FragmentSearchTabBinding;

public class SearchTabFragment extends Fragment {
    private FragmentSearchTabBinding binding;

    private SearchResultEmptyFragment searchResultEmptyFragment;

    public static SearchTabFragment newInstance() {
        SearchTabFragment fragment = new SearchTabFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchTabBinding.inflate(inflater, container, false);

        searchResultEmptyFragment = SearchResultEmptyFragment.newInstance();

        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.getId(), searchResultEmptyFragment)
                .commit();

        return binding.getRoot();
    }
}
