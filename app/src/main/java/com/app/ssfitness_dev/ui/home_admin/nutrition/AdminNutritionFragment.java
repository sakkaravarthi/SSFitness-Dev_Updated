package com.app.ssfitness_dev.ui.home_admin.nutrition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ssfitness_dev.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminNutritionFragment extends Fragment {

    public AdminNutritionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_nutrition, container, false);
    }
}
