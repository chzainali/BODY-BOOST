package com.example.bodyboost.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bodyboost.R;
import com.example.bodyboost.databinding.ActivityWaistBinding;
import com.example.bodyboost.model.HelperClass;
import com.example.bodyboost.model.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class WaistActivity extends AppCompatActivity {
    ActivityWaistBinding binding;
    String currentWaist, goalWaist;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    String checkFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this activity using view binding
        binding = ActivityWaistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Users");

        // Check if the activity is opened from authentication or another source
        if (getIntent().getExtras() != null) {
            checkFrom = getIntent().getStringExtra("from");
            // Show or hide skip and back options based on the source
            if (checkFrom.contentEquals("auth")){
                binding.tvSkip.setVisibility(View.VISIBLE);
            } else {
                binding.tvSkip.setVisibility(View.GONE);
                binding.ivBack.setVisibility(View.VISIBLE);
                // Set initial values for current and goal waist
                binding.etCurrentWaist.setText(HelperClass.users.getCurrentWaist());
                binding.etGoalWaist.setText(HelperClass.users.getTargetWaist());
            }
        }

        // Handle back button click event
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Handle skip button click event
        binding.tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaistActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Handle set button click event
        binding.btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate input and save data to Firebase
                if (isValidated()){
                    progressDialog.show();
                    UsersModel users = HelperClass.users;
                    users.setCurrentWaist(currentWaist);
                    users.setTargetWaist(goalWaist);
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put("currentWaist", currentWaist);
                    update.put("targetWaist", goalWaist);
                    dbRefUsers.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        showMessage("Successfully Saved");
                        HelperClass.users = users;
                        // If opened from authentication, navigate to MainActivity
                        if (checkFrom.contentEquals("auth")){
                            Intent intent = new Intent(WaistActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("from", "auth");
                            startActivity(intent);
                        }
                        finish();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        showMessage(e.getMessage());
                    });
                }
            }
        });
    }

    // Validate user input for current and goal waist
    private Boolean isValidated(){
        currentWaist = binding.etCurrentWaist.getText().toString().trim();
        goalWaist = binding.etGoalWaist.getText().toString().trim();

        if (currentWaist.isEmpty() || currentWaist.contentEquals("0")){
            showMessage("Please enter current waist");
            return false;
        }
        if (goalWaist.isEmpty() || goalWaist.contentEquals("0")){
            showMessage("Please enter goal waist");
            return false;
        }

        return true;
    }

    // Display a toast message
    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}