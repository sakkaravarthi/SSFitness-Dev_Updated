package com.app.ssfitness_dev.ui.home.blog;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.blog.BlogListAdapter.OnBlogListItemClicked;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;

import static com.app.ssfitness_dev.utilities.Constants.TAG_GOAL_FRAGMENT;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;

public class BlogFragment extends Fragment {


    private ViewPager viewPager;
    private TabLayout tabLayout;

    private BlogFitnessFragment blogFitnessFragment;
    private BlogMindsetFragment blogMindsetFragment;
    private BlogRecipesFragment blogRecipesFragment;



    public BlogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.blog_fragment, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.blog_tab_layout);
        viewPager = view.findViewById(R.id.blog_view_pager);

        blogFitnessFragment = new BlogFitnessFragment();
        blogMindsetFragment = new BlogMindsetFragment();
        blogRecipesFragment = new BlogRecipesFragment();

        tabLayout.setupWithViewPager(viewPager);

        viewPageAdapter viewPageAdapter = new viewPageAdapter(getChildFragmentManager(), 0);

        viewPageAdapter.addFragment(blogFitnessFragment,"Fitness");
        viewPageAdapter.addFragment(blogMindsetFragment,"Mindset");
        viewPageAdapter.addFragment(blogRecipesFragment,"Recipes");

        viewPager.setAdapter(viewPageAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_fitness);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_mindset);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_recipes_black);

       /* BadgeDrawable badgeDrawable = tabLayout.getTabAt(0).getOrCreateBadge();
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(12);*/
    }


    private class viewPageAdapter extends FragmentStatePagerAdapter {

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
