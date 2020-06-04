package com.app.ssfitness_dev.ui.home.blog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ssfitness_dev.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class BlogListAdapter  extends RecyclerView.Adapter<BlogListAdapter.BlogViewHolder>{

    private List<BlogModel> blogListModels;
    private OnBlogListItemClicked onBlogListItemClicked;

    public BlogListAdapter(OnBlogListItemClicked onBlogListItemClicked) {
        this.onBlogListItemClicked = onBlogListItemClicked;
    }

    public void setBlogListModels(List<BlogModel> blogListModels) {
        this.blogListModels = blogListModels;
    }


    @NonNull
    @Override
    public BlogListAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogListAdapter.BlogViewHolder holder, int position) {
        holder.listTitle.setText(blogListModels.get(position).getTitle());

        String imageUrl = blogListModels.get(position).getImageUrl();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_app_icon)
                .into(holder.listImage);

        String listDescription = blogListModels.get(position).getDescription();
        String listAuthor = blogListModels.get(position).getAuthor();
        String listDate = blogListModels.get(position).getDateposted();

        if(listDescription.length() > 150){
            listDescription = listDescription.substring(0, 150);
        }

        holder.listDesc.setText(listDescription + "...");
        holder.listAuthor.setText(listAuthor);
        holder.listDate.setText(listDate);
        holder.listCategory.setText(blogListModels.get(position).getCategory());

    }


    @Override
    public int getItemCount() {
        if(blogListModels == null){
            return 0;
        } else {
            return blogListModels.size();
        }
    }


    public class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView listImage;
        private TextView listTitle;
        private TextView listDesc;
        private TextView listAuthor;
        private TextView listCategory;
        private TextView listMore;
        private TextView listDate;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);

            listImage = itemView.findViewById(R.id.list_image);
            listTitle = itemView.findViewById(R.id.list_title);
            listDesc = itemView.findViewById(R.id.list_desc);
            listCategory = itemView.findViewById(R.id.list_category);
            listMore = itemView.findViewById(R.id.list_more);
            listAuthor = itemView.findViewById(R.id.list_author);
            listDate = itemView.findViewById(R.id.list_date_posted);

            listMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBlogListItemClicked.onItemClicked(getAdapterPosition());
        }
    }

    public interface OnBlogListItemClicked {
        void onItemClicked(int position);
    }
}
