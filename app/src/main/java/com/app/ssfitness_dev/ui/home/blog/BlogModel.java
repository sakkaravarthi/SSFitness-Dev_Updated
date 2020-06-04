package com.app.ssfitness_dev.ui.home.blog;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BlogModel {
    @DocumentId
    private String blog_id;
    private String title;
    private String description;
    private String imageUrl;
    private String author;
    private String dateposted;

    public @ServerTimestamp
    Date createdOn;

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public BlogModel(){

    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateposted() {
        return dateposted;
    }

    public void setDateposted(String dateposted) {
        this.dateposted = dateposted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BlogModel(String blog_id, String title, String description, String imageUrl, String author, String dateposted, String category) {
        this.blog_id = blog_id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.author = author;
        this.dateposted = dateposted;
        this.category = category;
    }

    private String category;


}
