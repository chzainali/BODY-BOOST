package com.example.bodyboost.main.fragments.workout;

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
import com.example.bodyboost.adapter.WorkoutAdapter;
import com.example.bodyboost.databinding.FragmentWorkoutBinding;
import com.example.bodyboost.main.WorkoutDetailsActivity;
import com.example.bodyboost.model.WorkoutModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WorkoutFragment extends Fragment {
    FragmentWorkoutBinding binding;
    WorkoutAdapter workoutAdapter;
    ArrayList<WorkoutModel> list = new ArrayList<>();
    Long date;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefWorkout;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve date argument if available
        if (getArguments() != null) {
            date = getArguments().getLong("date", System.currentTimeMillis());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutBinding.inflate(getLayoutInflater());
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
        dbRefWorkout = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Workout");

        // Set label and visibility for the top bar
        binding.rlTop.tvLabel.setText("View Workouts");
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
        list.clear();

        progressDialog.show();
        // Retrieve workout data from Firebase
        dbRefWorkout.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    // Filter workouts based on the specified date
                    if (date != null) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                WorkoutModel model = ds.getValue(WorkoutModel.class);
                                // Check if the workout date matches the specified date
                                if (getCurrentDate(date).equals(model.getDate())) {
                                    list.add(model);
                                }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // If no date is specified, retrieve all workouts
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                WorkoutModel model = ds.getValue(WorkoutModel.class);
                                list.add(model);
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (list.size() > 0) {
                        // If there are workouts, display them in the RecyclerView
                        binding.noDataFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                        binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));
                        workoutAdapter = new WorkoutAdapter(list, requireContext(), (pos, status) -> {
                            WorkoutModel workoutModel = list.get(pos);
                            if (status.contentEquals("next")) {
                                // Open WorkoutDetailsActivity for detailed view
                                Intent intent = new Intent(requireContext(), WorkoutDetailsActivity.class);
                                intent.putExtra("data", workoutModel);
                                startActivity(intent);
                            } else {
                                // Remove workout if the user chooses to delete it
                                dbRefWorkout.child(auth.getCurrentUser().getUid()).child(workoutModel.getId()).removeValue();
                                workoutAdapter.notifyItemRemoved(pos);
                                list.remove(pos);
                                workoutAdapter.notifyDataSetChanged();
                                if (list.isEmpty()) {
                                    binding.noDataFound.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        binding.rvGoals.setAdapter(workoutAdapter);
                        workoutAdapter.notifyDataSetChanged();
                    } else {
                        // If there are no workouts, show a message
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

    // Convert date to a different format
    private static String convertDateFormat(String inputDateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");

        try {
            Date date = inputFormat.parse(inputDateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error in date conversion";
        }
    }

    // Get the current date
    private String getCurrentDate(Long time) {
        if (time != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(new Date(time.longValue()));
        } else {
            return "N/A";
        }
    }
}
