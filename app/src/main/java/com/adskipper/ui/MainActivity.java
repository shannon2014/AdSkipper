package com.adskipper.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.adskipper.R;
import com.adskipper.data.PreferencesManager;
import com.adskipper.ui.fragment.DashboardFragment;
import com.adskipper.ui.fragment.HistoryFragment;
import com.adskipper.ui.fragment.SettingsFragment;
import com.adskipper.utils.AppScanner;
import com.adskipper.utils.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate()
        ThemeManager.applyThemeToActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check first run and initialize app list
        PreferencesManager prefs = new PreferencesManager(this);
        if (prefs.isFirstRun()) {
            AppScanner.scanAndSaveApps(this);
            prefs.setFirstRun(false);
        }

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.navigation_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        }
    }
}
