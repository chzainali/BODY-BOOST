package com.example.bodyboost.main.fragments.hydration;

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
import com.example.bodyboost.adapter.HydrationAdapter;
import com.example.bodyboost.databinding.FragmentHydrationBinding;
import com.example.bodyboost.model.HydrationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HydrationFragment extends Fragment {
    private FragmentHydrationBinding binding;
    HydrationAdapter hydrationAdapter;
    List<HydrationModel> list = new ArrayList<>();
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefHydration;

    public HydrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // You can handle any arguments here if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHydrationBinding.inflate(getLayoutInflater());
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

        // Set label and visibility for the top bar
        binding.rlTop.tvLabel.setText("View Hydration's");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);

        // Handle the back button click event
        binding.rlTop.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        // Clear the list to avoid duplicate entries
        list.clear();

        // Show progress dialog
        progressDialog.show();

        // Fetch hydration data from Firebase
        dbRefHydration.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // If data exists, clear the list and populate it with new data
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            HydrationModel model = ds.getValue(HydrationModel.class);
                            list.add(model);
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (list.size() > 0) {
                        // If there is data, hide the "No Data Found" view and show the RecyclerView
                        binding.noDataFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                        binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));

                        // Initialize the adapter and set it to the RecyclerView
                        hydrationAdapter = new HydrationAdapter(list, requireContext(), (pos, status) -> {
                            HydrationModel hydrationModel = list.get(pos);

                            // Handle item click events (e.g., navigating to setHydrationFragment)
                            if (status.contentEquals("next")) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", hydrationModel);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_hydrationFragment_to_setHydrationFragment, bundle);
                            } else {
                                // Handle item deletion
                                dbRefHydration.child(auth.getCurrentUser().getUid()).child(hydrationModel.getId()).removeValue();
                                hydrationAdapter.notifyItemRemoved(pos);
                                list.remove(pos);
                                hydrationAdapter.notifyDataSetChanged();
                                if (list.isEmpty()) {
                                    binding.noDataFound.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });

                        binding.rvGoals.setAdapter(hydrationAdapter);
                        hydrationAdapter.notifyDataSetChanged();
                    } else {
                        // If there is no data, show the "No Data Found" view and hide the RecyclerView
                        binding.noDataFound.setVisibility(View.VISIBLE);
                        binding.rvGoals.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();

                } else {
                    // If there is no data, hide the "No Data Found" view and hide the RecyclerView
                    binding.noDataFound.setVisibility(View.VISIBLE);
                    binding.rvGoals.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
