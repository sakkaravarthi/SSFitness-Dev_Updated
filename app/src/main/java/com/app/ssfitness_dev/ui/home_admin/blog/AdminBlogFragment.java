package com.app.ssfitness_dev.ui.home_admin.blog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.ssfitness_dev.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminBlogFragment extends Fragment {

    private Button addBlog, deleteBlog, modifyBlog, viewBlog;
    private NavController navController;

    public AdminBlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_blog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addBlog = view.findViewById(R.id.addBlogBtn);
        deleteBlog = view.findViewById(R.id.deleteBlogBtn);
        modifyBlog = view.findViewById(R.id.modifyBlogBtn);
        viewBlog = view.findViewById(R.id.viewBlogBtn);

        navController = Navigation.findNavController(view);

        addBlog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_adminBlogFragment_to_addBlogFragment);
            }
        });

        deleteBlog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_adminBlogFragment_to_deleteBlogFragment);
            }
        });

        modifyBlog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_adminBlogFragment_to_modifyBlogFragment);
            }
        });

        viewBlog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_adminBlogFragment_to_viewBlogsFragment);
            }
        });
    }




}
