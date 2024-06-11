package com.example.bodyboost.main.fragments.hydration;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bodyboost.R;
import com.example.bodyboost.databinding.FragmentSetHydrationBinding;
import com.example.bodyboost.model.CaloriesModel;
import com.example.bodyboost.model.HelperClass;
import com.example.bodyboost.model.HydrationModel;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetHydrationFragment extends Fragment {
    private FragmentSetHydrationBinding binding;
    String title, date, goalHydration, currentHydration, status = "";
    HydrationModel updatedModel;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefHydration;
    Boolean isAlreadyAdded = false;

    public SetHydrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            updatedModel = (HydrationModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSetHydrationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        dbRefHydration = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Hydration");

        // Set the label and visibility for the top bar
        binding.rlTop.tvLabel.setText("Hydration Goal");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);

        // Handle the back button click event
        binding.rlTop.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        // Get current date
        getCurrentDate();

        // Handle the "Set Goal" button click event
        binding.btnSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate input fields
                if (isValidated()) {
                    progressDialog.show();
                    // Determine status based on goal and current hydration
                    if (Integer.valueOf(goalHydration).equals(Integer.valueOf(currentHydration))) {
                        status = "Completed";
                    } else {
                        status = "Pending";
                    }
                    // Check if the hydration goal is being updated
                    if (updatedModel != null) {
                        // Update the hydration goal in the database
                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("title", title);
                        update.put("goalHydration", goalHydration);
                        update.put("currentHydration", currentHydration);
                        update.put("status", status);
                        dbRefHydration.child(auth.getCurrentUser().getUid()).child(updatedModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            showMessage("Updated Successfully");
                            updatedModel.setTitle(title);
                            updatedModel.setGoalHydration(goalHydration);
                            updatedModel.setCurrentHydration(currentHydration);
                            updatedModel.setStatus(status);
                            Navigation.findNavController(view).navigateUp();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            showMessage(e.getMessage());
                        });
                    } else {
                        // Check if hydration goal for today is already set
                        if (isAlreadyAdded) {
                            progressDialog.dismiss();
                            showMessage("You have already set a goal for today, please update that");
                        } else {
                            // Add a new hydration goal to the database
                            String hydrationId = dbRefHydration.push().getKey();
                            HydrationModel hydrationModel = new HydrationModel(hydrationId, auth.getCurrentUser().getUid(), title, date, goalHydration, currentHydration, status);
                            dbRefHydration.child(auth.getCurrentUser().getUid()).child(hydrationId).setValue(hydrationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    // Method to validate input fields
    private Boolean isValidated() {
        goalHydration = binding.etTarget.getText().toString().trim();
        currentHydration = binding.etCurrent.getText().toString().trim();
        title = binding.etTitle.getText().toString().trim();

        if (title.isEmpty()) {
            showMessage("Please enter title");
        }

        if (currentHydration.isEmpty()) {
            currentHydration = "0";
        }

        if (goalHydration.isEmpty()) {
            showMessage("Please enter goal hydration");
            return false;
        }
        if (Integer.parseInt(goalHydration) < Integer.parseInt(currentHydration)) {
            showMessage("Please enter goal hydration equals or greater than current hydration");
            return false;
        }

        return true;
    }

    // Method to show toast message
    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        // If updating existing hydration goal, populate fields with existing data
        if (updatedModel != null) {
            binding.btnSetGoal.setText("Update Goal");
            binding.etCurrent.setText(updatedModel.getCurrentHydration());
            binding.etTarget.setText(updatedModel.getGoalHydration());
            binding.etTitle.setText(updatedModel.getTitle());
        } else {
            // Check if hydration goal for today is already set
            progressDialog.show();
            dbRefHydration.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                HydrationModel model = ds.getValue(HydrationModel.class);
                                if (date.equals(model.getDate())) {
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
