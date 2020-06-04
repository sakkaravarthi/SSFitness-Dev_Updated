package com.app.ssfitness_dev.ui.home.userprofiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private TextView mProfileDisplayName, mProfileDisplayActivity, mProfileDisplayFriends;
    private Button mProfileSendRequest, mProfileDeclineRequest;
    private ImageView mProfileDisplayImage;
    private MaterialToolbar materialToolbar;
    private ProgressDialog mProgressDialog;
    private String mCurrent_state;

    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String user_id = getIntent().getStringExtra("user_id");

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mProfileDisplayName = findViewById(R.id.profile_display_name);
        mProfileDisplayActivity = findViewById(R.id.profile_display_activity);
        mProfileDisplayFriends = findViewById(R.id.profile_display_total_friends);
        mProfileDisplayImage = findViewById(R.id.profile_display_image);

        mProfileSendRequest = findViewById(R.id.profile_send_request);
        mProfileDeclineRequest = findViewById(R.id.profile_decline_request);


        materialToolbar = findViewById(R.id.topAppBar);

        mCurrent_state = "not_friends";

       /* mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User data");
        mProgressDialog.setMessage("Please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
       mProgressDialog.show();*/

        mProfileDeclineRequest.setVisibility(View.INVISIBLE);
        mProfileDeclineRequest.setEnabled(false);

        //Toolbar Back
        materialToolbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("userName").getValue().toString();
                String activityLvl = dataSnapshot.child("activitylevel").getValue().toString();
                String profilePhoto = dataSnapshot.child("photoUrl").getValue().toString();

                mProfileDisplayName.setText(name);
                materialToolbar.setTitle(name);
                mProfileDisplayActivity.setText(activityLvl);

                if(profilePhoto!=null){
                    Glide.with(getApplication()).load(profilePhoto).into(mProfileDisplayImage);
                }

                //--------------- FRIENDS LIST / REQUEST FEATURE -----------
                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received"))
                            {
                                mCurrent_state = "request_received";
                                mProfileSendRequest.setBackgroundColor(Color.parseColor("#61A92A"));
                                mProfileSendRequest.setText("Accept Friend Request");

                                mProfileDeclineRequest.setVisibility(View.VISIBLE);
                                mProfileDeclineRequest.setEnabled(true);
                            }
                            else if(req_type.equals("sent"))
                                {
                                mCurrent_state = "request_sent";
                                mProfileSendRequest.setBackgroundColor(Color.RED);
                                mProfileSendRequest.setText("Cancel Friend Request");

                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);
                                }

                        }

                       // mProgressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){

                            mCurrent_state = "friends";

                            mProfileSendRequest.setBackgroundColor(Color.RED);
                            mProfileSendRequest.setText("Remove Friend");

                            mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                            mProfileDeclineRequest.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileDeclineRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // -------------------- DECLINE STATE ------------
                if(mCurrent_state.equals("request_received")||mCurrent_state.equals("request_sent")){
                    //delete from out user


                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //remove value from other user id
                                mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Log.d("USER_PROFILE_ACTIVITY", "onComplete: Friend request cancelled");
                                            mProfileSendRequest.setEnabled(true);
                                            mCurrent_state = "not_friends";
                                            mProfileSendRequest.setBackgroundColor(Color.parseColor("#61A92A"));
                                            mProfileSendRequest.setText("Send Friend Request");


                                            mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                            mProfileDeclineRequest.setEnabled(false);

                                        }
                                        else
                                        {
                                            Log.d("USER_PROFILE_ACTIVITY", "onFailure: Something went wrong. Please try again later.");
                                        }

                                    }
                                });

                            }
                            else {
                                Log.d("USER_PROFILE_ACTIVITY", "onFailure: Something went wrong. Please try again later.");
                            }
                        }
                    });

                }

            }
        });

        mProfileSendRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendRequest.setEnabled(false);

                // -------------------- NOT FRIEND STATE ------------
                if(mCurrent_state.equals("not_friends")){

                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationRef.getKey();


                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");


                    Map requestMap = new HashMap();
                    requestMap.put("friend_requests/"+mCurrentUser.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("friend_requests/"+user_id + "/" + mCurrentUser.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);


                    mRootRef.updateChildren(requestMap, new CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError!=null) {
                                Log.d("User Profile Activity", "onComplete: There was some error in sending request");
                            }
                            else {
                                mCurrent_state = "request_sent";
                                mProfileSendRequest.setBackgroundColor(Color.RED);
                                mProfileSendRequest.setText("Cancel Friend Request");

                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);
                            }
                            mProfileSendRequest.setEnabled(true);
                        }
                    });
                }

                // -------------------- FRIENDS STATE ------------
                if(mCurrent_state.equals("friends")){

                    Map unfriendMap = new HashMap();

                    unfriendMap.put("friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unfriendMap.put("friends/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener(){
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {

                                mCurrent_state = "not_friends";
                                mProfileSendRequest.setBackgroundColor(Color.parseColor("#61A92A"));
                                mProfileSendRequest.setText("Send Friend Request");


                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);

                                Toast.makeText(UserProfileActivity.this, "Friend removed successfully", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                String error = databaseError.getMessage();
                                Log.d("UserProfileActivity", "onError: " + error);
                            }

                            mProfileSendRequest.setEnabled(true);
                        }
                    });


                }



                // -------------------- CANCEL STATE ------------
                if(mCurrent_state.equals("request_sent")){

                    Map requestCancel = new HashMap();

                    requestCancel.put("friend_requests/" + mCurrentUser.getUid() +"/" + user_id, null);
                    requestCancel.put("friend_requests/" + user_id + "/" + mCurrentUser.getUid() , null);

                    mRootRef.updateChildren(requestCancel, new DatabaseReference.CompletionListener(){
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError == null ){
                                Log.d("USER_PROFILE_ACTIVITY", "onComplete: Friend request cancelled");

                                mCurrent_state = "not_friends";
                                mProfileSendRequest.setBackgroundColor(Color.parseColor("#61A92A"));
                                mProfileSendRequest.setText("Send Friend Request");


                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);

                            }
                            else {
                                Log.d("USER_PROFILE_ACTIVITY", "onFailure: Something went wrong. Please try again later.");
                            }

                            mProfileSendRequest.setEnabled(true);
                        }
                    });


                }



                // -------------------REQUEST RECEIVED STATE-----------
                if(mCurrent_state.equals("request_received"))
                {
                    String currentDate = DateFormat.getDateInstance().format(new Date());

                    Map friendMap = new HashMap();
                    friendMap.put("friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                    friendMap.put("friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);

                    friendMap.put("friend_requests/" + mCurrentUser.getUid() + "/" + user_id , null);
                    friendMap.put("friend_requests/" + user_id + "/" + mCurrentUser.getUid() , null);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){
                                Log.d("USER_PROFILE_ACTIVITY", "onComplete: Friend request cancelled");

                                mCurrent_state = "friends";

                                mProfileSendRequest.setBackgroundColor(Color.parseColor("#61A92A"));
                                mProfileSendRequest.setText("Remove Friend");


                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);
                            }
                            else
                            {
                                Log.d("USER_PROFILE_ACTIVITY", "onFailure: Something went wrong. Please try again later.");
                            }

                            mProfileSendRequest.setEnabled(true);
                        }
                    });

                }

                //------------------------------------------------

            }
        });
    }

   @Override
   protected void onStart() {
       super.onStart();
       if (mCurrentUser!= null) {
           mRootRef.child("users").child(mCurrentUser.getUid()).child("online").setValue("true");
       }

   }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCurrentUser!= null) {
            mRootRef.child("users").child(mCurrentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
