package com.tourbuddy.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourbuddy.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private ActivityMainBinding binding;

    private ActivityResultLauncher<Intent> loginLauncher;
    private Intent loginIntent;

    private SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // 로그인 화면 액티비티로 전환하는 launcher
        loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    // 로그인 액티비티가 종료되면 홈 화면으로 전환하고 user 필드를 갱신함
                    user = auth.getCurrentUser();
                    Util.fetchUserId(db, user, id -> userPreferences.edit()
                            .putString("id", id)
                            .apply());
                    setHome();
                });
        loginIntent = new Intent(MainActivity.this, LoginActivity.class);

        userPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        checkCredential();
    }

    /**
     * 현재 캐시된 인증 정보가 유효한지 확인하고 유효하지 않을 경우 로그아웃 후 로그인 액티비티로 전환하는 메소드
     */
    private void checkCredential() {
        // 현재 로그인한 인증 정보가 캐시에 없을 경우
        if (user == null) {
            // 로그인 액티비티로 전환
            loginLauncher.launch(loginIntent);
        }
        // 현재 로그인한 인증 정보가 캐시에 있을 경우
        else {
            // 인증 정보를 갱신해 유효한지 확인
            user.reload()
                    // 캐시된 인증 정보가 유효할 경우
                    .addOnSuccessListener(reloadResult -> {
                        // user SharedPreferences에 로그인한 아이디 저장
                        Util.fetchUserId(db, user, this::setIdInPreferences);
                        // 홈 화면으로 전환
                        setHome();
                    })
                    // 캐시된 인증 정보가 유효하지 않을 경우
                    .addOnFailureListener(exception -> {
                        // 로그아웃 후 로그인 액티비티로 전환
                        auth.signOut();
                        setIdInPreferences(null);
                        loginLauncher.launch(new Intent(MainActivity.this, LoginActivity.class));
                    });
        }
    }

    /**
     * 로그인이 완료되면 홈 화면으로 전환하고 fragmentContainer에 홈 탭의 fragment를 채우는 메소드
     */
    private void setHome() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        attachListenerToBottomNavigation();

        transferTo(HomeTabFragment.newInstance(userPreferences));
    }

    /**
     * 하단 메뉴의 각 이벤트에 대한 동작을 정의하는 메소드
     */
    private void attachListenerToBottomNavigation() {
        BottomNavigationView bottomNavigation = binding.bottomNavigation;

        // 하단 메뉴를 선택했을 때 어떤 fragment로 전환될 지 정의함
        bottomNavigation.setOnItemSelectedListener(item -> {
            // TODO: 탭 간 전환에서 fragment를 새 인스턴스로 만들지 않고 이전 상황을 다시 불러올 수 있도록 재구현할 것
            if (item.getItemId() == R.id.homeTab) {
                transferTo(HomeTabFragment.newInstance(userPreferences));
            }
            else if (item.getItemId() == R.id.searchTab) {
                 transferTo(SearchTabFragment.newInstance());
            }
            else if (item.getItemId() == R.id.newPostTab) {
                // transferTo(newPostTabFragment.newInstance());
            }
            else if (item.getItemId() == R.id.myPlanTab) {
//                 transferTo(MyPlanTabFragment.newInstance(userPreferences));
            }
            else if (item.getItemId() == R.id.profileTab) {
                // transferTo(profileTabFragment.newInstance());
            }

            return true;
        });

        // 하단 메뉴 바에서 같은 메뉴를 선택하면 스크롤을 맨 위로 올림
        bottomNavigation.setOnItemReselectedListener(item -> {
            // TODO: 현재 fragment의 스크롤을 맨 위로 올리도록 구현
        });
    }

    /**
     * 매개변수로 주어진 fragment 인스턴스로 홈 Main 액티비티의 fragmentContainer를 교체함
     * @param fragment 교체할 fragment 인스턴스
     */
    private void transferTo(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void setIdInPreferences(String id) {
        userPreferences.edit()
                .putString("id", id)
                .apply();
    }

    public void onLogout() {
        auth.signOut();
        user = null;
        setIdInPreferences(null);
        loginLauncher.launch(loginIntent);
    }
}