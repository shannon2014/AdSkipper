package com.adskipper.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adskipper.R;
import com.adskipper.data.AppPreference;
import com.adskipper.data.SkipDatabase;
import com.adskipper.ui.adapter.AppAdapter;
import com.adskipper.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitoredAppsActivity extends AppCompatActivity implements AppAdapter.OnAppToggleListener {

    private AppAdapter adapter;
    private SkipDatabase database;
    private ExecutorService executorService;

    private List<AppPreference> allApps = new ArrayList<>();
    private boolean showRecentOnly = false;

    private RecyclerView recyclerApps;
    private EditText etSearch;
    private MaterialButton btnTabAll;
    private MaterialButton btnTabRecent;
    private TextView btnBack;
    private TextView btnSelectAll;
    private TextView tvMonitoringDesc;

    /**
     * Packages excluded from ad-skip monitoring by default.
     * These are common apps that should never be monitored:
     * - System dialer / contacts
     * - WeChat (social messaging, not ad-heavy on startup)
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
            "com.tencent.mm"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyThemeToActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitored_apps);

        database = SkipDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        initViews();
        setupListeners();
        loadInstalledApps();
    }

    private void initViews() {
        recyclerApps = findViewById(R.id.recycler_apps);
        etSearch = findViewById(R.id.et_search);
        btnTabAll = findViewById(R.id.btn_tab_all);
        btnTabRecent = findViewById(R.id.btn_tab_recent);
        btnBack = findViewById(R.id.btn_back);
        btnSelectAll = findViewById(R.id.btn_select_all);
        tvMonitoringDesc = findViewById(R.id.tv_monitoring_desc);

        adapter = new AppAdapter(getPackageManager(), this);
        recyclerApps.setLayoutManager(new LinearLayoutManager(this));
        recyclerApps.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSelectAll.setOnClickListener(v -> {
            boolean allSelected = true;
            for (AppPreference app : adapter.getApps()) {
                if (!app.isMonitored()) {
                    allSelected = false;
                    break;
                }
            }
            // Toggle: if all selected → deselect all; else select all
            boolean newState = !allSelected;
            adapter.selectAll(newState);
            // Save all changes immediately
            saveAllAppsAsync();
        });

        btnTabAll.setOnClickListener(v -> {
            showRecentOnly = false;
            btnTabAll.setBackgroundTintList(getColorStateList(R.color.primary_blue));
            btnTabAll.setTextColor(getColor(R.color.white));
            btnTabRecent.setBackgroundTintList(getColorStateList(R.color.background_card));
            btnTabRecent.setTextColor(getColor(R.color.text_secondary));
            filterApps(etSearch.getText().toString());
        });

        btnTabRecent.setOnClickListener(v -> {
            showRecentOnly = true;
            btnTabRecent.setBackgroundTintList(getColorStateList(R.color.primary_blue));
            btnTabRecent.setTextColor(getColor(R.color.white));
            btnTabAll.setBackgroundTintList(getColorStateList(R.color.background_card));
            btnTabAll.setTextColor(getColor(R.color.text_secondary));
            filterApps(etSearch.getText().toString());
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Scan installed apps and merge with saved preferences.
     * <p>
     * - Apps WITHOUT a launcher activity are excluded entirely.
     * - Our own app is excluded.
     * - Excluded packages (Phone, Contacts, WeChat) default to monitored=false.
     * - All other apps default to monitored=true (first time).
     */
    private void loadInstalledApps() {
        executorService.execute(() -> {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            // Load existing preferences
            List<AppPreference> savedPrefs = database.appPreferenceDao().getAllAppsList();
            java.util.Map<String, AppPreference> savedMap = new java.util.HashMap<>();
            if (savedPrefs != null) {
                for (AppPreference pref : savedPrefs) {
                    savedMap.put(pref.getPackageName(), pref);
                }
            }

            allApps.clear();
            List<AppPreference> newAppsToSave = new ArrayList<>();

            for (ApplicationInfo appInfo : installedApps) {
                // Skip apps without a launcher activity (no main Activity)
                if (pm.getLaunchIntentForPackage(appInfo.packageName) == null) {
                    continue;
                }
                // Skip our own app
                if (appInfo.packageName.equals(getPackageName())) {
                    continue;
                }

                String appName = pm.getApplicationLabel(appInfo).toString();
                AppPreference existing = savedMap.get(appInfo.packageName);

                if (existing != null) {
                    // Already in DB — use saved setting
                    allApps.add(existing);
                } else {
                    // New app — default to monitored=true, unless excluded
                    boolean defaultMonitored = !EXCLUDED_PACKAGES.contains(appInfo.packageName);
                    AppPreference newPref = new AppPreference(appInfo.packageName, appName, defaultMonitored);
                    allApps.add(newPref);
                    newAppsToSave.add(newPref);
                }
            }

            // Persist new apps immediately
            if (!newAppsToSave.isEmpty()) {
                for (AppPreference pref : newAppsToSave) {
                    database.appPreferenceDao().insert(pref);
                }
            }

            // Sort: Unmonitored first (false < true), then alphabetically
            Collections.sort(allApps, (a, b) -> {
                if (a.isMonitored() != b.isMonitored()) {
                    return Boolean.compare(a.isMonitored(), b.isMonitored());
                }
                return a.getAppName().compareToIgnoreCase(b.getAppName());
            });

            runOnUiThread(() -> {
                adapter.setApps(new ArrayList<>(allApps));
                updateMonitoringCount();
            });
        });
    }

    /**
     * Filter apps by search query and tab selection
     */
    private void filterApps(String query) {
        List<AppPreference> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (AppPreference app : allApps) {
            boolean matchesSearch = lowerQuery.isEmpty() ||
                    app.getAppName().toLowerCase().contains(lowerQuery);

            boolean matchesTab = !showRecentOnly ||
                    app.getLastUsedTimestamp() > 0;

            if (matchesSearch && matchesTab) {
                filtered.add(app);
            }
        }

        if (showRecentOnly) {
            Collections.sort(filtered, (a, b) -> Long.compare(b.getLastUsedTimestamp(), a.getLastUsedTimestamp()));
        }

        adapter.setApps(filtered);
    }

    /**
     * Persist a single app preference change to the database immediately.
     */
    private void saveAppAsync(AppPreference app) {
        executorService.execute(() -> {
            database.appPreferenceDao().insert(app);
        });
    }

    /**
     * Persist all app preferences (used by Select All / Deselect All).
     */
    private void saveAllAppsAsync() {
        executorService.execute(() -> {
            for (AppPreference app : allApps) {
                database.appPreferenceDao().insert(app);
            }
        });
        updateMonitoringCount();
    }

    private void updateMonitoringCount() {
        int count = 0;
        for (AppPreference app : allApps) {
            if (app.isMonitored()) {
                count++;
            }
        }
        tvMonitoringDesc.setText(getString(R.string.service_active_desc, count));
    }

    @Override
    public void onAppToggled(AppPreference app, boolean isMonitored) {
        // Update the master list
        for (AppPreference a : allApps) {
            if (a.getPackageName().equals(app.getPackageName())) {
                a.setMonitored(isMonitored);
                break;
            }
        }
        // Save immediately — no confirmation needed
        saveAppAsync(app);
        updateMonitoringCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
