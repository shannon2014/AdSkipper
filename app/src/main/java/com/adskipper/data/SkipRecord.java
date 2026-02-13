package com.adskipper.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "skip_records")
public class SkipRecord {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String packageName;
    private String appName;
    private long timestamp;
    private String adType; // "startup", "interstitial", "video"
    private int timeSavedSeconds; // Estimated time saved
    private String skipMethod; // "text", "ocr", "id"
    
    public SkipRecord() {
    }
    
    @Ignore
    public SkipRecord(String packageName, String appName, long timestamp, 
                     String adType, int timeSavedSeconds, String skipMethod) {
        this.packageName = packageName;
        this.appName = appName;
        this.timestamp = timestamp;
        this.adType = adType;
        this.timeSavedSeconds = timeSavedSeconds;
        this.skipMethod = skipMethod;
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
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getAdType() {
        return adType;
    }
    
    public void setAdType(String adType) {
        this.adType = adType;
    }
    
    public int getTimeSavedSeconds() {
        return timeSavedSeconds;
    }
    
    public void setTimeSavedSeconds(int timeSavedSeconds) {
        this.timeSavedSeconds = timeSavedSeconds;
    }
    
    public String getSkipMethod() {
        return skipMethod;
    }
    
    public void setSkipMethod(String skipMethod) {
        this.skipMethod = skipMethod;
    }
}