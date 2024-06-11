package com.example.bodyboost.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.bodyboost.R;
import com.example.bodyboost.databinding.ActivityLoginBinding;
import com.example.bodyboost.main.MainActivity;
import com.example.bodyboost.model.HelperClass;
import com.example.bodyboost.model.UsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding; // Binding object for the layout
    String email, password; // Variables to hold email and password
    ProgressDialog progressDialog; // Progress dialog for showing loading state
    FirebaseAuth auth; // Firebase authentication instance
    DatabaseReference dbRefUsers; // Database reference for "Users" node

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater()); // Inflate the layout using data binding
        setContentView(binding.getRoot()); // Set the content view to the root view of the binding
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black)); // Set status bar color to black

        progressDialog = new ProgressDialog(this); // Create a new progress dialog
        progressDialog.setTitle(getString(R.string.app_name)); // Set the title of the progress dialog
        progressDialog.setMessage("Please wait..."); // Set the message of the progress dialog
        progressDialog.setCancelable(false); // Make the progress dialog non-cancelable

        auth = FirebaseAuth.getInstance(); // Get Firebase authentication instance
        dbRefUsers = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Users"); // Get database reference for "Users" node

        // Set click listener for register text view
        binding.llRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // Start RegisterActivity
            }
        });

        // Set click listener for login button
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()) { // Check if input fields are valid
                    progressDialog.show(); // Show progress dialog
                    // Sign in with email and password
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                // Retrieve user data from Firebase Realtime Database
                                dbRefUsers.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            HelperClass.users = snapshot.getValue(UsersModel.class); // Store user data in HelperClass
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class)); // Start MainActivity
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // Apply fade animation
                                            showMessage("Successfully Login"); // Show success message
                                            progressDialog.dismiss(); // Dismiss progress dialog
                                            finish(); // Finish LoginActivity
                                        } else {
                                            progressDialog.dismiss(); // Dismiss progress dialog
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss(); // Dismiss progress dialog
                                        Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show(); // Show error message
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage(e.getMessage()); // Show failure message
                            progressDialog.dismiss(); // Dismiss progress dialog
                        }
                    });

                }
            }
        });

    }

    // Method to validate input fields
    private Boolean isValidated(){
        email = binding.etEmail.getText().toString().trim(); // Get email from edit text
        password = binding.etPassword.getText().toString().trim(); // Get password from edit text

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

        return true;
    }

    // Method to show toast message
    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); // Show toast message
    }

}

