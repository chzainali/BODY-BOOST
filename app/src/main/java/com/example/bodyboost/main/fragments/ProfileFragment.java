package com.example.bodyboost.main.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bodyboost.R;
import com.example.bodyboost.auth.LoginActivity;
import com.example.bodyboost.auth.RegisterActivity;
import com.example.bodyboost.databinding.FragmentProfileBinding;
import com.example.bodyboost.main.FatActivity;
import com.example.bodyboost.main.WaistActivity;
import com.example.bodyboost.main.WeightActivity;
import com.example.bodyboost.model.HelperClass;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding; // Binding object for the layout

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the title and visibility for the top bar
        binding.rlTop.tvLabel.setText("Profile");
        binding.rlTop.ivRight.setVisibility(View.VISIBLE);

        // Handle click on the right icon in the top bar (Logout)
        binding.rlTop.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to LoginActivity and clear the back stack
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Handle click on the Weight icon
        binding.ivWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to WeightActivity with information from the profile
                Intent intent = new Intent(requireContext(), WeightActivity.class);
                intent.putExtra("from", "profile");
                startActivity(intent);
            }
        });

        // Handle click on the Fat icon
        binding.ivFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to FatActivity with information from the profile
                Intent intent = new Intent(requireContext(), FatActivity.class);
                intent.putExtra("from", "profile");
                startActivity(intent);
            }
        });

        // Handle click on the Waist icon
        binding.ivWaist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to WaistActivity with information from the profile
                Intent intent = new Intent(requireContext(), WaistActivity.class);
                intent.putExtra("from", "profile");
                startActivity(intent);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        // Update UI with user's weight, body fat, and waist information
        if (!HelperClass.users.getCurrentWeight().contentEquals("") && !HelperClass.users.getTargetWeight().contentEquals("")){
            binding.etCurrentWeight.setText(HelperClass.users.getCurrentWeight()+ " kg");
            binding.etGoalWeight.setText(HelperClass.users.getTargetWeight()+ " kg");
        }

        if (!HelperClass.users.getCurrentBodyFat().contentEquals("") && !HelperClass.users.getTargetBodyFat().contentEquals("")){
            binding.etCurrentFat.setText(HelperClass.users.getCurrentBodyFat()+ " %");
            binding.etGoalFat.setText(HelperClass.users.getTargetBodyFat()+ " %");
        }

        if (!HelperClass.users.getCurrentWaist().contentEquals("") && !HelperClass.users.getTargetWaist().contentEquals("")){
            binding.etCurrentWaist.setText(HelperClass.users.getCurrentWaist()+ " inches");
            binding.etGoalWaist.setText(HelperClass.users.getTargetWaist()+ " inches");
        }
    }
}
