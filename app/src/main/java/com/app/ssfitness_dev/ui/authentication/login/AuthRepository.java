package com.app.ssfitness_dev.ui.authentication.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.ssfitness_dev.data.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
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
import java.util.Objects;

import static com.app.ssfitness_dev.utilities.Constants.TAG_AUTH_REPOSITORY;
import static com.app.ssfitness_dev.utilities.Constants.USERS;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;

public class AuthRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //Creating Database Reference Firestore
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserDatabase ;

    String deviceToken;



    MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential){

        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                deviceToken = FirebaseInstanceId.getInstance().getToken();
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                //insert device token to userdb

                assert firebaseUser != null;

                String userID = firebaseUser.getUid();
                String userName = firebaseUser.getDisplayName();
                String userEmail = firebaseUser.getEmail();
                String photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
                String dateofbirth = "";
                String gender = "";
                int height = 0;
                int weight = 0;
                double bmi = 0;
                String country = "";
                String goal = "";
                String diet = "";
                String activitylevel = "";

                int membershipDays = 0;
                boolean profileComplete = false;
                boolean membership = false;
                boolean admin = false;

                Date createdOn = null;

                User user = new User(userID, userName, userEmail, photoUrl,dateofbirth,gender,height,weight,bmi
                        ,country,createdOn, profileComplete, membership, membershipDays,goal,diet,activitylevel, admin);

                mUserDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(firebaseAuth.getCurrentUser().getUid());

                if (isNewUser) {
                    mUserDatabase.child("device_token").setValue(deviceToken);
                    user.isNew = isNewUser;
                    user.profileComplete = false;
                    authenticatedUserMutableLiveData.setValue(user);
                    logMessage(TAG_AUTH_REPOSITORY,"User is new. Creating a new account");
                } else {
                    mUserDatabase.child("device_token").setValue(deviceToken);
                    usersRef.document(user.userID).get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            DocumentSnapshot document = userTask.getResult();
                            if(document.exists()) {
                                User userProfileCheck = document.toObject(User.class);
                                    authenticatedUserMutableLiveData.setValue(userProfileCheck);
                                    logMessage(TAG_AUTH_REPOSITORY, "Profile is incomplete. Redirecting to Questionaire");
                            }
                        } else {
                            logMessage(TAG_AUTH_REPOSITORY,userTask.getException().getMessage());
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> {
            logMessage(TAG_AUTH_REPOSITORY,e.toString());
        });

        return authenticatedUserMutableLiveData;
    }

    MutableLiveData<User> createUserInFirestoreIfNotExists(User authenticatedUser) {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();

        //Users - > ID(Documents) -> Data(fields)
        DocumentReference uidRef = usersRef.document(authenticatedUser.userID);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());

        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                assert document != null;
                if (!document.exists()) {


                    //adds data to realtime db
                    mUserDatabase.setValue(authenticatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mUserDatabase.child("device_token").setValue(deviceToken);
                                logMessage(TAG_AUTH_REPOSITORY, "data added to google firebase database");
                            }
                        }
                    });

                    //adds data to firestore db
                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {

                            authenticatedUser.isCreated = true;
                            newUserMutableLiveData.setValue(authenticatedUser);
                            logMessage(TAG_AUTH_REPOSITORY,"Google account created successfully in firebase.");

                        } else {
                           logMessage(TAG_AUTH_REPOSITORY, Objects.requireNonNull(userCreationTask.getException()).getMessage());
                        }
                    });
                }
            } else {
               logMessage(TAG_AUTH_REPOSITORY, Objects.requireNonNull(uidTask.getException()).getMessage());
            }
         }
        );
        return newUserMutableLiveData;
    }

    //Sign in, Check if profile is complete or not
    public LiveData<String> firebaseSignIn(String userEmail, String userPassword) {

        MutableLiveData<String> signInUserLiveData = new MutableLiveData<>();


        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
            if(task.isSuccessful()){


                //Check if profile is complete or not. If not completed, show questions and complete the full profile
                String currentUid = firebaseAuth.getCurrentUser().getUid();
                DocumentReference uidRef = usersRef.document(currentUid);
                deviceToken = FirebaseInstanceId.getInstance().getToken();

                mUserDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(currentUid);

                mUserDatabase.child("device_token").setValue(deviceToken);

                uidRef.get().addOnCompleteListener(profileCompleteTask -> {
                    if(profileCompleteTask.isSuccessful()){

                        DocumentSnapshot document = profileCompleteTask.getResult();
                        if(document.exists()){

                            User user = document.toObject(User.class);
                            if(user.profileComplete){
                                 signInUserLiveData.setValue("success");
                                logMessage(TAG_AUTH_REPOSITORY,"Firebase user login successful...going to Dashboard");
                            }
                            else{
                                 signInUserLiveData.setValue("incomplete");
                                logMessage(TAG_AUTH_REPOSITORY,"Firebase user incomplete. Redirecting to Questionaire");
                            }
                        }
                    }
                    else {
                        signInUserLiveData.setValue("dataerror");
                    }
                });

            }
        }).addOnFailureListener(e -> {
            signInUserLiveData.setValue("failure");
            logMessage(TAG_AUTH_REPOSITORY,e.toString());
        });

        return signInUserLiveData;
    }



}
