package com.app.ssfitness_dev.ui.authentication.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.data.models.User;
import com.app.ssfitness_dev.databinding.ActivityLoginBinding;
import com.app.ssfitness_dev.ui.admin.AdminDashboardActivity;
import com.app.ssfitness_dev.ui.home.HomeActivity;
import com.app.ssfitness_dev.ui.splash.SplashActivity;
import com.app.ssfitness_dev.ui.user.userprofile.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.app.ssfitness_dev.utilities.Constants.RC_SIGN_IN;
import static com.app.ssfitness_dev.utilities.Constants.TAG_AUTH_REPOSITORY;
import static com.app.ssfitness_dev.utilities.Constants.TAG_LOGIN;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;
    ProgressBar progress_bar;
    EditText userEmail, userPassword;
    Button loginBtn;
    TextInputLayout textInputLayoutEmail, textInputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setViewmodel(new AuthViewModel());
        activityLoginBinding.executePendingBindings();

        if(connected()){
            //initializing login activity operations
            initAuthViewModel();

            //Binding all UI elements
            initLoginLayoutUi();

            //On Google Login Button Click
            initGoogleLoginClick();

            //Check for Google Authentication
            initGoogleSigninClient();

            //Initialize login Button click
            loginBtnClick();

            //Clear Edit text email errors when user input some text
            clearErrors();

        }
        else {
            Toast.makeText(this, "Internet Connection is required to use the application.", Toast.LENGTH_LONG).show();
        }


    }

    private boolean connected(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null && activeNetworkInfo.isConnected();
    }

    private void clearErrors() {
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayoutEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayoutPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void loginBtnClick() {

        loginBtn.setOnClickListener(view -> {
            //Add Valid Email check

            if(TextUtils.isEmpty(userEmail.getText().toString().trim()))
            {
                textInputLayoutEmail.setError("Please enter your Email ID");
            }
            if(!isEmailValid(userEmail.getText().toString().trim()))
            {
                textInputLayoutEmail.setError("Please enter a valid Email ID");
            }
            else if (TextUtils.isEmpty(userPassword.getText())){
                textInputLayoutPassword.setError("Please enter your password");
            }
            else if (userPassword.getText().toString().trim().length() <6)
            {
                textInputLayoutPassword.setError("Please check your password");
            }
            else {
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                authViewModel.loginFirebaseUser(email, password);
                if(authViewModel != null){
                    authViewModel.loginFirebaseUserLiveData.observe(this, message ->{

                        if(message.equals("success")){
                            goToHomeActivity();
                        }else if(message.equals("incomplete")){
                            completeUserProfile();
                        }
                        else if(message.equals("failure")){
                            Toast.makeText(this, "You have entered wrong password. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        else if(message.equals("dataerror")){
                            Toast.makeText(this, "Server down...Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Init Ui elements
    private void initLoginLayoutUi() {
        progress_bar = findViewById(R.id.progress_bar);
        //Checking Login Errors
        textInputLayoutEmail = findViewById(R.id.text_input_email);
        textInputLayoutPassword = findViewById(R.id.text_input_password);
        userEmail = findViewById(R.id.edit_text_email);
        userPassword = findViewById(R.id.edit_text_password);
        loginBtn = findViewById(R.id.button_login);

    }

    //Go to home activity
    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Initialize Google login click
    private void initGoogleLoginClick() {
        Button googleSignInButton = findViewById(R.id.button_google_login);
        googleSignInButton.setOnClickListener(v -> googleSignIn());
    }

    private void initAuthViewModel() {
        logMessage(TAG_LOGIN, "Initializing AuthViewModel");
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void initGoogleSigninClient() {
        logMessage(TAG_LOGIN, "Initializing GoogleSigninClient");
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    //Capture Button Click event for Google Login
    private void googleSignIn() {
        progress_bar.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //When a Google account is selected, onActivityResult is triggered
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                    progress_bar.setVisibility(View.GONE);
                    logMessage(TAG_LOGIN, "onActivityResult - Get Google credentials");
                }
            } catch (ApiException e) {
                logMessage(TAG_LOGIN,e.getMessage());
                progress_bar.setVisibility(View.GONE);
            }
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);

         //To login to firebase
        logMessage(TAG_LOGIN, "getGoogleAuthCredential - Send credentials to Firebase");
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    /* Once credentials are received, login to firebase
     * First set Google credentials in our AuthViewModel and then start observing the changes
     */
    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        logMessage(TAG_LOGIN, "signInWithGoogleAuthCredential - Sent to authViewModel for livedata");
        authViewModel.authenticateGoogleUser(googleAuthCredential);



        authViewModel.authenticateGoogleUserLiveData.observe(this, authenticatedUser -> {
            if (authenticatedUser.isNew) {
                logMessage(TAG_LOGIN,"New Google User - Create a new user");
                createNewGoogleUser(authenticatedUser);
            }
            else if(!authenticatedUser.profileComplete){
                logMessage(TAG_LOGIN,"Check database if the profile is complete");
                completeUserProfile();
            }
            else if(!authenticatedUser.admin){
                logMessage(TAG_LOGIN,"Profile is complete - Redirecting to HomeScreen");
                goToMainActivity();
            }
            else {
                logMessage(TAG_LOGIN,"Admin Profile is complete- Redirecting to Admin Dashboard");
                goToAdminActivity();
            }
        });
    }

    private void goToAdminActivity() {
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void completeUserProfile() {
        Intent intent = new Intent(this, UserProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //After the mutable Live data is observed, based on user type ( new/ old ) accounts are created.
    private void createNewGoogleUser(User authenticatedUser) {
        authViewModel.createNewGoogleUser(authenticatedUser);

        authViewModel.createdGoogleUserLiveData.observe(this, user -> {
            if (user.isCreated) {
                logMessage(TAG_LOGIN,"Registration Successful for "+ user.userName);
            }
            completeUserProfile();
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
