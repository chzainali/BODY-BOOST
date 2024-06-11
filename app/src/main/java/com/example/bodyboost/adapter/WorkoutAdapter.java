package com.example.bodyboost.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodyboost.R;
import com.example.bodyboost.model.OnClick;
import com.example.bodyboost.model.WorkoutModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    // List to hold WorkoutModel data
    List<WorkoutModel> list;

    // Context to be used for inflating the layout
    Context context;

    // Interface for handling item clicks
    OnClick onClick;

    // Constructor for the adapter
    public WorkoutAdapter(List<WorkoutModel> list, Context context, OnClick onClick) {
        this.list = list;
        this.context = context;
        this.onClick = onClick;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get WorkoutModel at the current position
        WorkoutModel workoutModel = list.get(position);

        // Calculate the time difference between start and end time
        String startTime = workoutModel.getStartTime();
        String endTime = workoutModel.getEndTime();
        String timeDifference = calculateTimeDifference(startTime, endTime);

        // Set data to the ViewHolder
        holder.tvExerciseHours.setText("Duration: " + timeDifference);
        holder.tvTitle.setText(workoutModel.getType());
        holder.tvStatus.setText(workoutModel.getStatus());
        holder.tvDateTime.setText("Date: " + workoutModel.getDate() + ",\nStart time: " + workoutModel.getStartTime() + ", End Time: " + workoutModel.getEndTime());

        // Set text color based on status
        if (workoutModel.getStatus().contentEquals("Pending")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.yellow));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        }

        // Set click listener for item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.clicked(position, "next");
            }
        });

        // Set click listener for delete icon
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                onClick.clicked(position, "delete");
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder class for holding the view references
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseHours, tvDateTime, tvTitle, tvStatus;
        ImageView ivDelete;

        // Constructor for the ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize TextView and ImageView
            tvExerciseHours = itemView.findViewById(R.id.tvExerciseHours);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    // Method to calculate time difference between start and end time
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
}
