package com.app.ssfitness_dev.ui.authentication.signup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.ssfitness_dev.data.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.app.ssfitness_dev.utilities.Constants.TAG_SIGNUP_REPOSITORY;
import static com.app.ssfitness_dev.utilities.Constants.TAG_SPLASH_REPOSITORY;
import static com.app.ssfitness_dev.utilities.Constants.USERS;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;

public class SignupRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //Creating Database Reference Firestore
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);

    //
    private DatabaseReference mUserDatabase;

    String deviceToken;

    MutableLiveData<User> firebaseSignUp(String email, String password) {
        MutableLiveData<User> createdUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            boolean isNewUser = true;
            deviceToken = FirebaseInstanceId.getInstance().getToken();
            logMessage(TAG_SIGNUP_REPOSITORY,"user is successfully created!");
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            DocumentReference userIdRef = usersRef.document(firebaseUser.getUid());
            mUserDatabase  = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

            if(firebaseUser != null){

                String userID = firebaseUser.getUid();
                String userName = "";
                String userEmail = email;
                String photoUrl = "";
                String dateofbirth = "";
                String gender = "";
                int height = 0;
                int weight = 0;
                double bmi = 0;
                String country = "";
                String goal = "";
                String diet = "";
                String activitylevel = "";

                boolean membership = false;
                boolean profileComplete = false;
                boolean admin = false;
                int membershipdays=0;
                //String createdOn = CurrentDate + CurrentTime;
                Date createdOn = null;
                User user = new User(userID, userName, userEmail, photoUrl,dateofbirth,gender
                        ,height,weight, bmi,country,createdOn
                        ,profileComplete, membership, membershipdays,goal,diet,activitylevel,false);

                user.isCreated = isNewUser;

                userIdRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();

                        if (!document.exists()) {


                            mUserDatabase.setValue(user);
                            mUserDatabase.child("device_token").setValue(deviceToken);

                            userIdRef.set(user).addOnCompleteListener(userCreationTask -> {
                                if (userCreationTask.isSuccessful()) {
                                    user.isCreated = true;
                                    createdUserMutableLiveData.setValue(user);

                                }
                            });
                        }
                    }
                }).addOnFailureListener(e -> logMessage(TAG_SIGNUP_REPOSITORY,e.getMessage()));
            }
        }).addOnFailureListener(e -> {
            User user = new User();
            user.isCreated = false;
            if(e.getMessage().contains("already")){
                createdUserMutableLiveData.setValue(user);
            }

            logMessage(TAG_SIGNUP_REPOSITORY,e.getMessage());
        });

        return createdUserMutableLiveData;
    }

}