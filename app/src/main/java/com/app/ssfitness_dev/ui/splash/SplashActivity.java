package com.app.ssfitness_dev.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.admin.AdminDashboardActivity;
import com.app.ssfitness_dev.ui.authentication.login.LoginActivity;
import com.app.ssfitness_dev.ui.home.HomeActivity;
import com.app.ssfitness_dev.ui.user.userprofile.UserProfile;

import static com.app.ssfitness_dev.utilities.Constants.TAG_SPLASH;

public class SplashActivity extends AppCompatActivity {
    SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initSplashViewModel();
        checkIfUserIsAuthenticated();
    }

    private void initSplashViewModel() {
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
    }

    private void checkIfUserIsAuthenticated() {
        splashViewModel.checkIfUserIsAuthenticated();

        splashViewModel.isUserAuthenticatedLiveData.observe(this, user -> {
            if (!user.isAuthenticated) {
                goToAuthInActivity();
                finish();
            } else {
                getUserFromDatabase(user.userID);
            }
        });
    }

    private void completeUserProfile() {
        Intent intent = new Intent(this, UserProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToAuthInActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void getUserFromDatabase(String userId) {
        splashViewModel.setUid(userId);

        splashViewModel.userLiveData.observe(this, user -> {

            Log.d(TAG_SPLASH, "User profile complete status: " + user.profileComplete);
            if(user.profileComplete && !user.admin){
                Log.d(TAG_SPLASH, "User profile complete going to home activity");
                goToMainActivity();
                finish();
            }
            else if (user.profileComplete){
                Log.d(TAG_SPLASH, "User is admin going to Admin activity");
                goToAdminActivity();
                finish();
            }
            else{
                Log.d(TAG_SPLASH, "User profile not complete, going to user profile completion");
                completeUserProfile();
                finish();
            }
        });

    }

    private void goToAdminActivity() {
        Intent intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
