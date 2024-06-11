package com.example.bodyboost.main.fragments.calories;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bodyboost.R;
import com.example.bodyboost.adapter.CaloriesAdapter;
import com.example.bodyboost.databinding.FragmentSetCaloriesBinding;
import com.example.bodyboost.main.FatActivity;
import com.example.bodyboost.main.WeightActivity;
import com.example.bodyboost.model.CaloriesModel;
import com.example.bodyboost.model.HelperClass;
import com.example.bodyboost.model.WorkoutModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SetCaloriesFragment extends Fragment {
    private FragmentSetCaloriesBinding binding;
    String title, date, goalCalories, currentCalories, status = "";
    CaloriesModel updatedModel;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefCalories;
    Boolean isAlreadyAdded = false;

    public SetCaloriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            updatedModel = (CaloriesModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSetCaloriesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefCalories = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Calories");

        binding.rlTop.tvLabel.setText("Calories Goal");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        getCurrentDate();

        binding.btnSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()) {
                    progressDialog.show();
                    if (Integer.valueOf(goalCalories).equals(Integer.valueOf(currentCalories))) {
                        status = "Completed";
                    } else {
                        status = "Pending";
                    }
                    if (updatedModel != null) {
                        // Update existing data
                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("title", title);
                        update.put("targetCalories", goalCalories);
                        update.put("currentCalories", currentCalories);
                        update.put("status", status);
                        dbRefCalories.child(auth.getCurrentUser().getUid()).child(updatedModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            showMessage("Updated Successfully");
                            updatedModel.setTitle(title);
                            updatedModel.setTargetCalories(goalCalories);
                            updatedModel.setCurrentCalories(currentCalories);
                            updatedModel.setStatus(status);
                            Navigation.findNavController(view).navigateUp();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            showMessage(e.getMessage());
                        });
                    } else {
                        if (isAlreadyAdded) {
                            progressDialog.dismiss();
                            showMessage("You have already set a goal for today, please update that");
                        } else {
                            // Add new data
                            String caloriesId = dbRefCalories.push().getKey();
                            CaloriesModel caloriesModel = new CaloriesModel(caloriesId, auth.getCurrentUser().getUid(), title, date, goalCalories, currentCalories, status);
                            dbRefCalories.child(auth.getCurrentUser().getUid()).child(caloriesId).setValue(caloriesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    showMessage("Set Successfully");
                                    Navigation.findNavController(view).navigateUp();
                                }
                            }).addOnFailureListener((OnFailureListener) e -> {
                                progressDialog.dismiss();
                                showMessage(e.getMessage());
                            });
                        }

                    }
                }
            }
        });
    }

    // Method to get the current date
    private void getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        date = sdf.format(new Date());
    }

    // Method to validate input data
    private Boolean isValidated() {
        goalCalories = binding.etTarget.getText().toString().trim();
        currentCalories = binding.etCurrentCalories.getText().toString().trim();
        title = binding.etTitle.getText().toString().trim();
        if (currentCalories.isEmpty()) {
            currentCalories = "0";
        }

        if (goalCalories.isEmpty()) {
            showMessage("Please enter goal steps");
            return false;
        }
        if (title.isEmpty()) {
            showMessage("Please enter title");
            return false;
        }
        if (Integer.parseInt(goalCalories) < Integer.parseInt(currentCalories)) {
            showMessage("Please enter goal calories equals or greater than current calories");
            return false;
        }

        return true;
    }

    // Method to display a toast message
    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        if (updatedModel != null) {
            // If updating existing data, set UI components accordingly
            binding.btnSetGoal.setText("Update Calories");
            binding.etCurrentCalories.setText(updatedModel.getCurrentCalories());
            binding.etTarget.setText(updatedModel.getTargetCalories());
            binding.etTitle.setText(updatedModel.getTitle());
        } else {
            // If adding new data, check if there's already a goal set for today
            progressDialog.show();
            dbRefCalories.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                CaloriesModel model = ds.getValue(CaloriesModel.class);
                                if (date.equals(model.getDate())) {
                                    // If a goal for today already exists, set the flag
                                    isAlreadyAdded = true;
                                    break;
                                }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
