package com.tourbuddy.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tourbuddy.app.databinding.HomeTabFragmentBinding;

/**
 * 홈 화면의 홈 탭의 컨텐츠를 표시하는 Fragment.
 * home_tab_fragment.xml에 레이아웃이 정의되어 있으며, 크게 두 부분으로 나뉨
 * 1. 여행 대쉬보드(id: userTravelDashboard): 사용자의 여행까지의 디데이나, 여행 중인 지역의 날씨 등 필요한 정보를 간략하게 표시하는 영역.
 *      TODO: 현재 레이아웃에서는 예시 사진이 바탕으로 들어가 있음. 사진을 배경으로 사용하면 위에 표시할 텍스트가 잘 보이지 않아 단색 영역으로 바꾸기로 함.
 * 2. 컨텐츠 피드(id: contentsFeed): 다른 사용자들의 포스트들을 스크롤해서 볼 수 있는 RecyclerView. 포스트의 레이아웃은 post.xml에 정의되어 있음.
 *      TODO: Post 뷰를 참조하는 PostViewHolder, PostAdapter 정의 필요.
 *      TODO: PostAdapter의 필드에 PostViewHolder 리스트와 해당 리스트에 아이템을 추가하는 void getNewPosts() 메소드 정의 필요.
 *      TODO: 스크롤을 감지해서 PostViewHolder의 리스트에서 일정 비율 이상의 PostView를 소진하면 getNewPosts() 메소드를 호출해 추가 포스트를 요청해야 함.
 */
public class HomeTabFragment extends Fragment {
    private HomeTabFragmentBinding binding;

    /**
     * Fragment를 생성하는 Factory 메소드
     * @return HomeTabFragment의 새 인스턴스
     */
    public static HomeTabFragment newInstance() {
        HomeTabFragment fragment = new HomeTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomeTabFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}