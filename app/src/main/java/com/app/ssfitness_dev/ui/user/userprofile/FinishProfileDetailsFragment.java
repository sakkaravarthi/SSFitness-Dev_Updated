package com.app.ssfitness_dev.ui.user.userprofile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import nl.dionsegijn.konfetti.KonfettiView;

import static com.app.ssfitness_dev.utilities.Constants.USERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishProfileDetailsFragment extends Fragment {

    Button continueToHome;
    KonfettiView konfettiView;
    public FinishProfileDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finish_profile_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        continueToHome = view.findViewById(R.id.button_finish_to_home);
        konfettiView = view.findViewById(R.id.viewKonfetti);

        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED)
                .setDirection(0.0, 359.0)
                .setSpeed(4f, 7f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1600L)
                .setPosition(50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(200, 5000L);


        continueToHome.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }
}
