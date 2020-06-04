package com.app.ssfitness_dev.ui.home.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.data.models.User;
import com.app.ssfitness_dev.ui.home.chat.AvailableUsers.UsersViewHolder;
import com.app.ssfitness_dev.ui.home.userprofiles.UserProfileActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    EditText editTextSearchUsers;
    ImageButton buttonsearchUsers;
    private NavController navController;
    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private View mMainView;
    FirebaseRecyclerOptions<Friends> options;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendsList = mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrentUser);
        //For offline purpose
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        //For offline purpose
        mUsersDatabase.keepSynced(true);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFriendsDatabase, Friends.class).build();


        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewHolderAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends friends) {

                holder.setDate(friends.getDate());

                String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("userName").getValue().toString();
                        String userProfile = dataSnapshot.child("photoUrl").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }


                        holder.setName(userName);
                        if (userProfile != null) {
                            holder.setPhoto(userProfile, getContext());
                        }


                        holder.mView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //Alert Dialog
                                CharSequence options[] = new CharSequence[]
                                        {"Open Profile", "Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click event for each item
                                        if (i == 0) {
                                            Intent profileIntent = new Intent(getContext(), UserProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);
                                        }

                                        if (i == 1) {
                                            Intent chatIntent = new Intent(getContext(), UserChatActivity.class);
                                            chatIntent.putExtra("user_name", userName);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            startActivity(chatIntent);
                                        }

                                    }
                                });

                                builder.show();
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
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_search_single_item, viewGroup, false);

                return new FriendsViewHolder(view);
            }
        };

        friendsRecyclerViewHolderAdapter.startListening();
        mFriendsList.setAdapter(friendsRecyclerViewHolderAdapter);
    }

    //Adapter
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setDate(String date) {

            TextView userDateView = mView.findViewById(R.id.user_single_activity_level);
            userDateView.setText(date);
        }

        public void setName(String name) {

            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setPhoto(String userProfileUrl, Context applicationContext) {
            ImageView userProfileView = mView.findViewById(R.id.user_single_profile);

            if (applicationContext != null) {
                Glide.with(mView.getContext()).load(userProfileUrl).into(userProfileView);
            }

            //Glide.with(mView.getContext()).load(userProfileUrl).into(userProfileView);

        }

        public void setUserOnline(String online_icon) {

            ImageView userOnlineView = mView.findViewById(R.id.user_single_online_status);

            if (online_icon.equals("true")) {

                userOnlineView.setColorFilter(mView.getContext().getResources().getColor(R.color.colorPrimary));
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setColorFilter(mView.getContext().getResources().getColor(R.color.colorRed));
                userOnlineView.setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextSearchUsers = view.findViewById(R.id.btn_search_users);
        buttonsearchUsers = view.findViewById(R.id.button_search);

        navController = Navigation.findNavController(view);

        buttonsearchUsers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String editTextQuery = editTextSearchUsers.getText().toString();
                Bundle args = new Bundle();
                args.putString("searchName", editTextQuery);
                Navigation.findNavController(getView()).navigate(R.id.availableUsers,args);
            }
        });


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
}
