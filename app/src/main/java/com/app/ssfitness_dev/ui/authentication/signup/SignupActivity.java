package com.app.ssfitness_dev.ui.authentication.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.databinding.ActivitySignupBinding;
import com.app.ssfitness_dev.ui.user.userprofile.UserProfile;
import com.google.android.material.textfield.TextInputLayout;

import static com.app.ssfitness_dev.utilities.Constants.TAG_SIGNUP;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;

public class SignupActivity extends AppCompatActivity {

    private SignupViewModel signupViewModel;

    Button button_sign_up;
    EditText userEmail, userPassword, userConfirmPassword;
    TextInputLayout textInputLayoutEmail, textInputLayoutPassword, textInputLayoutConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySignupBinding activitySignupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        activitySignupBinding.setViewmodel(new SignupViewModel());
        activitySignupBinding.executePendingBindings();

        //Init View model to observe live data
        initSignupViewModel();

        //UI initialization
        initUiLayout();

        //Sign up button click
        signupBtnClick();

        //Clear Edit text email errors when user input some text
        clearErrors();
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

        userConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayoutConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void signupBtnClick() {
        button_sign_up.setOnClickListener(view -> {

            if(TextUtils.isEmpty(userEmail.getText().toString().trim()))
            {
                textInputLayoutEmail.setError("Please enter your Email ID");
            }
            if(!isEmailValid(userEmail.getText().toString().trim()))
            {
                textInputLayoutEmail.setError("Please enter a valid Email ID");
            }
            else if(TextUtils.isEmpty(userPassword.getText()))
            {
                textInputLayoutPassword.setError("Please enter your password");
            }
            else if(userPassword.getText().length() < 6)
            {
                textInputLayoutPassword.setError("Minimum characters are 6");
            }
            else if(TextUtils.isEmpty(userConfirmPassword.getText()))
            {
                textInputLayoutConfirmPassword.setError("Minimum characters are 6");
            }
            else if(userConfirmPassword.getText().length() < 6)
            {
                textInputLayoutConfirmPassword.setError("Minimum characters are 6");
            }

            else if(!userConfirmPassword.getText().toString().trim()
                    .equals(userPassword.getText().toString().trim())){
                textInputLayoutConfirmPassword.setError("Passwords do not match");
            }
            else
            {
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();
                signUpWithFirebase(email,password);
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void initUiLayout() {
        button_sign_up = findViewById(R.id.button_sign_up);
        userEmail = findViewById(R.id.edit_text_signup_email);
        userPassword = findViewById(R.id.edit_text_signup_password);
        userConfirmPassword = findViewById(R.id.edit_text_signup_confirm_password);

        textInputLayoutEmail = findViewById(R.id.text_signup_input_email);
        textInputLayoutPassword = findViewById(R.id.text_signup_input_password);
        textInputLayoutConfirmPassword = findViewById(R.id.text_signup_input_confirm);
    }

    private void signUpWithFirebase(String email, String password) {
        signupViewModel.signupWithFirebaseEmail(email,password);

        signupViewModel.createdFirebaseUserLiveData.observe(this, createdUser -> {
            if (createdUser.isCreated) {
                Toast.makeText(this, "Successfully registered. Complete the profile.", Toast.LENGTH_SHORT).show();
                logMessage(TAG_SIGNUP, "User registration successful with Firebase. Redirect to Complete Profile");
                completeUserProfile();
            }
            else {
                Toast.makeText(this, "Email already exists. Please use different email ID", Toast.LENGTH_SHORT).show();
                logMessage(TAG_SIGNUP, "User Email already exists. Try different Email ID.");
            }
        });
    }

    private void completeUserProfile() {
        Intent intent = new Intent(this, UserProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void initSignupViewModel() {
        logMessage(TAG_SIGNUP, "Initiating Signup View Model");
        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
    }

}
