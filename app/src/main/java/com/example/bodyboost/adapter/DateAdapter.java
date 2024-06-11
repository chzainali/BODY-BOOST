package com.example.bodyboost.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodyboost.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    // List of date values in milliseconds
    private List<Long> dates;

    // Index of the selected date
    int selected = 0;

    // Interface for handling date selection
    OnSelected onSelected;

    // Constructor for the adapter
    public DateAdapter(List<Long> dates, OnSelected onSelected) {
        this.dates = dates;
        this.onSelected = onSelected;
    }

    // Set the selected date index
    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    // Get the selected date index
    public int getSelected() {
        return selected;
    }

    // Interface for handling date selection events
    public interface OnSelected {
        void onSelected(long data);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the date value in milliseconds at the current position
        long dateInMillis = dates.get(position);

        // Bind data to the ViewHolder
        holder.bind(dateInMillis);

        // Set background tint based on whether the date is selected or not
        if (selected == position) {
            holder.dateTextView.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getColor(R.color.blue)));
        } else {
            holder.dateTextView.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getColor(R.color.field_color)));
        }

        // Set click listener for date item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the selected date is clicked again
                if (selected == position) {
                    return;
                } else {
                    // Save the previously selected index
                    int previousSelected = selected;
                    // Update the selected index
                    selected = position;
                    // Notify item change for the current and previous selected indices
                    notifyItemChanged(position);
                    onSelected.onSelected(dateInMillis);
                    notifyItemChanged(previousSelected);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dates.size();
    }

    // ViewHolder class for holding the view references
    static class DateViewHolder extends RecyclerView.ViewHolder {
        // TextView to display the date
        private final TextView dateTextView;

        // Constructor for the ViewHolder
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the TextView
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        // Bind date data to the TextView
        public void bind(long dateInMillis) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
            Date date = new Date(dateInMillis);
            dateTextView.setText(dateFormat.format(date));
        }
    }
}
