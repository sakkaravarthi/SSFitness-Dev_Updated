package com.app.ssfitness_dev.data.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;


import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class User implements Serializable {

    public String userID;
    public String userName;
    public String userEmail;
    public String photoUrl;
    public String dateofbirth;
    public int height;
    public String gender;
    public int weight;
    public double bmi;
    public String country;
    public @ServerTimestamp
    Date createdOn;
    public String goal;
    public String activitylevel;
    public String diet;

    //Check if admin
    public boolean admin;

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    //To know whether profile is complete or not
    public boolean profileComplete;

    //To check remaining membership days
    public int membershipDays;
    public boolean membership;

    @Exclude
    public boolean isAuthenticated;

    @Exclude
    public boolean isNew, isCreated;

    public User() {
    }

    public User(String goal) {
        this.goal = goal;
    }

    public User(String userID, String userName, String userEmail, String photoUrl, String dateofbirth,
                String gender, int height, int weight, double bmi, String country, Date createdOn,
                Boolean profileComplete, Boolean membership, Integer membershipDays
                , String goal, String diet, String activitylevel, boolean admin) {

        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.photoUrl = photoUrl;
        this.dateofbirth = dateofbirth;
        this.height = height;
        this.gender = gender;
        this.weight = weight;
        this.bmi = bmi;
        this.country = country;
        this.createdOn = createdOn;
        this.profileComplete = profileComplete;
        this.membership = membership;
        this.membershipDays = membershipDays;
        this.goal = goal;
        this.activitylevel = activitylevel;
        this.diet = diet;
        this.admin = admin;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getActivitylevel() {
        return activitylevel;
    }

    public void setActivitylevel(String activitylevel) {
        this.activitylevel = activitylevel;
    }

    public User(String userName, String photoUrl, String activitylevel) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.activitylevel = activitylevel;
    }
}

