package com.tourbuddy.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourbuddy.app.databinding.FragmentSearchResultEmptyBinding;
import com.tourbuddy.app.databinding.FragmentSearchTabBinding;
import com.tourbuddy.app.databinding.ItemSearchSuggestionCityBinding;

import java.util.ArrayList;

public class SearchTabFragment extends Fragment {
    private FragmentSearchTabBinding binding;

    private SearchResultEmptyFragment searchResultEmptyFragment;

    private class CitySuggestionViewHolder extends RecyclerView.ViewHolder {
        private ItemSearchSuggestionCityBinding itemBinding;

        public CitySuggestionViewHolder(ItemSearchSuggestionCityBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }

    private class CitySuggetstionAdapter extends RecyclerView.Adapter<CitySuggestionViewHolder> {
        ArrayList<DocumentSnapshot> cityList = new ArrayList<>();

        @NonNull
        @Override
        public CitySuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSearchSuggestionCityBinding itemBinding = ItemSearchSuggestionCityBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            );

            return new CitySuggestionViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull CitySuggestionViewHolder holder, int position) {
            DocumentSnapshot cityDocument = cityList.get(position);

            holder.itemBinding.itemTitle.setText(cityDocument.getString("name"));
            if (cityDocument.getBoolean("isDomestic"))
                holder.itemBinding.itemTag.setText("국내 여행지");
            else holder.itemBinding.itemTag.setText("해외 여행지");

//            holder.itemBinding.itemContainer.setFocusable(true);
        }

        @Override
        public int getItemCount() {
            return cityList.size();
        }

        public void setCityList(ArrayList<DocumentSnapshot> cityList) {
            this.cityList = cityList;
            notifyDataSetChanged();
        }
    }

    public static SearchTabFragment newInstance() {
        SearchTabFragment fragment = new SearchTabFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchTabBinding.inflate(inflater, container, false);

        FragmentSearchResultEmptyBinding emptyResultBinding = FragmentSearchResultEmptyBinding.inflate(
                inflater, binding.resultContainer, true
        );

        CitySuggetstionAdapter citySuggetstionAdapter = new CitySuggetstionAdapter();
        binding.seachSuggestion.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.seachSuggestion.setAdapter(citySuggetstionAdapter);

        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ArrayList<DocumentSnapshot> result = Util.searchCity(editable.toString());
                citySuggetstionAdapter.setCityList(result);
            }
        });

        return binding.getRoot();
    }
}
