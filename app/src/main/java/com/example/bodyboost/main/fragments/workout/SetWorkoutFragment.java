package com.example.bodyboost.main.fragments.workout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bodyboost.R;
import com.example.bodyboost.databinding.FragmentSetWorkoutBinding;
import com.example.bodyboost.model.WorkoutModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SetWorkoutFragment extends Fragment {
    FragmentSetWorkoutBinding binding;
    String workoutType = "", date, startTime, endTime, isReminderEnable = "false";
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private static final int NOTIFICATION_ID = 1;
    WorkoutModel newWorkoutModel;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefWorkout;

    public SetWorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle any arguments here if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSetWorkoutBinding.inflate(getLayoutInflater());
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

        // Initialize date and time formats
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Set label and visibility for the top bar
        binding.rlTop.tvLabel.setText("Workout Tracking");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);

        // Handle the back button click event
        binding.rlTop.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        // Call methods to select workout type and date/time
        selectWorkoutType();
        selectDateTime();
    }

    // Method to select date and time
    private void selectDateTime() {
        binding.etWorkoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(binding.etStartTime);
            }
        });

        binding.etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(binding.etEndTime);
            }
        });

        binding.btnSetTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input and set workout details
                if (isValidated()) {
                    progressDialog.show();
                    String workoutId = dbRefWorkout.push().getKey();
                    newWorkoutModel = new WorkoutModel(workoutId, auth.getCurrentUser().getUid(), workoutType, date, startTime, endTime, isReminderEnable, "", "", "Pending");
                    dbRefWorkout.child(auth.getCurrentUser().getUid()).child(workoutId).setValue(newWorkoutModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            showMessage("Set Successfully");
                            Navigation.findNavController(v).navigateUp();
                        }
                    }).addOnFailureListener((OnFailureListener) e -> {
                        progressDialog.dismiss();
                        showMessage(e.getMessage());
                    });
                }
            }
        });
    }

    // Method to validate user input
    private Boolean isValidated() {
        date = binding.etWorkoutDate.getText().toString().trim();
        startTime = binding.etStartTime.getText().toString().trim();
        endTime = binding.etEndTime.getText().toString().trim();

        if (workoutType.isEmpty()) {
            showMessage("Please select workout type");
            return false;
        }
        if (date.isEmpty()) {
            showMessage("Please select date");
            return false;
        }
        if (startTime.isEmpty()) {
            showMessage("Please select start time");
            return false;
        }
        if (endTime.isEmpty()) {
            showMessage("Please select end time");
            return false;
        }

        // Check if end time is greater than start time
        Calendar startCalendar = parseDateTime(date, startTime);
        Calendar endCalendar = parseDateTime(date, endTime);
        if (endCalendar != null && endCalendar.before(startCalendar)) {
            showMessage("End time should be greater than start time");
            return false;
        }

        return true;
    }

    // Method to display a toast message
    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Method to handle workout type selection
    private void selectWorkoutType() {
        // Add onClickListeners for each workout type TextView
        // Yoga
        binding.tvYoga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvYoga.getText().toString();
                selectTextView(binding.tvYoga);
            }
        });

        // Swimming
        binding.tvSwimming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvSwimming.getText().toString();
                selectTextView(binding.tvSwimming);
            }
        });

        // Cycling
        binding.tvCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvCycling.getText().toString();
                selectTextView(binding.tvCycling);
            }
        });

        // Running
        binding.tvRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvRunning.getText().toString();
                selectTextView(binding.tvRunning);
            }
        });

        // Aerobic
        binding.tvAerobic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvAerobic.getText().toString();
                selectTextView(binding.tvAerobic);
            }
        });

        // Pilates
        binding.tvPilates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvPilates.getText().toString();
                selectTextView(binding.tvPilates);
            }
        });

        // HIT
        binding.tvHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvHit.getText().toString();
                selectTextView(binding.tvHit);
            }
        });

        // Bootcamp
        binding.tvBootcamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvBootcamp.getText().toString();
                selectTextView(binding.tvBootcamp);
            }
        });

        // Circuit
        binding.tvCircuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvCircuit.getText().toString();
                selectTextView(binding.tvCircuit);
            }
        });

        // Dance
        binding.tvDance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutType = binding.tvDance.getText().toString();
                selectTextView(binding.tvDance);
            }
        });
    }

    // Method to update background color for selected TextView
    private void selectTextView(TextView textView) {
        textView.setBackgroundColor(getContext().getColor(R.color.blue));
        // Reset other TextViews to default color
        resetTextViewColors(textView);
    }

    // Method to reset background color for all TextViews
    private void resetTextViewColors(TextView selectedTextView) {
        TextView[] allTextViews = {
                binding.tvYoga,
                binding.tvSwimming,
                binding.tvCycling,
                binding.tvRunning,
                binding.tvAerobic,
                binding.tvPilates,
                binding.tvHit,
                binding.tvBootcamp,
                binding.tvCircuit,
                binding.tvDance
        };

        for (TextView textView : allTextViews) {
            if (textView != selectedTextView) {
                textView.setBackgroundColor(getContext().getColor(R.color.field_color));
            }
        }
    }

    // Method to show date picker dialog
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        binding.etWorkoutDate.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Method to show time picker dialog
    private void showTimePickerDialog(EditText etTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (timePicker, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    etTime.setText(timeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    // Method to parse date and time
    private Calendar parseDateTime(String date, String time) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            calendar.setTime(dateFormat.parse(date + " " + time));
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
