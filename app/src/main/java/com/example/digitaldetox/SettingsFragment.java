package com.example.digitaldetox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class SettingsFragment extends Fragment {

    private Spinner breakIntervalSpinner;
    private CheckBox readingCheckbox;
    private CheckBox walkingCheckbox;
    private CheckBox meditationCheckbox;
    private Switch notificationsSwitch;
    private Button resetDataButton;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        breakIntervalSpinner = view.findViewById(R.id.break_interval_spinner);
        readingCheckbox = view.findViewById(R.id.reading_checkbox);
        walkingCheckbox = view.findViewById(R.id.walking_checkbox);
        meditationCheckbox = view.findViewById(R.id.meditation_checkbox);
        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        resetDataButton = view.findViewById(R.id.reset_data_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        setupBreakIntervalSpinner();
        loadSettings();

        resetDataButton.setOnClickListener(v -> resetData());
    }

    private void setupBreakIntervalSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.break_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breakIntervalSpinner.setAdapter(adapter);
        breakIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] intervals = getResources().getStringArray(R.array.break_intervals);
                sharedPreferences.edit().putString("break_interval", intervals[position]).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadSettings() {
        String breakInterval = sharedPreferences.getString("break_interval", "30 minutes");
        String[] intervals = getResources().getStringArray(R.array.break_intervals);
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i].equals(breakInterval)) {
                breakIntervalSpinner.setSelection(i);
                break;
            }
        }

        readingCheckbox.setChecked(sharedPreferences.getBoolean("reading_enabled", true));
        walkingCheckbox.setChecked(sharedPreferences.getBoolean("walking_enabled", true));
        meditationCheckbox.setChecked(sharedPreferences.getBoolean("meditation_enabled", true));
        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
    }

    private void resetData() {
        // Clear all stored data
        sharedPreferences.edit().clear().apply();

        // Reset UI to default values
        breakIntervalSpinner.setSelection(0);
        readingCheckbox.setChecked(true);
        walkingCheckbox.setChecked(true);
        meditationCheckbox.setChecked(true);
        notificationsSwitch.setChecked(true);

        // Notify the user
        Toast.makeText(getContext(), "All data has been reset", Toast.LENGTH_SHORT).show();
    }
}