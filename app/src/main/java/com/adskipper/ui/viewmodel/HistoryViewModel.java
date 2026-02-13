package com.adskipper.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.adskipper.data.SkipDatabase;
import com.adskipper.data.SkipRecord;

import java.util.Calendar;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final SkipDatabase database;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        database = SkipDatabase.getInstance(application);
    }

    public LiveData<List<SkipRecord>> getAllRecords() {
        return database.skipRecordDao().getAllRecords();
    }

    public LiveData<Integer> getTotalTimeSaved() {
        return database.skipRecordDao().getTotalTimeSaved();
    }

    public LiveData<Integer> getTodaySkipCount() {
        long todayStart = getTodayStartTimestamp();
        return database.skipRecordDao().getSkipCountSince(todayStart);
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
