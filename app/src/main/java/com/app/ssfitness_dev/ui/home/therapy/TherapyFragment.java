package com.app.ssfitness_dev.ui.home.therapy;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.chat.ChatFragment;

public class TherapyFragment extends Fragment {

    private TherapyViewModel mViewModel;

    public static TherapyFragment newInstance() {
        return new TherapyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.therapy_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TherapyViewModel.class);
        // TODO: Use the ViewModel
    }


}
