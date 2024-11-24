package com.example.digitaldetox;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.PowerManager;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ScreenTimeService extends Service {

    public static final String ACTION_SCREEN_TIME_UPDATE = "com.example.digitaldetox.SCREEN_TIME_UPDATE";
    public static final String ACTION_TOGGLE_PAUSE = "com.example.digitaldetox.TOGGLE_PAUSE";
    public static final String ACTION_RESET_SESSION = "com.example.digitaldetox.RESET_SESSION";

    public static final String EXTRA_TOTAL_SCREEN_TIME = "total_screen_time";
    public static final String EXTRA_ACTIVE_TIME = "active_time";
    public static final String EXTRA_IDLE_TIME = "idle_time";
    public static final String EXTRA_NEXT_BREAK_TIME = "next_break_time";
    public static final String EXTRA_BREAK_PROGRESS = "break_progress";

    private PowerManager powerManager;
    private Handler handler = new Handler();
    private long startTime;
    private long totalScreenTime;
    private long activeTime;
    private long idleTime;
    private long lastActiveTime;
    private boolean isPaused = false;

    private static final long UPDATE_INTERVAL = 1000; // 1 second
    private static final long IDLE_THRESHOLD = 60000; // 1 minute

    @Override
    public void onCreate() {
        super.onCreate();
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        startTime = System.currentTimeMillis();
        lastActiveTime = startTime;
        startTracking();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_TOGGLE_PAUSE.equals(action)) {
                togglePause();
            } else if (ACTION_RESET_SESSION.equals(action)) {
                resetSession();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTracking() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateScreenTime();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private void updateScreenTime() {
        if (!isPaused) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;

            if (powerManager.isInteractive()) {
                activeTime += currentTime - lastActiveTime;
                lastActiveTime = currentTime;
            } else {
                idleTime += currentTime - lastActiveTime;
                if (idleTime > IDLE_THRESHOLD) {
                    lastActiveTime = currentTime;
                }
            }

            totalScreenTime = activeTime + idleTime;

            sendScreenTimeUpdate();
        }
    }

    private void sendScreenTimeUpdate() {
        Intent intent = new Intent(ACTION_SCREEN_TIME_UPDATE);
        intent.putExtra(EXTRA_TOTAL_SCREEN_TIME, totalScreenTime);
        intent.putExtra(EXTRA_ACTIVE_TIME, activeTime);
        intent.putExtra(EXTRA_IDLE_TIME, idleTime);
        // Calculate next break time and progress here
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (!isPaused) {
            startTime = System.currentTimeMillis() - totalScreenTime;
            lastActiveTime = System.currentTimeMillis();
        }
    }

    private void resetSession() {
        startTime = System.currentTimeMillis();
        totalScreenTime = 0;
        activeTime = 0;
        idleTime = 0;
        lastActiveTime = startTime;
        isPaused = false;
        sendScreenTimeUpdate();
    }
}