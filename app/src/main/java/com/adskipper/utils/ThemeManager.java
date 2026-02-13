package com.adskipper.utils;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.adskipper.R;
import com.adskipper.data.PreferencesManager;

public class ThemeManager {
    
    /**
     * Apply theme based on user preference
     */
    public static void applyTheme(Context context) {
        PreferencesManager prefsManager = new PreferencesManager(context);
        String theme = prefsManager.getTheme();
        
        switch (theme) {
            case PreferencesManager.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case PreferencesManager.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case PreferencesManager.THEME_DEFAULT:
            default:
                // Default theme uses a custom dark blue theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
    
    /**
     * Set and apply theme
     */
    public static void setTheme(Context context, String theme) {
        PreferencesManager prefsManager = new PreferencesManager(context);
        prefsManager.setTheme(theme);
        applyTheme(context);
    }
    
    /**
     * Get current theme resource ID
     */
    public static int getThemeResId(Context context) {
        PreferencesManager prefsManager = new PreferencesManager(context);
        String theme = prefsManager.getTheme();
        
        switch (theme) {
            case PreferencesManager.THEME_LIGHT:
                return R.style.Theme_AdSkipper_Light;
            case PreferencesManager.THEME_DARK:
                return R.style.Theme_AdSkipper_Dark;
            case PreferencesManager.THEME_DEFAULT:
            default:
                return R.style.Theme_AdSkipper;
        }
    }
    
    /**
     * Apply theme to activity
     */
    public static void applyThemeToActivity(Activity activity) {
        activity.setTheme(getThemeResId(activity));
    }
}
