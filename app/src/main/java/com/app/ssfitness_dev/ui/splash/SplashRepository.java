package com.app.ssfitness_dev.ui.splash;

import androidx.lifecycle.MutableLiveData;

import com.app.ssfitness_dev.data.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.app.ssfitness_dev.utilities.Constants.TAG_SPLASH_REPOSITORY;
import static com.app.ssfitness_dev.utilities.Constants.USERS;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;

public class SplashRepository  {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user = new User();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);

    MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase() {
        MutableLiveData<User> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            user.isAuthenticated = false;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);

            logMessage(TAG_SPLASH_REPOSITORY,"User Authentication Failed");
        } else {
            user.userID = firebaseUser.getUid();
            user.isAuthenticated = true;

            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
            logMessage(TAG_SPLASH_REPOSITORY,"User Authentication Success");
        }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }

    MutableLiveData<User> addUserToLiveData(String userId) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

        usersRef.document(userId).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot document = userTask.getResult();
                if(document.exists()) {
                    User user = document.toObject(User.class);
                    userMutableLiveData.setValue(user);
                }
            } else {
                logMessage(TAG_SPLASH_REPOSITORY,userTask.getException().getMessage());
            }
        });
        return userMutableLiveData;
    }



}
