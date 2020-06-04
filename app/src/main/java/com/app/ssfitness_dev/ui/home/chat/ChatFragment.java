package com.app.ssfitness_dev.ui.home.chat;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.blog.BlogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private Button mBtnLiveChat, mBtnSendMail;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

     public  ChatFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);

    }

    private void initUi(View view) {
        mBtnLiveChat = view.findViewById(R.id.button_chat_live);
        mBtnSendMail = view.findViewById(R.id.button_chat_email);

        TabLayout tabLayout = view.findViewById(R.id.chat_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.chat_view_pager);

        LiveChatFragment liveChatFragment = new LiveChatFragment();
        FriendsFragment friendsFragment = new FriendsFragment();
        RequestsFragment requestsFragment = new RequestsFragment();

        tabLayout.setupWithViewPager(viewPager);

        viewPageAdapter viewPageAdapter = new viewPageAdapter(getChildFragmentManager(), 0);
        viewPageAdapter.addFragment(liveChatFragment,"Chat");
        viewPageAdapter.addFragment(friendsFragment,"Friends");
        viewPageAdapter.addFragment(requestsFragment,"Requests");

        viewPager.setAdapter(viewPageAdapter);

    }


    private static class viewPageAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public viewPageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }


}