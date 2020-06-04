package com.app.ssfitness_dev.utilities;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class HelperClass extends AppCompatActivity {


    public static void logMessage(String Tag, String errorMessage) {
        Log.d(Tag, errorMessage);
    }

    public static void makeSnackBarMessage(View layout,String message){
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }
}
