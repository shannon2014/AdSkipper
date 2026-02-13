package com.adskipper.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_preferences")
public class AppPreference {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String packageName;
    private String appName;
    private boolean isMonitored;
    private long lastUsedTimestamp;
    
    public AppPreference() {
    }
    
    public AppPreference(String packageName, String appName, boolean isMonitored) {
        this.packageName = packageName;
        this.appName = appName;
        this.isMonitored = isMonitored;
        this.lastUsedTimestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getAppName() {
        return appName;
    }
    
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public boolean isMonitored() {
        return isMonitored;
    }
    
    public void setMonitored(boolean monitored) {
        isMonitored = monitored;
    }
    
    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }
    
    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }
}
