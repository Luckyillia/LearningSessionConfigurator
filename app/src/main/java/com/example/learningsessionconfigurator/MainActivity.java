package com.example.learningsessionconfigurator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "StudySessionPrefs";
    private static final String KEY_REMINDERS = "RemindersEnabled";
    private static final String KEY_SESSION_MINUTES = "SessionMinutes";
    private static final String KEY_SESSION_HISTORY = "SessionHistory";
    private static String[] historyArr;
    private static final int DEFAULT_SESSION_MINUTES = 45;

    private Switch switchReminders;
    private SeekBar seekBarSession;
    private TextView tvSessionValue;
    private TextView tvSummary;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchReminders = findViewById(R.id.switchReminders);
        seekBarSession = findViewById(R.id.seekBarSession);
        tvSessionValue = findViewById(R.id.tvSessionValue);
        tvSummary = findViewById(R.id.tvSummary);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        loadSettings();
        setupListeners();
    }

    private void setupListeners() {
        switchReminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveBoolean(KEY_REMINDERS, isChecked);
                updateSummary();
            }
        });

        seekBarSession.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = progressToMinutes(progress);
                updateSessionLabel(minutes);
                updateSummary();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveInt(KEY_SESSION_MINUTES, progressToMinutes(seekBar.getProgress()));

                //saveHistory(KEY_SESSION_HISTORY, );
            }
        });
    }

    private void loadSettings() {
        boolean remindersEnabled = sharedPreferences.getBoolean(KEY_REMINDERS, true);
        int sessionMinutes = sharedPreferences.getInt(KEY_SESSION_MINUTES, DEFAULT_SESSION_MINUTES);
        String history = sharedPreferences.getString(KEY_SESSION_HISTORY, null);
        if(history != null){
            historyArr = history.split(",");
        }
        switchReminders.setChecked(remindersEnabled);
        seekBarSession.setProgress(minutesToProgress(sessionMinutes));

        updateSessionLabel(sessionMinutes);
        updateSummary();
    }

    // Zakres: 15..90 minut, krok 5 minut.
    private int progressToMinutes(int progress) {
        return 15 + (progress * 5);
    }

    private int minutesToProgress(int minutes) {
        int clamped = Math.max(15, Math.min(90, minutes));
        return (clamped - 15) / 5;
    }

    private int calculateFocusPoints(int minutes, boolean remindersEnabled) {
        int base = minutes / 5;
        return remindersEnabled ? base + 2 : base;
    }

//    private String historyToString(String[] historyTab){
//        String historyStr = "";
//        if(historyArr.length > 5){
//
//        } eles {
//            for (int i = 0; i < historyArr.length; i++) {
//
//            }
//        }
//
//    }

    private void updateSessionLabel(int minutes) {
        tvSessionValue.setText(String.format(Locale.getDefault(), "%d min", minutes));
    }

    private void updateSummary() {
        boolean reminders = switchReminders.isChecked();
        int minutes = progressToMinutes(seekBarSession.getProgress());
        int points = calculateFocusPoints(minutes, reminders);

        String reminderText = reminders ? "włączone" : "wyłączone";
        String summary = "Plan sesji:\n"
                + "• Czas: " + minutes + " min\n"
                + "• Przypomnienia: " + reminderText + "\n"
                + "• Punkty skupienia: " + points;

        tvSummary.setText(summary);
    }

    private void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    private void saveInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    private void saveHistory(String key, String value) {

        sharedPreferences.edit().putString(key, value).apply();
    }
}