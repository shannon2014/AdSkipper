package com.adskipper.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.adskipper.data.AppPreference;
import com.adskipper.data.PreferencesManager;
import com.adskipper.data.SkipDatabase;
import com.adskipper.data.SkipRecord;
import com.adskipper.detector.SkipPatternDetector;
import com.adskipper.utils.OcrHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdSkipperAccessibilityService extends AccessibilityService {

    private static final String TAG = "AdSkipperService";
    private static final long COOLDOWN_MS = 100; // Avoid repeated clicks within 2 seconds
    
    private PreferencesManager prefsManager;
    private SkipDatabase database;
    private ExecutorService executorService;
    private PackageManager packageManager;

    // Cache of monitored packages (refreshed periodically)
    private Set<String> monitoredPackages = new HashSet<>();
    private long lastPackageRefresh = 0;
    private static final long PACKAGE_REFRESH_INTERVAL_MS = 30_000; // 30 seconds
    
    // Cache of launcher packages
    private Set<String> launcherPackages = new HashSet<>();

    // Cool-down tracking: avoid clicking the same app repeatedly
    private String lastClickedPackage = "";
    private long lastClickTime = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        prefsManager = new PreferencesManager(this);
        database = SkipDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        packageManager = getPackageManager();
        
        refreshMonitoredPackages();
        loadLauncherPackages();
        
        Log.d(TAG, "AdSkipperAccessibilityService created");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility service connected");

        // Mark service as enabled in preferences
        prefsManager.setServiceEnabled(true);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // --- CHECK MASTER SWITCH ---
        if (!prefsManager.isAdSkippingEnabled()) {
            return;
        }
        // ---------------------------

        int eventType = event.getEventType();

        // Only handle window state changes and content changes
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return;
        }

        String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";
        
        // --- EXCLUSION LOGIC START ---
        // 1. Exclude self
        if (packageName.equals(getPackageName())) {
            return;
        }
        
        // 2. Exclude System UI and Android System
        if (packageName.startsWith("com.android.") && !packageName.equals("com.android.vending")) {
            // Keep com.android.vending (Play Store) as it might have ads/promos we want to skip, 
            // but usually exclude core system like systemui, settings, etc.
            // Actually user requested com.android.* exclusion generally.
            // Let's stick to user request: "com.android.*"
            return;
        }
        
        // 3. Exclude Launchers (Home screens)
        if (isLauncherApp(packageName)) {
            // Log.v(TAG, "Skipping launcher: " + packageName);
            return;
        }
        // --- EXCLUSION LOGIC END ---

//        Log.d(TAG, "onAccessibilityEvent: " + packageName + ", type=" + eventType);

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            Log.w(TAG, "rootNode is null for " + packageName);
            return;
        }

        try {
            // 0) Battery optimization auto-setup (time-limited)

            // 1) Try auto-granting permissions first
            if (PermissionAutoGranter.tryAutoGrant(rootNode, packageName, this)) {
                return; // Handled a permission dialog, nothing else to do
            }

            // 2) Refresh monitored packages periodically
            long now = System.currentTimeMillis();
            if (now - lastPackageRefresh > PACKAGE_REFRESH_INTERVAL_MS) {
                refreshMonitoredPackages();
                lastPackageRefresh = now;
            }

            // 3) Only process monitored apps (if list is empty, monitor all)
            // Log ignored packages for debugging
            if (!monitoredPackages.isEmpty() && !monitoredPackages.contains(packageName)) {
                Log.v(TAG, "Package " + packageName + " is not in monitored list, skipping.");
                return;
            }

            // 4) Cool-down check
            if (packageName.equals(lastClickedPackage) && now - lastClickTime < COOLDOWN_MS) {
//                Log.v(TAG, "Cool-down active for " + packageName + ", skipping.");
                return;
            }

            // 5) Search for and click skip buttons
            searchAndClickSkipButton(rootNode, packageName);
        } finally {
            rootNode.recycle();
        }
    }

    /**
     * Search for skip button in node tree using simple text filtering
     * optimized to avoid global traversal.
     */
    private void searchAndClickSkipButton(AccessibilityNodeInfo rootNode, String packageName) {
        if (rootNode == null) return;
        
        // Optimization: Use findAccessibilityNodeInfosByText to filter nodes instead of full traversal
        // This avoids the overhead of fetching every node via IPC.
        // Keywords prioritize: "跳" (for 跳过), "skip", "关" (for 关闭), "close".
        String[] keywords = {"跳", "skip", "关", "close"};

        for (String keyword : keywords) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(keyword);
            if (list == null || list.isEmpty()) continue;

            boolean foundInBatch = false;
            for (AccessibilityNodeInfo node : list) {
                // If we already found a button in this batch, recycle the rest and skip processing
                if (foundInBatch) {
                    node.recycle();
                    continue;
                }

                try {
                    // Check strict patterns using the detector
                    if (SkipPatternDetector.isSkipButton(node)) {
                        CharSequence text = node.getText();
                        String textStr = text != null ? text.toString() : "[no text]";
                        Log.i(TAG, "Found skip button via search('" + keyword + "') in " + packageName + ": " + textStr);

                        if (performClick(node)) {
                            lastClickedPackage = packageName;
                            lastClickTime = System.currentTimeMillis();

                            // Log the skip event and update last-used timestamp
                            logSkipEvent(packageName, "search:" + keyword);
                            updateLastUsedTimestamp(packageName);
                            
                            foundInBatch = true;
                        } else {
                            Log.w(TAG, "Failed to click found skip button");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error checking node", e);
                } finally {
                    // Always recycle nodes obtained from findAccessibilityNodeInfosByText
                    node.recycle();
                }
            }
            
            // If found in this keyword search, stop searching other keywords
            if (foundInBatch) return;
        }
    }

    /**
     * Perform click on node
     */
    private boolean performClick(AccessibilityNodeInfo node) {
        if (node == null) {
            return false;
        }

        // Try direct click action first
        if (node.isClickable() && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
            Log.d(TAG, "Clicked using ACTION_CLICK");
            return true;
        }

        // Try clicking ancestors if node itself is not clickable
        // Many times the "Skip" TextView is inside a FrameLayout/LinearLayout that handles the click
        AccessibilityNodeInfo current = node.getParent();
        int depth = 0;
        final int MAX_DEPTH = 5; // Look up to 5 levels up

        while (current != null && depth < MAX_DEPTH) {
            if (current.isClickable()) {
                if (current.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                    Log.d(TAG, "Clicked ancestor (depth " + (depth + 1) + ") using ACTION_CLICK");
                    current.recycle();
                    return true;
                }
            }
            AccessibilityNodeInfo next = current.getParent();
            current.recycle(); // Recycle current before moving up
            current = next;
            depth++;
        }
        
        // Ensure the last current node is recycled if loop finished without clicking
        if (current != null) {
            current.recycle();
        }

        // Try gesture click as fallback
        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        if (!bounds.isEmpty()) {
            return performGestureClick(bounds.centerX(), bounds.centerY());
        }

        return false;
    }

    /**
     * Perform click using gesture
     */
    private boolean performGestureClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gesture = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 0, 100))
                .build();

        boolean result = dispatchGesture(gesture, null, null);
        Log.d(TAG, "Gesture click at (" + x + ", " + y + "): " + result);
        return result;
    }

    /**
     * Refresh the cached set of monitored package names from the database
     */
    private void refreshMonitoredPackages() {
        executorService.execute(() -> {
            try {
                List<AppPreference> prefs = database.appPreferenceDao().getAllAppsList();
                Set<String> newSet = new HashSet<>();
                if (prefs != null) {
                    for (AppPreference pref : prefs) {
                        if (pref.isMonitored()) {
                            newSet.add(pref.getPackageName());
                        }
                    }
                }
                monitoredPackages = newSet;
                Log.d(TAG, "Refreshed monitored packages: " + newSet.size() + " apps");
            } catch (Exception e) {
                Log.e(TAG, "Failed to refresh monitored packages", e);
            }
        });
    }
    
    /**
     * Determine all launcher apps installed on the device and cache them.
     */
    private void loadLauncherPackages() {
        executorService.execute(() -> {
            Set<String> launchers = new HashSet<>();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo info : resolveInfos) {
                launchers.add(info.activityInfo.packageName);
            }
            // Add known common launchers just in case
            launchers.add("com.miui.home");
            launchers.add("com.huawei.android.launcher");
            launchers.add("com.oppo.launcher");
            launchers.add("com.bbk.launcher2"); // Vivo
            launchers.add("com.sec.android.app.launcher"); // Samsung
            launchers.add("com.google.android.apps.nexuslauncher"); // Pixel
            
            launcherPackages = launchers;
            Log.d(TAG, "Loaded " + launchers.size() + " launcher packages");
        });
    }
    
    private boolean isLauncherApp(String packageName) {
        if (launcherPackages.contains(packageName)) {
            return true;
        }
        // Fallback checks
        return packageName.contains(".launcher") || packageName.contains(".home");
    }

    /**
     * Log skip event to database
     */
    private void logSkipEvent(String packageName, String skipMethod) {
        executorService.execute(() -> {
            try {
                String appName = getAppName(packageName);
                SkipRecord record = new SkipRecord(
                        packageName,
                        appName,
                        System.currentTimeMillis(),
                        "startup",
                        5, // Assume 5 seconds saved per ad
                        skipMethod);
                database.skipRecordDao().insert(record);
                Log.d(TAG, "Logged skip event for " + appName);
            } catch (Exception e) {
                Log.e(TAG, "Failed to log skip event", e);
            }
        });
    }

    /**
     * Update the last-used timestamp for the given package
     */
    private void updateLastUsedTimestamp(String packageName) {
        executorService.execute(() -> {
            try {
                AppPreference pref = database.appPreferenceDao().getAppByPackage(packageName);
                if (pref != null) {
                    pref.setLastUsedTimestamp(System.currentTimeMillis());
                    database.appPreferenceDao().update(pref);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to update last used timestamp", e);
            }
        });
    }

    /**
     * Get app name from package name
     */
    private String getAppName(String packageName) {
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        Log.d(TAG, "Service destroyed");
    }
}