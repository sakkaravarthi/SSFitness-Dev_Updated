package com.app.ssfitness_dev.ui.home.chat;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.data.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    FirebaseRecyclerOptions<Notifications> options;
    int count = 0;
    private DatabaseReference mRequestsDatabase,mFriendsDatabase;
    private DatabaseReference mUserReference;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private String notificationId;
    private RecyclerView mRequestsRecyclerView;
    private FirebaseDatabase mNotificationDatabase;
    private com.google.firebase.database.Query mQueryRef;

    public RequestsFragment() {
        /// Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getUid();
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users");
        mQueryRef = mRequestsDatabase.child(mCurrentUserId);
        mRequestsRecyclerView = view.findViewById(R.id.recycler_view_requests);
        mRequestsRecyclerView.setHasFixedSize(true);
        mRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        //options = new FirebaseRecyclerOptions.Builder<Notifications>().setQuery(mQueryRef, Notifications.class).build();
        options =
                new FirebaseRecyclerOptions.Builder<Notifications>()
                        .setQuery(mQueryRef, new SnapshotParser<Notifications>() {
                            @NonNull
                            @Override
                            public Notifications parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Notifications(snapshot.child("from").getValue().toString(), snapshot.child("type").getValue().toString(), snapshot.getKey());
                            }
                        })
                        .build();

        FirebaseRecyclerAdapter<Notifications, FriendRequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notifications, FriendRequestViewHolder>(
                options

        ) {

            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull Notifications model) {
                mUserReference.child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        holder.setDetails(user.userName);
                        holder.cancel_btn_request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(model.getReqId())){
                                            DataSnapshot Child = dataSnapshot.child(model.getReqId());
                                            Child.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        holder.accept_btn_request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat date_format = new SimpleDateFormat("MMMM. dd, yyyy");
                                String current_date = date_format.format(calendar.getTime());
                                mFriendsDatabase.child(mCurrentUserId).child(model.getFrom()).child("date").setValue(current_date);
                                mFriendsDatabase.child(model.getFrom()).child(mCurrentUserId).child("date").setValue(current_date);
                                mQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(model.getReqId())){
                                            DataSnapshot Child = dataSnapshot.child(model.getReqId());
                                            Toast.makeText(getActivity().getApplicationContext(),"You and "+user.userName+" are now Friends",Toast.LENGTH_LONG).show();
                                            Child.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            @NonNull
            @Override
            public RequestsFragment.FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_requests_view, viewGroup, false);

                return new RequestsFragment.FriendRequestViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        do {
            count++;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    firebaseRecyclerAdapter.notifyDataSetChanged();
                }
            }, 1000);
        }
        while (options == null && count == 3);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        mRequestsRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    //adapter

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView accept_btn_request, cancel_btn_request;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            accept_btn_request = mView.findViewById(R.id.accept_btn_request);
            cancel_btn_request = mView.findViewById(R.id.cancel_btn_request);
        }


        public void setDetails(String from) {

            TextView requestTextView = mView.findViewById(R.id.text_view_request);
            requestTextView.setText("You got a friend request from " + from);

        }
    }
}
