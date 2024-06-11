package com.example.bodyboost.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.bodyboost.R;
import com.example.bodyboost.databinding.ActivityRegisterBinding;
import com.example.bodyboost.main.WeightActivity;
import com.example.bodyboost.model.HelperClass;
import com.example.bodyboost.model.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding; // Binding object for the layout
    String name, email, password, age, gender = ""; // Variables to hold user details
    ProgressDialog progressDialog; // Progress dialog for showing loading state
    FirebaseAuth auth; // Firebase authentication instance
    DatabaseReference dbRefUsers; // Database reference for "Users" node

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater()); // Inflate the layout using data binding
        setContentView(binding.getRoot()); // Set the content view to the root view of the binding
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black)); // Set status bar color to black

        progressDialog = new ProgressDialog(this); // Create a new progress dialog
        progressDialog.setTitle(getString(R.string.app_name)); // Set the title of the progress dialog
        progressDialog.setMessage("Please wait..."); // Set the message of the progress dialog
        progressDialog.setCancelable(false); // Make the progress dialog non-cancelable

        auth = FirebaseAuth.getInstance(); // Get Firebase authentication instance
        dbRefUsers = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Users"); // Get database reference for "Users" node

        // Set click listener for back image view
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the activity
            }
        });

        // Set click listener for login text view
        binding.llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Start LoginActivity
            }
        });

        // Set radio group listener for gender selection
        binding.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);

                if (selectedRadioButton != null) {
                    gender = selectedRadioButton.getText().toString(); // Get selected gender
                }
            }
        });

        // Set click listener for register button
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()){
                    registerUser(); // Call method to register the user
                }
            }
        });

    }

    // Method to register the user
    private void registerUser() {
        progressDialog.show(); // Show progress dialog
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            UsersModel model = new UsersModel(auth.getCurrentUser().getUid(), name,email, password, age, gender, "", "", "", "", "", "");
            dbRefUsers.child(auth.getCurrentUser().getUid()).setValue(model).addOnCompleteListener(task -> {
                progressDialog.dismiss(); // Dismiss progress dialog
                showMessage("Registered Successfully"); // Show success message
                HelperClass.users = model; // Store user data in HelperClass
                Intent intent = new Intent(RegisterActivity.this, WeightActivity.class);
                intent.putExtra("from", "auth");
                startActivity(intent); // Start WeightActivity
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // Apply fade animation
                finish(); // Finish RegisterActivity
            }).addOnFailureListener(e -> {
                progressDialog.dismiss(); // Dismiss progress dialog
                showMessage(e.getLocalizedMessage()); // Show error message
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss(); // Dismiss progress dialog
            showMessage(e.getLocalizedMessage()); // Show error message
        });

    }

    // Method to validate input fields
    private Boolean isValidated(){
        name = binding.etUsername.getText().toString().trim(); // Get username from edit text
        email = binding.etEmail.getText().toString().trim(); // Get email from edit text
        password = binding.etPassword.getText().toString().trim(); // Get password from edit text
        age = binding.etAge.getText().toString().trim(); // Get age from edit text

        if (name.isEmpty()){
            showMessage("Please enter username"); // Show error message
            return false;
        }
        if (email.isEmpty()){
            showMessage("Please enter email"); // Show error message
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format"); // Show error message
            return false;
        }
        if (password.isEmpty()){
            showMessage("Please enter password"); // Show error message
            return false;
        }
        if (age.isEmpty()){
            showMessage("Please enter age"); // Show error message
            return false;
        }
        if (gender.isEmpty()){
            showMessage("Please select gender"); // Show error message
            return false;
        }

        return true;
    }

    // Method to show toast message
    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); // Show toast message
    }

}
