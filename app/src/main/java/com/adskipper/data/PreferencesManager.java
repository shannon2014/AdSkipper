package com.adskipper.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "AdSkipperPrefs";
    private static final String KEY_SERVICE_ENABLED = "service_enabled";
    private static final String KEY_AD_SKIPPING_ENABLED = "ad_skipping_enabled";
    private static final String KEY_THEME = "theme";
    private static final String KEY_FIRST_RUN = "first_run";
    private static final String KEY_SKIP_SENSITIVITY = "skip_sensitivity";

    public static final String THEME_DEFAULT = "default";
    public static final String THEME_DARK = "dark";
    public static final String THEME_LIGHT = "light";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isServiceEnabled() {
        return prefs.getBoolean(KEY_SERVICE_ENABLED, false);
    }

    public void setServiceEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply();
    }

    public boolean isAdSkippingEnabled() {
        return prefs.getBoolean(KEY_AD_SKIPPING_ENABLED, true);
    }

    public void setAdSkippingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AD_SKIPPING_ENABLED, enabled).apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, THEME_DEFAULT);
    }

    public void setTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    public boolean isFirstRun() {
        return prefs.getBoolean(KEY_FIRST_RUN, true);
    }

    public void setFirstRun(boolean firstRun) {
        prefs.edit().putBoolean(KEY_FIRST_RUN, firstRun).apply();
    }

    // --- Battery optimization auto-setup ---

    private static final String KEY_BATTERY_SETUP_DONE = "battery_setup_done";

    public boolean isBatterySetupDone() {
        return prefs.getBoolean(KEY_BATTERY_SETUP_DONE, false);
    }

    public void setBatterySetupDone(boolean done) {
        prefs.edit().putBoolean(KEY_BATTERY_SETUP_DONE, done).apply();
    }
}
