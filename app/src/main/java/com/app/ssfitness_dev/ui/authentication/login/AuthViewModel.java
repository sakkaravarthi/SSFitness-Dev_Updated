package com.app.ssfitness_dev.ui.authentication.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import static com.app.ssfitness_dev.utilities.Constants.TAG_AUTH_VIEW_MODEL;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;
import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.data.models.User;
import com.app.ssfitness_dev.ui.authentication.signup.SignupActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;

public class AuthViewModel extends ViewModel {

    /* To observe the changes in UI, we need Live data
     * To communicate with repository we need to connect Repository and Viewmodel
     */

    public String userEmail;
    public String userPassword;
    private AuthRepository authRepository;

    LiveData<User> authenticateGoogleUserLiveData;
    LiveData<User> createdGoogleUserLiveData;
    LiveData<String> loginFirebaseUserLiveData;


    public AuthViewModel() {
        super();
        authRepository = new AuthRepository();
    }

    //Communicating to firebase via Repository
    void authenticateGoogleUser(AuthCredential googleAuthCredential) {
        authenticateGoogleUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    void createNewGoogleUser(User authenticatedUser) {
        createdGoogleUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }

    void loginFirebaseUser(String userEmail, String userPassword)
    {
        loginFirebaseUserLiveData = authRepository.firebaseSignIn(userEmail, userPassword);
    }

    //Forgot Password
    public void forgotPassword(View view){

        AlertDialog myAlertdialog;

        View mDialogView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.alertdialog_forgetpwd, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
        builder.setTitle("Forgot Password")
                .setView(mDialogView)
                .setIcon(R.drawable.ic_email)
                .setCancelable(true)
        .setBackground(view.getResources().getDrawable(R.drawable.alert_dialog_bg))
        .setMessage("Enter your registered Email ID to receive the forgot password link");

        myAlertdialog = builder.create();
        myAlertdialog.show();

        Button send = mDialogView.findViewById(R.id.button_send);
        Button dismiss = mDialogView.findViewById(R.id.button_dismiss);

        send.setOnClickListener(sendClick -> {
            EditText dialogEmail = mDialogView.findViewById(R.id.dialogEmail);
            if(TextUtils.isEmpty(dialogEmail.getText())){
                dialogEmail.setError("Please enter your Email ID");
                logMessage(TAG_AUTH_VIEW_MODEL,"Email Field empty");
            }
            else
            {
                String email = dialogEmail.getText().toString();
                logMessage(TAG_AUTH_VIEW_MODEL,"Forgot password link sent to "+ email);
            }
        });

        dismiss.setOnClickListener(view1 -> myAlertdialog.dismiss());
    }

    public void goToSignup(View view){
        Intent intent = new Intent(view.getContext(), SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        view.getContext().startActivity(intent);
    }


}
