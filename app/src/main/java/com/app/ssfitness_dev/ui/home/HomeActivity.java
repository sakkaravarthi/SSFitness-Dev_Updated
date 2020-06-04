package com.app.ssfitness_dev.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.data.models.User;
import com.app.ssfitness_dev.ui.authentication.login.LoginActivity;
import com.app.ssfitness_dev.ui.home.profile.ProfileSettingsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import static com.app.ssfitness_dev.utilities.Constants.USER;

public class HomeActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mUserRef;
    private DatabaseReference mRootRef;
    private GoogleSignInClient googleSignInClient;
    NavController navController;
    BottomNavigationView bottomNavigationView;
    MaterialToolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        // To check for Offline and online status
        if(mAuth.getCurrentUser()!=null){
            mUserRef = mRootRef.child("users").child(mAuth.getCurrentUser().getUid());
        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        mainToolbar = findViewById(R.id.toolbar);

       setSupportActionBar(mainToolbar);
       NavigationUI.setupWithNavController(mainToolbar, navController);
       NavigationUI.setupWithNavController(bottomNavigationView, navController);

        initGoogleSignInClient();
    }

    private User getUserFromIntent() {
        return (User) getIntent().getSerializableExtra(USER);
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            goToAuthInActivity();
        }
    }

    private void goToAuthInActivity() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean connected(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null && activeNetworkInfo.isConnected();
    }

    private void signOut() {
        mAuth.signOut();
        googleSignInClient.signOut();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(connected()) {
            mAuth.addAuthStateListener(this);
                mRootRef.child("users").child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");
        }
        else {
            goToAuthInActivity();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
       int id = menuItem.getItemId();
        switch (id){
           case R.id.toolbar_signout:
               Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

               signOut();
               return true;
            case R.id.toolbar_profile:
                Intent intent = new Intent(HomeActivity.this, ProfileSettingsActivity.class);
                startActivity(intent);
                return true;
       }
        return true;
    }

}
