package com.adskipper.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.adskipper.data.SkipDatabase;

import java.util.Calendar;

public class DashboardViewModel extends AndroidViewModel {

    private final SkipDatabase database;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        database = SkipDatabase.getInstance(application);
    }

    public LiveData<Integer> getTotalSkipCount() {
        return database.skipRecordDao().getTotalSkipCount();
    }

    public LiveData<Integer> getTotalTimeSaved() {
        return database.skipRecordDao().getTotalTimeSaved();
    }

    public LiveData<Integer> getTodaySkipCount() {
        long todayStart = getTodayStartTimestamp();
        return database.skipRecordDao().getSkipCountSince(todayStart);
    }

    public LiveData<Integer> getTodayTimeSaved() {
        long todayStart = getTodayStartTimestamp();
        return database.skipRecordDao().getTimeSavedSince(todayStart);
    }

    public LiveData<Integer> getMonitoredAppsCount() {
        return database.appPreferenceDao().getMonitoredAppsCount();
    }

    private long getTodayStartTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
