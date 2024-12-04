package com.tourbuddy.app;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tourbuddy.app.databinding.FragmentHomeTabBinding;

/**
 * 홈 화면의 홈 탭의 컨텐츠를 표시하는 Fragment.
 * home_tab_fragment.xml에 레이아웃이 정의되어 있으며, 크게 두 부분으로 나뉨
 * 1. 여행 대쉬보드(id: userTravelDashboard): 사용자의 여행까지의 디데이나, 여행 중인 지역의 날씨 등 필요한 정보를 간략하게 표시하는 영역.
 *      TODO: 현재 레이아웃에서는 예시 사진이 바탕으로 들어가 있음. 사진을 배경으로 사용하면 위에 표시할 텍스트가 잘 보이지 않아 단색 영역으로 바꾸기로 함.
 * 2. 컨텐츠 피드(id: contentsFeed): 다른 사용자들의 포스트들을 스크롤해서 볼 수 있는 영역. 포스트의 레이아웃은 post.xml에 정의되어 있음.
 *      TODO: RecyclerView를 사용해서 스크롤을 구현할 예정.
 *
 */
public class HomeTabFragment extends Fragment {
    private FragmentHomeTabBinding binding;

    private SharedPreferences userPreferences;

    private TextView dashboardText;

    /**
     * Fragment를 생성하는 Factory 메소드
     * @return HomeTabFragment의 새 인스턴스
     */
    public static HomeTabFragment newInstance(SharedPreferences userPreferences) {
        HomeTabFragment fragment = new HomeTabFragment();
        fragment.userPreferences = userPreferences;

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeTabBinding.inflate(inflater, container, false);

        // 테스트 용 코드 시작
        dashboardText = binding.dashboardText;
        Button logoutButton = binding.logoutButton;

        userPreferences.registerOnSharedPreferenceChangeListener((pref, key) -> {
            if (key.equals("id")) {
                updateDashboard();
            }
        });

        logoutButton.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(HomeTabFragment.this)
                    .commit();
            mainActivity.onLogout();
        });

        updateDashboard();
        // 테스트 용 코드 끝

        return binding.getRoot();
    }

    private void updateDashboard() {
        String id = userPreferences.getString("id", null);
        dashboardText.setText(String.format("로그인: %s", id));
    }
}