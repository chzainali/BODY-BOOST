package com.example.bodyboost.main.fragments.calories;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bodyboost.R;
import com.example.bodyboost.adapter.CaloriesAdapter;
import com.example.bodyboost.databinding.FragmentCaloriesBinding;
import com.example.bodyboost.model.CaloriesModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CaloriesFragment extends Fragment {
    private FragmentCaloriesBinding binding;
    CaloriesAdapter caloriesAdapter;
    List<CaloriesModel> list = new ArrayList<>();
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefCalories;

    public CaloriesFragment() {
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
        binding = FragmentCaloriesBinding.inflate(getLayoutInflater());

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

        // Set the label and visibility for the top bar
        binding.rlTop.tvLabel.setText("View Calories");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);

        // Handle the back button click event
        binding.rlTop.ivBack.setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        list.clear(); // Clear the existing list

        progressDialog.show();
        dbRefCalories.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    // Populate the list with data from the database
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            CaloriesModel model = ds.getValue(CaloriesModel.class);
                            list.add(model);
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (list.size() > 0) {
                        // If there are entries, set up the RecyclerView
                        binding.noDataFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                        binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));
                        caloriesAdapter = new CaloriesAdapter(list, requireContext(), (pos, status) -> {
                            // Handle item clicks in the adapter
                            CaloriesModel caloriesModel = list.get(pos);

                            if (status.contentEquals("next")) {
                                // Navigate to the next screen with data bundle
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", caloriesModel);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_caloriesFragment_to_setCaloriesFragment, bundle);
                            } else {
                                // Remove entry from the database and update the UI
                                dbRefCalories.child(auth.getCurrentUser().getUid()).child(caloriesModel.getId()).removeValue();
                                caloriesAdapter.notifyItemRemoved(pos);
                                list.remove(pos);
                                caloriesAdapter.notifyDataSetChanged();
                                if (list.isEmpty()) {
                                    binding.noDataFound.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        binding.rvGoals.setAdapter(caloriesAdapter);
                        caloriesAdapter.notifyDataSetChanged();
                    } else {
                        // If no entries, show appropriate UI elements
                        binding.noDataFound.setVisibility(View.VISIBLE);
                        binding.rvGoals.setVisibility(View.GONE);
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
