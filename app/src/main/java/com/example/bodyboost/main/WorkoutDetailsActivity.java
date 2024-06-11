package com.example.bodyboost.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bodyboost.R;
import com.example.bodyboost.databinding.ActivityWorkoutDetailsBinding;
import com.example.bodyboost.model.WorkoutModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WorkoutDetailsActivity extends AppCompatActivity {
    // Binding for the layout
    ActivityWorkoutDetailsBinding binding;

    // Model representing workout details
    WorkoutModel workoutModel;

    // Image URIs for the first and second images
    String imageUri1 = "", imageUri2 = "";

    // Request codes for image selection
    private static final int REQUEST_CODE_IMAGE_1 = 1;
    private static final int REQUEST_CODE_IMAGE_2 = 2;

    // Progress dialog for loading and updating data
    ProgressDialog progressDialog;

    // Firebase authentication instance
    FirebaseAuth auth;

    // Reference to the database node for workout details
    DatabaseReference dbRefWorkout;

    // Reference to Firebase Storage for storing images
    StorageReference storageReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using View Binding
        binding = ActivityWorkoutDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        // Retrieve the workout model from the intent
        if (getIntent().getExtras() != null) {
            workoutModel = (WorkoutModel) getIntent().getSerializableExtra("data");
        }

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        dbRefWorkout = FirebaseDatabase.getInstance(
                "https://body-boost-e4cfa-default-rtdb.firebaseio.com").getReference("Workout");
        storageReference = FirebaseStorage.getInstance().getReference("WorkoutPictures");

        // Set up UI components
        binding.rlTop.tvLabel.setText("Workouts Details");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Populate UI with workout details
        if (workoutModel != null) {
            // Set workout details
            binding.etWorkoutType.setText(workoutModel.getType());
            binding.etWorkoutDate.setText(workoutModel.getDate());
            binding.etStartTime.setText(workoutModel.getStartTime());
            binding.etEndTime.setText(workoutModel.getEndTime());
            binding.etEndTime.setText(workoutModel.getEndTime());
            binding.etStatus.setText(workoutModel.getStatus());

            // Hide the "Complete" button if the workout is already completed
            if (workoutModel.getStatus().contentEquals("Completed")) {
                binding.btnComplete.setVisibility(View.GONE);
            }

            // Calculate and display the duration of the workout
            String startTime = workoutModel.getStartTime();
            String endTime = workoutModel.getEndTime();
            String timeDifference = calculateTimeDifference(startTime, endTime);
            binding.etDuration.setText(timeDifference);

            // Load and display images using Glide library
            imageUri1 = workoutModel.getFirstPic();
            imageUri2 = workoutModel.getSecondPic();
            if (!workoutModel.getFirstPic().contentEquals("")) {
                Glide.with(this)
                        .asBitmap()
                        .load(Uri.parse(workoutModel.getFirstPic()))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Set the Bitmap as the image resource
                                binding.ivImage1.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Called when the Drawable is cleared, for example, when the view is recycled.
                            }
                        });
            }
            if (!workoutModel.getSecondPic().contentEquals("")) {
                Glide.with(this)
                        .asBitmap()
                        .load(Uri.parse(workoutModel.getSecondPic()))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Set the Bitmap as the image resource
                                binding.ivImage2.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Called when the Drawable is cleared, for example, when the view is recycled.
                            }
                        });
            }

            // Set up click listeners for image selection
            binding.ivImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start image picker for the first image
                    ImagePicker.with(WorkoutDetailsActivity.this)
                            .compress(512)
                            .maxResultSize(512, 512)
                            .start(REQUEST_CODE_IMAGE_1);
                }
            });
            binding.ivImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start image picker for the second image
                    ImagePicker.with(WorkoutDetailsActivity.this)
                            .compress(512)
                            .maxResultSize(512, 512)
                            .start(REQUEST_CODE_IMAGE_2);
                }
            });

            // Set up click listener for updating images
            binding.btnSetImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!imageUri1.isEmpty() || !imageUri2.isEmpty()) {
                        // Update images and save to the database
                        workoutModel.setFirstPic(imageUri1);
                        workoutModel.setSecondPic(imageUri2);
                        updateImages(imageUri1, imageUri2);
                    } else {
                        Toast.makeText(WorkoutDetailsActivity.this, "Please pick image first", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Set up click listener for completing the workout
            binding.btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Mark the workout as completed and update the database
                    workoutModel.setStatus("Completed");
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put("status", "Completed");
                    dbRefWorkout.child(auth.getCurrentUser().getUid()).child(workoutModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        showMessage("Successfully Updated");
                        binding.btnComplete.setVisibility(View.GONE);
                        binding.etStatus.setText("Completed");
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        showMessage(e.getMessage());
                    });
                }
            });
        }
    }

    // Method to update images in Firebase Storage and database
    private void updateImages(String imageUri1, String imageUri2) {
        progressDialog.show();

        // Check if both image URIs are not empty
        if (!imageUri1.isEmpty() && !imageUri2.isEmpty()) {
            Uri uriImage1 = Uri.parse(imageUri1);
            Uri uriImage2 = Uri.parse(imageUri2);

            // Upload the first image
            StorageReference imageRef = storageReference.child(uriImage1.getLastPathSegment());
            imageRef.putFile(uriImage1).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUri1 = uri.toString();

                // Upload the second image
                StorageReference imageRef2 = storageReference.child(uriImage2.getLastPathSegment());
                imageRef2.putFile(uriImage2).addOnSuccessListener(taskSnapshot2 -> imageRef2.getDownloadUrl().addOnSuccessListener(uri2 -> {
                    String downloadUri2 = uri2.toString();

                    // Update the database with the new image URIs
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put("firstPic", downloadUri1);
                    update.put("secondPic", downloadUri2);
                    dbRefWorkout.child(auth.getCurrentUser().getUid()).child(workoutModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        showMessage("Successfully Saved");
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        showMessage(e.getMessage());
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getLocalizedMessage());
                })).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getLocalizedMessage());
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            });
        } else {
            // If only one image URI is not empty, upload that image
            String singleImageUri = "";
            String checkImage = "";
            if (!imageUri1.isEmpty()) {
                singleImageUri = imageUri1;
                checkImage = "1";
            }
            if (!imageUri2.isEmpty()) {
                singleImageUri = imageUri2;
                checkImage = "2";
            }

            Uri uriImage = Uri.parse(singleImageUri);
            StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());
            String finalCheckImage = checkImage;
            imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUri = uri.toString();

                // Update the database with the new image URI
                Map<String, Object> update = new HashMap<String, Object>();
                if (finalCheckImage.contains("1")) {
                    update.put("firstPic", downloadUri);
                } else {
                    update.put("secondPic", downloadUri);
                }
                dbRefWorkout.child(auth.getCurrentUser().getUid()).child(workoutModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    showMessage("Successfully Saved");
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getMessage());
                });

            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            });
        }
    }

    // Handle the result of image selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_OK) {
            try {
                if (requestCode == REQUEST_CODE_IMAGE_1) {
                    // Handle result for the first image
                    imageUri1 = String.valueOf(data.getData());
                    Glide.with(this)
                            .asBitmap()
                            .load(Uri.parse(imageUri1))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    // Set the Bitmap as the image resource
                                    binding.ivImage1.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Called when the Drawable is cleared, for example, when the view is recycled.
                                }
                            });
                } else if (requestCode == REQUEST_CODE_IMAGE_2) {
                    // Handle result for the second image
                    imageUri2 = String.valueOf(data.getData());
                    Glide.with(this)
                            .asBitmap()
                            .load(Uri.parse(imageUri2))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    // Set the Bitmap as the image resource
                                    binding.ivImage2.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Called when the Drawable is cleared, for example, when the view is recycled.
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Calculate the time difference between start and end times
    private String calculateTimeDifference(String startTime, String endTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date startDate = dateFormat.parse(startTime);
            Date endDate = dateFormat.parse(endTime);

            long timeDifferenceMillis = endDate.getTime() - startDate.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60;

            return String.format(Locale.getDefault(), "%d hours %d minutes", hours, minutes);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Display a Toast message
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
