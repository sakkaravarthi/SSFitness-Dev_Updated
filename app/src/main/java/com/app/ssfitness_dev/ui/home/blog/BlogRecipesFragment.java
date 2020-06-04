package com.app.ssfitness_dev.ui.home.blog;

import android.content.Intent;
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
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.blog.BlogListAdapter.OnBlogListItemClicked;


public class BlogRecipesFragment extends Fragment implements OnBlogListItemClicked {

    private BlogViewModel mBlogViewModel;
    private NavController navController;
    private ProgressBar listProgress;
    private RecyclerView listView;
    private Animation fadeInAnim;
    private Animation fadeOutAnim;
    private BlogListAdapter adapter;


    public BlogRecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_recipes, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBlogViewModel = new ViewModelProvider(getActivity()).get(BlogViewModel.class);
        mBlogViewModel = new BlogViewModel("recipes");
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

        navController = Navigation.findNavController(view);

        listView = view.findViewById(R.id.blog_recipes_list_view);
        listProgress = view.findViewById(R.id.list_progress);

        adapter = new BlogListAdapter(this);

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setHasFixedSize(true);
        listView.setAdapter(adapter);

        fadeInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
    }

    @Override
    public void onItemClicked(int position) {
       //BlogRecipesFragmentDirections.ActionBlogRecipesFragmentToBlogDetails action = BlogRecipesFragmentDirections.actionBlogRecipesFragmentToBlogDetails();
       //action.setPosition(position);
       //navController.navigate(action);

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("category", "recipes");
        Navigation.findNavController(getView()).navigate(R.id.blogDetails, args);

    }

    //Fix for multiple touches, backstack delete
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

}
