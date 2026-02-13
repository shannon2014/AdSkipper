package com.adskipper.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.adskipper.data.AppPreference;
import com.adskipper.data.SkipDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppScanner {

    private static final String TAG = "AppScanner";

    /**
     * Packages excluded from ad-skip monitoring by default.
     */
    private static final Set<String> EXCLUDED_PACKAGES = new HashSet<>(Arrays.asList(
            // System phone / dialer
            "com.android.dialer",
            "com.android.phone",
            "com.google.android.dialer",
            "com.samsung.android.dialer",
            "com.samsung.android.incallui",
            "com.miui.phone",
            // System contacts
            "com.android.contacts",
            "com.google.android.contacts",
            "com.samsung.android.contacts",
            // WeChat
            "com.tencent.mm"
    ));

    public static void scanAndSaveApps(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Log.d(TAG, "Starting app scan...");
                PackageManager pm = context.getPackageManager();
                List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                SkipDatabase database = SkipDatabase.getInstance(context);

                // Load existing preferences to avoid duplicates
                List<AppPreference> savedPrefs = database.appPreferenceDao().getAllAppsList();
                Set<String> existingPackages = new HashSet<>();
                if (savedPrefs != null) {
                    for (AppPreference pref : savedPrefs) {
                        existingPackages.add(pref.getPackageName());
                    }
                }

                List<AppPreference> newAppsToSave = new ArrayList<>();

                for (ApplicationInfo appInfo : installedApps) {
                    // Skip apps without a launcher activity (no main Activity)
                    if (pm.getLaunchIntentForPackage(appInfo.packageName) == null) {
                        continue;
                    }
                    // Skip our own app
                    if (appInfo.packageName.equals(context.getPackageName())) {
                        continue;
                    }
                    // Skip if already in DB
                    if (existingPackages.contains(appInfo.packageName)) {
                        continue;
                    }

                    String appName = pm.getApplicationLabel(appInfo).toString();
                    
                    // New app — default to monitored=true, unless excluded
                    boolean defaultMonitored = !EXCLUDED_PACKAGES.contains(appInfo.packageName);
                    AppPreference newPref = new AppPreference(appInfo.packageName, appName, defaultMonitored);
                    newAppsToSave.add(newPref);
                }

                if (!newAppsToSave.isEmpty()) {
                    for (AppPreference pref : newAppsToSave) {
                        database.appPreferenceDao().insert(pref);
                    }
                    Log.d(TAG, "Added " + newAppsToSave.size() + " new apps to database.");
                } else {
                    Log.d(TAG, "No new apps found.");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error scanning apps", e);
            } finally {
                executor.shutdown();
            }
        });
    }
}
