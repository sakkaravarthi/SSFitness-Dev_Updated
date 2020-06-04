package com.app.ssfitness_dev.ui.home.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.data.models.User;
import com.app.ssfitness_dev.ui.home.userprofiles.UserProfileActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class AvailableUsers extends Fragment {

    private RecyclerView mUsersRecyclerList;
    FirebaseRecyclerOptions<User> options;
    FirestoreRecyclerOptions<User> firestoreRecyclerOptions;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mCurrentUserDatabase;
    private FirebaseAuth mAuth;
    private com.google.firebase.database.Query mQueryRef;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Query mUsersRef;
    private String searchQuery;
    String current_user_name;


    public AvailableUsers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            searchQuery = bundle.getString("searchName");
        }

        return inflater.inflate(R.layout.fragment_available_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mUsersDatabase.child(mAuth.getCurrentUser().getUid()).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user_name =dataSnapshot.getValue().toString();
                Toast.makeText(getContext(), current_user_name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(!searchQuery.equals( current_user_name)) {
            mQueryRef = mUsersDatabase.orderByChild("userName").startAt(searchQuery).endAt(searchQuery + "\\uf8ff");
            //mUsersRef = firebaseFirestore.collection("users");//.whereEqualTo("userEmail", "ravi.gamer95@gmail.com");;

            mUsersRecyclerList = view.findViewById(R.id.recyclerview_users);
            mUsersRecyclerList.setHasFixedSize(true);
            mUsersRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        }

    }

    @Override
    public void onStart() {
        super.onStart();

/*
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<User>().setQuery(mUsersRef, User.class).build();
        FirestoreRecyclerAdapter<User, UsersViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<User, UsersViewHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {
                holder.setDetails(model.getUserName(), model.getActivitylevel(), model.getPhotoUrl());
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_search_single_item, viewGroup, false);

                return new UsersViewHolder(view);
            }
        };

        firestoreRecyclerAdapter.startListening();
        mUsersRecyclerList.setAdapter(firestoreRecyclerAdapter);
 */

        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(mQueryRef,User.class).build();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                options

        ) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {

                    holder.setDetails(model.getUserName(), model.getActivitylevel(), model.getPhotoUrl());
                    String userID;

                    userID = getRef(position).getKey();

                    holder.mView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent profileIntent = new Intent(getContext(), UserProfileActivity.class);
                            profileIntent.putExtra("user_id", userID);
                            startActivity(profileIntent);
                        }
                    });


            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_search_single_item, viewGroup, false);

                return new UsersViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        mUsersRecyclerList.setAdapter(firebaseRecyclerAdapter);
    }


    //Adapter
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setDetails(String userName, String userActivity, String userProfileUrl) {

            TextView userNameView = mView.findViewById(R.id.user_single_name);
            TextView userActivityView = mView.findViewById(R.id.user_single_activity_level);
            ImageView userProfileView = mView.findViewById(R.id.user_single_profile);

            userNameView.setText(userName);
            userActivityView.setText(userActivity);

            if(userProfileUrl!=null){
                Glide.with(mView.getContext()).load(userProfileUrl).into(userProfileView);
            }


        }
    }

}
