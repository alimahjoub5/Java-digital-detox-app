package com.example.digitaldetox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainFragment extends Fragment {



    private TextView totalScreenTimeView;
    private TextView activeTimeView;
    private TextView idleTimeView;
    private TextView nextBreakView;
    private ProgressBar breakProgressBar;
    private Button pauseButton;
    private Button resetButton;

    private BroadcastReceiver screenTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateScreenTimeInfo(intent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        totalScreenTimeView = view.findViewById(R.id.total_screen_time);
        activeTimeView = view.findViewById(R.id.active_time);
        idleTimeView = view.findViewById(R.id.idle_time);
        nextBreakView = view.findViewById(R.id.next_break);
        breakProgressBar = view.findViewById(R.id.break_progress);
        pauseButton = view.findViewById(R.id.pause_button);
        resetButton = view.findViewById(R.id.reset_button);

        pauseButton.setOnClickListener(v -> pauseOrResumeTracking());
        resetButton.setOnClickListener(v -> resetSession());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(screenTimeReceiver,
                new IntentFilter(ScreenTimeService.ACTION_SCREEN_TIME_UPDATE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(screenTimeReceiver);
    }

    private void updateScreenTimeInfo(Intent intent) {
        long totalScreenTime = intent.getLongExtra(ScreenTimeService.EXTRA_TOTAL_SCREEN_TIME, 0);
        long activeTime = intent.getLongExtra(ScreenTimeService.EXTRA_ACTIVE_TIME, 0);
        long idleTime = intent.getLongExtra(ScreenTimeService.EXTRA_IDLE_TIME, 0);
        long nextBreakTime = intent.getLongExtra(ScreenTimeService.EXTRA_NEXT_BREAK_TIME, 0);
        int breakProgress = intent.getIntExtra(ScreenTimeService.EXTRA_BREAK_PROGRESS, 0);

        totalScreenTimeView.setText(String.format("Total Screen Time: %s", formatTime(totalScreenTime)));
        activeTimeView.setText(String.format("Active Time: %s", formatTime(activeTime)));
        idleTimeView.setText(String.format("Idle Time: %s", formatTime(idleTime)));
        nextBreakView.setText(String.format("Next Break in: %s", formatTime(nextBreakTime)));
        breakProgressBar.setProgress(breakProgress);
    }

    private String formatTime(long timeInMillis) {
        long hours = timeInMillis / (60 * 60 * 1000);
        long minutes = (timeInMillis % (60 * 60 * 1000)) / (60 * 1000);
        return String.format("%dh %dm", hours, minutes);
    }

    private void pauseOrResumeTracking() {
        Intent intent = new Intent(getContext(), ScreenTimeService.class);
        intent.setAction(ScreenTimeService.ACTION_TOGGLE_PAUSE);

        // Utilisation de startForegroundService pour les versions Android >= 8.0 (API 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(getContext(), intent);
        } else {
            getContext().startService(intent);
        }
    }



    private void resetSession() {
        Intent intent = new Intent(getContext(), ScreenTimeService.class);
        intent.setAction(ScreenTimeService.ACTION_RESET_SESSION);
        getContext().startService(intent);
    }
}

