package com.example.bodyboost.main.fragments;

import static androidx.navigation.fragment.FragmentKt.findNavController;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodyboost.R;
import com.example.bodyboost.adapter.DateAdapter;
import com.example.bodyboost.adapter.WorkoutAdapter;
import com.example.bodyboost.databinding.FragmentHomeBinding;
import com.example.bodyboost.main.MapsActivity;
import com.example.bodyboost.main.WorkoutDetailsActivity;
import com.example.bodyboost.model.CaloriesModel;
import com.example.bodyboost.model.HelperClass;
import com.example.bodyboost.model.HydrationModel;
import com.example.bodyboost.model.WorkoutModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding; // Binding object for the layout
    DateAdapter adapter; // Adapter for the date RecyclerView
    String date; // Current date
    Long timeInMillis = System.currentTimeMillis(); // Current time in milliseconds
    ProgressDialog progressDialog; // Progress dialog for loading data
    FirebaseAuth auth; // Firebase authentication instance
    DatabaseReference dbRefHydration; // Database reference for hydration data
    DatabaseReference dbRefCalories; // Database reference for calories data
    CaloriesModel caloriesModel; // Model class for calories data
    HydrationModel hydrationModel; // Model class for hydration data

    public HomeFragment() {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date = getCurrentDate(System.currentTimeMillis()); // Get current date
        List<Long> days = getDaysOfMonth(getCurrentYear(), getCurrentMonth()); // Get days of the current month
        binding.tvTitle.setText("Welcome "+HelperClass.users.getUserName()); // Set welcome message
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance(); // Initialize Firebase authentication
        dbRefHydration = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Hydration"); // Get reference to hydration data

        dbRefCalories = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Calories"); // Get reference to calories data

        getCaloriesHydrationData(date); // Get calories and hydration data for the current date

        binding.tvNearestGyms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), MapsActivity.class)); // Start MapsActivity
            }
        });

        adapter = new DateAdapter(days, data -> {
            timeInMillis = data;
            date = getCurrentDate(data);
            getCaloriesHydrationData(date);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvDates.setLayoutManager(layoutManager);
        binding.rvDates.setAdapter(adapter);
        for (Long date : days) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String time = sdf.format(new Date(date));
            if (time.equals(this.date)) {
                adapter.setSelected(days.indexOf(date));
                binding.rvDates.scrollToPosition(adapter.getSelected());
                break;
            }
        }

        binding.rvDates.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int centerPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    layoutManager.scrollToPositionWithOffset(centerPosition, 0);
                }
            }
        });

        // Handle click on "Workout" button
        binding.tvWorkout.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putLong("date", timeInMillis);
            findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_workoutFragment, bundle);
        });
    }

    // Update UI with hydration and calories data
    private void updateData(HydrationModel hydrationModel, CaloriesModel caloriesModel) {
        if (hydrationModel != null) {
            // Update hydration data
            int goal = Integer.parseInt(hydrationModel.getGoalHydration() != null ? hydrationModel.getGoalHydration() : "0");
            int total = Integer.parseInt(hydrationModel.getCurrentHydration() != null ? hydrationModel.getCurrentHydration() : "0");
            int left = goal - total;
            binding.tvLabelGoal.setText(hydrationModel.getGoalHydration() != null ? hydrationModel.getGoalHydration() : "0");
            binding.tvLabelTotal.setText(hydrationModel.getCurrentHydration() != null ? hydrationModel.getCurrentHydration() : "0");
            binding.tvLabelLeft.setText(Math.max(left, 0) + "");
            binding.hydartionProgress.setMax(goal);
            binding.hydartionProgress.setProgress(total);
        }
        if (caloriesModel != null) {
            // Update calories data
            int goal = Integer.parseInt(caloriesModel.getTargetCalories() != null ? caloriesModel.getTargetCalories() : "0");
            int total = Integer.parseInt(caloriesModel.getCurrentCalories() != null ? caloriesModel.getCurrentCalories() : "0");
            int left = goal - total;
            binding.tvLabelGoal1.setText(caloriesModel.getTargetCalories() != null ? caloriesModel.getTargetCalories() : "0");
            binding.tvLabelTotal1.setText(caloriesModel.getCurrentCalories() != null ? caloriesModel.getCurrentCalories() : "0");
            binding.tvLabelLeft1.setText(Math.max(left, 0) + "");
            binding.calorieProgress.setMax(Integer.parseInt(caloriesModel.getTargetCalories() != null ? caloriesModel.getTargetCalories() : "0"));
            binding.calorieProgress.setProgress(Integer.parseInt(caloriesModel.getCurrentCalories() != null ? caloriesModel.getCurrentCalories() : "0"));
        }
    }

    // Get days of the current month
    private List<Long> getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);

        List<Long> daysOfMonth = new ArrayList<>();

        while (calendar.get(Calendar.MONTH) == month - 1) {
            daysOfMonth.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return daysOfMonth;
    }

    // Get the current year
    private int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    // Get the current month
    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        // Calendar months are 0-based, so add 1 to get the actual month
        return calendar.get(Calendar.MONTH) + 1;
    }

    // Get the formatted current date
    private String getCurrentDate(Long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(time));
    }

    // Get calories and hydration data for the specified date
    private void getCaloriesHydrationData(String date){
        caloriesModel = new CaloriesModel();
        hydrationModel = new HydrationModel();
        progressDialog.show();
        dbRefCalories.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        CaloriesModel model = ds.getValue(CaloriesModel.class);
                        if (date.equals(model.getDate())) {
                            caloriesModel = new CaloriesModel(model.getId(), model.getUserId(), model.getTitle(),
                                    model.getDate(), model.getTargetCalories(), model.getCurrentCalories(), model.getStatus());
                        }
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }
                }
                dbRefHydration.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                HydrationModel model = ds.getValue(HydrationModel.class);
                                if (date.equals(model.getDate())) {
                                    hydrationModel = new HydrationModel(model.getId(), model.getUserId(), model.getTitle(),
                                            model.getDate(), model.getGoalHydration(), model.getCurrentHydration(), model.getStatus());
                                }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        updateData(hydrationModel, caloriesModel);
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
