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
import com.example.bodyboost.model.HydrationModel;
import com.example.bodyboost.model.OnClick;

import java.util.List;

public class HydrationAdapter extends RecyclerView.Adapter<HydrationAdapter.ViewHolder> {

    // List to hold HydrationModel data
    List<HydrationModel> list;

    // Context to be used for inflating the layout
    Context context;

    // Interface for handling item clicks
    OnClick onClick;

    // Constructor for the adapter
    public HydrationAdapter(List<HydrationModel> list, Context context, OnClick onClick) {
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
        // Get HydrationModel at the current position
        HydrationModel hydrationModel = list.get(position);

        // Set data to the ViewHolder
        holder.tvGoal.setText("Goal Hydration: " + hydrationModel.getGoalHydration());
        holder.tvCurrent.setText("Current Hydration: " + hydrationModel.getCurrentHydration());
        holder.tvTitle.setText("Title: " + hydrationModel.getTitle());
        holder.tvStatus.setText(hydrationModel.getStatus());
        holder.tvDateTime.setText("Date: " + hydrationModel.getDate());

        // Set text color based on status
        if (hydrationModel.getStatus().contentEquals("Pending")) {
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
        TextView tvGoal, tvCurrent, tvDateTime, tvTitle, tvStatus;
        ImageView ivDelete;

        // Constructor for the ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize TextView and ImageView
            tvGoal = itemView.findViewById(R.id.tvGoal);
            tvCurrent = itemView.findViewById(R.id.tvCurrent);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
