package com.adskipper.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppPreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppPreference appPreference);

    @Update
    void update(AppPreference appPreference);

    @Query("SELECT * FROM app_preferences ORDER BY appName ASC")
    LiveData<List<AppPreference>> getAllApps();

    @Query("SELECT * FROM app_preferences WHERE isMonitored = 1 ORDER BY appName ASC")
    LiveData<List<AppPreference>> getMonitoredApps();

    @Query("SELECT * FROM app_preferences ORDER BY lastUsedTimestamp DESC LIMIT :limit")
    LiveData<List<AppPreference>> getRecentlyUsedApps(int limit);

    @Query("SELECT * FROM app_preferences WHERE packageName = :packageName")
    AppPreference getAppByPackage(String packageName);

    @Query("SELECT isMonitored FROM app_preferences WHERE packageName = :packageName")
    boolean isAppMonitored(String packageName);

    @Query("SELECT COUNT(*) FROM app_preferences WHERE isMonitored = 1")
    LiveData<Integer> getMonitoredAppsCount();

    @Query("SELECT * FROM app_preferences ORDER BY appName ASC")
    List<AppPreference> getAllAppsList();

    @Query("SELECT * FROM app_preferences WHERE appName LIKE '%' || :query || '%' ORDER BY appName ASC")
    LiveData<List<AppPreference>> searchApps(String query);

    @Query("DELETE FROM app_preferences")
    void deleteAll();
}
