package com.app.ssfitness_dev.ui.authentication.signup;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.app.ssfitness_dev.data.models.User;
import com.app.ssfitness_dev.ui.authentication.login.LoginActivity;

public class SignupViewModel extends ViewModel {

    private SignupRepository signupRepository;

    //Another Live data Object
    LiveData<User> createdFirebaseUserLiveData;

    public SignupViewModel() {
        super();
        signupRepository = new SignupRepository();
    }

    public void goToLogin(View view){
        Intent intent = new Intent(view.getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        view.getContext().startActivity(intent);
    }

    void signupWithFirebaseEmail(String email, String password){
        createdFirebaseUserLiveData = signupRepository.firebaseSignUp(email,password);
    }


}
