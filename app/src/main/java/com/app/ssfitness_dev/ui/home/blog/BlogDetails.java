package com.app.ssfitness_dev.ui.home.blog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogDetails extends Fragment {

    private TextView blogAuthor,blogTitle, blogCategory, blogDescription, blogDate;
    private ImageView blogImage;
    private NavController navController;
    private int position;
    private BlogViewModel blogListViewModel;
    private String category;

    public BlogDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);

        navController = Navigation.findNavController(view);
        position =  getArguments().getInt("position");
        category =  getArguments().getString("category");

        // position = BlogDetailsArgs.fromBundle(getArguments()).getPosition();


    }

    private void initUi(View view) {
        blogAuthor = view.findViewById(R.id.blog_author);
        blogCategory = view.findViewById(R.id.blog_category);
        //blogDate = findViewById(R.id.);
        blogTitle = view.findViewById(R.id.blog_title);
        blogDescription = view.findViewById(R.id.blog_desc);
        blogImage = view.findViewById(R.id.blog_image);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        blogListViewModel = new ViewModelProvider(getActivity()).get(BlogViewModel.class);
        blogListViewModel = new BlogViewModel(category);
        blogListViewModel.getBlogListModelData().observe(getViewLifecycleOwner(), new Observer<List<BlogModel>>() {
            @Override
            public void onChanged(List<BlogModel> blogModels) {

                Glide.with(getContext())
                        .load(blogModels.get(position).getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_app_icon)
                        .into(blogImage);

                blogAuthor.setText(blogModels.get(position).getAuthor());
                blogDescription.setText(blogModels.get(position).getDescription());
                blogTitle.setText(blogModels.get(position).getTitle());
                blogCategory.setText(blogModels.get(position).getCategory());
                
            }

        });

    }


}
