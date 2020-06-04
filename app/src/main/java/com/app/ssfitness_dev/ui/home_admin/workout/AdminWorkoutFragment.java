package com.app.ssfitness_dev.ui.home_admin.workout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ssfitness_dev.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminWorkoutFragment extends Fragment {

    public AdminWorkoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_workout, container, false);
    }
}
