package com.example.digitaldetox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.example.digitaldetox.Activity;
import com.example.digitaldetox.ActivitiesAdapter;
import com.example.digitaldetox.ActivityDatabase;import androidx.fragment.app.Fragment;

public class ActivitiesFragment extends Fragment {

    private TextView activitiesCompletedView;
    private ProgressBar activitiesProgressBar;
    private RecyclerView activitiesList;
    private ActivitiesAdapter activitiesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activitiesCompletedView = view.findViewById(R.id.activities_completed);
        activitiesProgressBar = view.findViewById(R.id.activities_progress);
        activitiesList = view.findViewById(R.id.activities_list);
        setupRecyclerView();
        loadActivities();
    }

    private void setupRecyclerView() {
        activitiesList.setLayoutManager(new LinearLayoutManager(getContext()));
        activitiesAdapter = new ActivitiesAdapter(new ArrayList<>());
        activitiesAdapter.setOnActivityClickListener(this::onActivityClick);
        activitiesList.setAdapter(activitiesAdapter);
    }

    private void loadActivities() {
        List<Activity> activities = ActivityDatabase.getInstance(getContext()).getActivities();
        activitiesAdapter.setActivities(activities);

        int completedActivities = (int) activities.stream().filter(Activity::isCompleted).count();
        activitiesCompletedView.setText(String.format("Activities Completed: %d", completedActivities));
        activitiesProgressBar.setMax(activities.size());
        activitiesProgressBar.setProgress(completedActivities);
    }

    private void onActivityClick(Activity activity) {
        activity.setCompleted(!activity.isCompleted());
        ActivityDatabase.getInstance(getContext()).updateActivity(activity);
        loadActivities();
    }
}

