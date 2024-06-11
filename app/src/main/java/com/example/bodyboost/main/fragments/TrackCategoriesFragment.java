package com.example.bodyboost.main.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bodyboost.R;
import com.example.bodyboost.databinding.FragmentProfileBinding;
import com.example.bodyboost.databinding.FragmentTrackCategoriesBinding;

public class TrackCategoriesFragment extends Fragment {
    FragmentTrackCategoriesBinding binding;
    public static String category = ""; // Variable to store selected category

    public TrackCategoriesFragment() {
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
        binding = FragmentTrackCategoriesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the title for the top bar
        binding.rlTop.tvLabel.setText("Tracking");

        // Handle click on the Workout category
        binding.rlWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Workout";
                // Update UI to show the selected category
                binding.ivCheckWorkout.setImageResource(R.drawable.baseline_check_circle_24);
                binding.ivCheckCalories.setImageResource(R.drawable.ic_unchecked);
                binding.ivHydration.setImageResource(R.drawable.ic_unchecked);
            }
        });

        // Handle click on the Calories category
        binding.rlCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Calories";
                // Update UI to show the selected category
                binding.ivCheckCalories.setImageResource(R.drawable.baseline_check_circle_24);
                binding.ivCheckWorkout.setImageResource(R.drawable.ic_unchecked);
                binding.ivHydration.setImageResource(R.drawable.ic_unchecked);
            }
        });

        // Handle click on the Hydration category
        binding.rlHydration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Hydration";
                // Update UI to show the selected category
                binding.ivHydration.setImageResource(R.drawable.baseline_check_circle_24);
                binding.ivCheckWorkout.setImageResource(R.drawable.ic_unchecked);
                binding.ivCheckCalories.setImageResource(R.drawable.ic_unchecked);
            }
        });

        // Handle click on the "Add" button
        binding.rlAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category.isEmpty()) {
                    // Show a toast if no category is selected
                    Toast.makeText(requireContext(), "Please select first, what would you like to track.", Toast.LENGTH_SHORT).show();
                } else if (category.contentEquals("Workout")) {
                    // Navigate to setWorkoutFragment
                    Navigation.findNavController(view).navigate(R.id.action_trackCategoriesFragment_to_setWorkoutFragment);
                } else if (category.contentEquals("Calories")) {
                    // Navigate to setCaloriesFragment
                    Navigation.findNavController(view).navigate(R.id.action_trackCategoriesFragment_to_setCaloriesFragment);
                } else if (category.contentEquals("Hydration")) {
                    // Navigate to setHydrationFragment
                    Navigation.findNavController(view).navigate(R.id.action_trackCategoriesFragment_to_setHydrationFragment);
                }
            }
        });

        // Handle click on the "View Tracking" button
        binding.btnViewTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category.isEmpty()) {
                    // Show a toast if no category is selected
                    Toast.makeText(requireContext(), "Please select first, what would you like to track.", Toast.LENGTH_SHORT).show();
                } else if (category.contentEquals("Workout")) {
                    // Navigate to workoutFragment
                    Navigation.findNavController(view).navigate(R.id.action_trackCategoriesFragment_to_workoutFragment);
                } else if (category.contentEquals("Calories")) {
                    // Navigate to caloriesFragment
                    Navigation.findNavController(view).navigate(R.id.action_trackCategoriesFragment_to_caloriesFragment);
                } else if (category.contentEquals("Hydration")) {
                    // Navigate to hydrationFragment
                    Navigation.findNavController(view).navigate(R.id.action_trackCategoriesFragment_to_hydrationFragment);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Reset the selected category when the fragment resumes
        category = "";
    }
}
