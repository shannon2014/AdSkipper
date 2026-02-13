package com.adskipper.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SkipRecordDao {
    
    @Insert
    void insert(SkipRecord record);
    
    @Query("SELECT * FROM skip_records ORDER BY timestamp DESC")
    LiveData<List<SkipRecord>> getAllRecords();
    
    @Query("SELECT * FROM skip_records WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    LiveData<List<SkipRecord>> getRecordsSince(long startTime);
    
    @Query("SELECT * FROM skip_records WHERE packageName = :packageName ORDER BY timestamp DESC")
    LiveData<List<SkipRecord>> getRecordsByPackage(String packageName);
    
    @Query("SELECT COUNT(*) FROM skip_records")
    LiveData<Integer> getTotalSkipCount();
    
    @Query("SELECT SUM(timeSavedSeconds) FROM skip_records")
    LiveData<Integer> getTotalTimeSaved();
    
    @Query("SELECT COUNT(*) FROM skip_records WHERE timestamp >= :startTime")
    LiveData<Integer> getSkipCountSince(long startTime);
    
    @Query("SELECT SUM(timeSavedSeconds) FROM skip_records WHERE timestamp >= :startTime")
    LiveData<Integer> getTimeSavedSince(long startTime);
    
    @Query("DELETE FROM skip_records")
    void deleteAll();
}
