package com.app.ssfitness_dev.ui.home.blog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.blog.BlogListAdapter.OnBlogListItemClicked;

public class BlogMindsetFragment extends Fragment implements OnBlogListItemClicked {


    private BlogViewModel mBlogViewModel;
    private NavController navController;
    private ProgressBar listProgress;
    private RecyclerView listView;
    private Animation fadeInAnim;
    private Animation fadeOutAnim;
    private BlogListAdapter adapter;

    public BlogMindsetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_mindset, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBlogViewModel = new ViewModelProvider(getActivity()).get(BlogViewModel.class);
        mBlogViewModel = new BlogViewModel("mindset");
        mBlogViewModel.getBlogListModelData().observe(getViewLifecycleOwner(), blogModels -> {

            //Load RecyclerView
            listView.startAnimation(fadeInAnim);
            listProgress.startAnimation(fadeOutAnim);

            adapter.setBlogListModels(blogModels);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);

        listView = view.findViewById(R.id.blog_mindset_list_view);
        listProgress = view.findViewById(R.id.list_progress);

        adapter = new BlogListAdapter(this);

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setHasFixedSize(true);
        listView.setAdapter(adapter);

        fadeInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    @Override
    public void onItemClicked(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("category", "mindset");
        Navigation.findNavController(getView()).navigate(R.id.blogDetails, args);

    }
}
