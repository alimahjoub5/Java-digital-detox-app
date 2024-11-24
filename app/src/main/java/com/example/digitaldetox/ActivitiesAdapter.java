package com.example.digitaldetox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder> {

    private List<Activity> activities;
    private OnActivityClickListener listener;

    public ActivitiesAdapter(List<Activity> activities) {
        this.activities = activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }

    public void setOnActivityClickListener(OnActivityClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.bind(activity);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    class ActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView descriptionTextView;
        private CheckBox completedCheckBox;

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.activity_name);
            descriptionTextView = itemView.findViewById(R.id.activity_description);
            completedCheckBox = itemView.findViewById(R.id.activity_completed);

            completedCheckBox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onActivityClick(activities.get(position));
                }
            });
        }

        void bind(Activity activity) {
            nameTextView.setText(activity.getName());
            descriptionTextView.setText(activity.getDescription());
            completedCheckBox.setChecked(activity.isCompleted());
        }
    }

    public interface OnActivityClickListener {
        void onActivityClick(Activity activity);
    }
}

