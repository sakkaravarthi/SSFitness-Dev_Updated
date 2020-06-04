package com.app.ssfitness_dev.ui.home.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.app.ssfitness_dev.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileSettingsActivity extends AppCompatActivity {

    private TextView tv_name, tv_email;
    ImageView imageview_profile;
    private Button btn_membership, btn_settings, btn_faq, btn_aboutus, btn_terms, btn_privacy, btn_logout;
    MaterialToolbar backToolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String currentUid, profile_name, profile_email, profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initUI();
        checkDb();


    }

    private void checkDb() {
        if(firebaseAuth!= null)
        {
            userRef = db.collection("users").document(currentUid);

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        profile_name = documentSnapshot.getString("userName");
                        profile_email = documentSnapshot.getString("userEmail");
                        profile_image = documentSnapshot.getString("photoUrl");

                        tv_name.setText(profile_name);
                        tv_email.setText(profile_email);

                        if(profile_image!=null){
                            Glide.with(getApplication()).load(profile_image).placeholder(R.drawable.ic_account).into(imageview_profile);
                        }

                    }
                    else {
                        Log.d("ProfileSettingsActivity", "Document do not exist");
                    }
                }
            });

        }

    }


    private void initUI() {
        tv_name = findViewById(R.id.profile_name);
        tv_email = findViewById(R.id.profile_email);
        btn_aboutus = findViewById(R.id.button_aboutus);
        btn_faq = findViewById(R.id.button_aboutus);
        btn_membership = findViewById(R.id.button_membership);
        btn_privacy = findViewById(R.id.button_privacy);
        btn_settings = findViewById(R.id.button_settings);
        btn_terms = findViewById(R.id.button_terms);
        btn_logout = findViewById(R.id.button_logout);
        currentUid = firebaseAuth.getCurrentUser().getUid();
        imageview_profile = findViewById(R.id.profile_image);
        backToolbar = findViewById(R.id.topAppBar);

        onClickListeners();
    }

    private void onClickListeners() {

        backToolbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
