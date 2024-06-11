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
import com.example.bodyboost.model.CaloriesModel;
import com.example.bodyboost.model.OnClick;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CaloriesAdapter extends RecyclerView.Adapter<CaloriesAdapter.ViewHolder> {
    // List of CaloriesModel objects
    List<CaloriesModel> list;

    // Context of the application
    Context context;

    // Interface for item click events
    OnClick onClick;

    // Constructor for the adapter
    public CaloriesAdapter(List<CaloriesModel> list, Context context, OnClick onClick) {
        this.list = list;
        this.context = context;
        this.onClick = onClick;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_calories, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the CaloriesModel at the current position
        CaloriesModel workoutModel = list.get(position);

        // Bind data to the ViewHolder
        holder.tvGoal.setText("Goal Calories: "+workoutModel.getTargetCalories());
        holder.tvCurrent.setText("Current Calories: "+workoutModel.getCurrentCalories());
        holder.tvTitle.setText("Title: "+workoutModel.getTitle());
        holder.tvStatus.setText(workoutModel.getStatus());
        holder.tvDateTime.setText("Date: " + workoutModel.getDate());

        // Set text color based on the status
        if (workoutModel.getStatus().contentEquals("Pending")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.yellow));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        }

        // Set click listeners for item and delete button
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger the onClick interface when the item is clicked
                onClick.clicked(position, "next");
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                // Trigger the onClick interface when the delete button is clicked
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
        // Views in the item layout
        TextView tvGoal, tvCurrent, tvDateTime, tvTitle, tvStatus;
        ImageView ivDelete;

        // Constructor for the ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            tvGoal = itemView.findViewById(R.id.tvGoal);
            tvCurrent = itemView.findViewById(R.id.tvCurrent);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
