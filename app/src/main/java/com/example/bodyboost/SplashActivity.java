package com.example.bodyboost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.bodyboost.auth.LoginActivity;
import com.example.bodyboost.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding; // Binding object for the layout
    Animation fromTop; // Animation object for the 'fromtop' animation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater()); // Inflate the layout using data binding
        setContentView(binding.getRoot()); // Set the content view to the root view of the binding
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black)); // Set status bar color to black

        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop); // Load 'fromtop' animation from resources
        binding.cvLogo.setAnimation(fromTop); // Apply the 'fromtop' animation to the logo CardView

        binding.btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the LoginActivity when the 'Get Started' button is clicked
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
        });
    }
}
