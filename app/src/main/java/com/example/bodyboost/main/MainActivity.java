package com.example.bodyboost.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.bodyboost.R;
import com.example.bodyboost.databinding.ActivityMainBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding; // Binding object for the layout
    NavHostFragment navHostFragment; // Fragment managing navigation
    NavController navController; // Controller for navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Inflate the layout using data binding
        setContentView(binding.getRoot()); // Set the content view to the root view of the binding
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black)); // Set status bar color to black

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController(); // Get NavController from the NavHostFragment

        setBottomNavigation(); // Call method to set up bottom navigation
    }

    private void setBottomNavigation() {

        // Add tabs to the bottom navigation
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_snowboarding_24));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_baseline_person_24));

        // Navigate to home fragment initially and show the corresponding tab
        navController.navigate(R.id.homeFragment);
        binding.bottomNav.bottomNavigation.show(1, true);

        // Set onClickMenuListener to handle clicks on bottom navigation tabs
        binding.bottomNav.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()) {
                    case 1:
                        navController.navigate(R.id.homeFragment);
                        break;

                    case 2:
                        navController.navigate(R.id.trackCategoriesFragment);
                        break;

                    case 3:
                        navController.navigate(R.id.profileFragment);
                        break;
                }

                return null;
            }
        });

        // Set onClickListener for the custom home icon to navigate to home fragment
        binding.bottomNav.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.homeFragment);
                binding.bottomNav.bottomNavigation.show(1, true);
            }
        });

        // Set onClickListener for the custom tracking icon to navigate to tracking fragment
        binding.bottomNav.tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNav.bottomNavigation.show(2, true);
                navController.navigate(R.id.trackCategoriesFragment);
            }
        });

        // Set onClickListener for the custom profile icon to navigate to profile fragment
        binding.bottomNav.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.profileFragment);
                binding.bottomNav.bottomNavigation.show(3, true);
            }
        });
    }
}
