package com.adskipper.service;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Auto-clicks through battery/power management settings pages to allow
 * unrestricted background activity for this app.
 * <p>
 * When the accessibility service detects it is on a settings page
 * (from a system settings or OEM power manager package), this class
 * searches for UI elements that match known "allow background" / "unrestricted"
 * patterns and clicks them.
 * <p>
 * Supports stock Android, Xiaomi/MIUI, Huawei/EMUI, OPPO/ColorOS,
 * Vivo/FuntouchOS, Samsung OneUI.
 */
public class BatterySettingAutoClicker {

    private static final String TAG = "BatteryAutoClicker";

    /**
     * Packages that host battery/power management settings
     */
    private static final Set<String> SETTINGS_PACKAGES = new HashSet<>(Arrays.asList(
            "com.android.settings",
            "com.miui.powerkeeper",
            "com.miui.securitycenter",
            "com.huawei.systemmanager",
            "com.coloros.safecenter",
            "com.coloros.oppoguardelf",
            "com.vivo.permissionmanager",
            "com.iqoo.powersaving",
            "com.samsung.android.lool",
            "com.samsung.android.sm",
            "com.oplus.battery"));

    /**
     * Text patterns that indicate "unrestricted / allow background" options.
     * We want to CLICK these to enable full background activity.
     */
    private static final Set<String> ALLOW_BACKGROUND_PATTERNS = new HashSet<>(Arrays.asList(
            // Chinese
            "无限制", "不限制", "允许后台活动", "允许后台运行",
            "完全允许后台行为",
            "不受限制", "无限制后台",
            "允许自启动", "允许后台弹出",
            "不优化", "不进行优化",
            "后台不限制", "电池优化不受限",
            // English
            "unrestricted", "no restrictions",
            "allow background activity",
            "don't optimize", "not optimized",
            "allow auto-launch", "no restriction"));

    /**
     * Text patterns for the app's own name — used to first locate our app
     * in a list of apps on the settings page, then click into its detail.
     */
    private static final String APP_LABEL = "Ad Skipper";

    /**
     * Check if this package is a settings / power manager page,
     * and if so, try to click the unrestricted background option.
     *
     * @return true if we handled (clicked) something
     */
    public static boolean tryAutoSetup(AccessibilityNodeInfo rootNode,
            String packageName,
            String selfAppLabel) {
        if (!SETTINGS_PACKAGES.contains(packageName)) {
            return false;
        }

        Log.d(TAG, "Detected settings page from: " + packageName);

        // Strategy 1: Look for our app name in a list and click it
        if (selfAppLabel != null && !selfAppLabel.isEmpty()) {
            if (findAndClickText(rootNode, selfAppLabel)) {
                Log.d(TAG, "Clicked our app entry: " + selfAppLabel);
                return true;
            }
        }

        // Strategy 2: Look for "Unrestricted" / "无限制" / "允许后台活动" etc.
        // and click it (it may be a radio button or a list option)
        Queue<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(rootNode);

        boolean found = false;

        try {
            while (!queue.isEmpty()) {
                AccessibilityNodeInfo node = queue.poll();
                
                if (node == null) continue;

                // Process node
                try {
                    CharSequence text = node.getText();
                    if (text != null) {
                        String lowerText = text.toString().trim().toLowerCase();
                        for (String pattern : ALLOW_BACKGROUND_PATTERNS) {
                            if (lowerText.contains(pattern.toLowerCase())) {
                                // Check if it's already selected (e.g. a RadioButton)
                                if (node.isChecked()) {
                                    Log.d(TAG, "Option already selected: " + text);
                                    found = true;
                                    break;
                                }

                                // Try to click it
                                if (clickNode(node)) {
                                    Log.d(TAG, "Clicked background option: " + text);
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!found) {
                        // Also check content description
                        CharSequence desc = node.getContentDescription();
                        if (desc != null) {
                            String lowerDesc = desc.toString().trim().toLowerCase();
                            for (String pattern : ALLOW_BACKGROUND_PATTERNS) {
                                if (lowerDesc.contains(pattern.toLowerCase())) {
                                    if (!node.isChecked()) {
                                        if (clickNode(node)) {
                                            Log.d(TAG, "Clicked by content-desc: " + desc);
                                            found = true;
                                            break;
                                        }
                                    } else {
                                        Log.d(TAG, "Already selected (desc): " + desc);
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (found) {
                        break;
                    }

                    // Add children
                    int childCount = node.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        AccessibilityNodeInfo child = node.getChild(i);
                        if (child != null) {
                            queue.add(child);
                        }
                    }

                } finally {
                    if (node != rootNode) {
                        node.recycle();
                    }
                }
            }
        } finally {
            // Cleanup remaining nodes
            while (!queue.isEmpty()) {
                AccessibilityNodeInfo n = queue.poll();
                if (n != null && n != rootNode) {
                    n.recycle();
                }
            }
        }

        return found;
    }

    /**
     * Find a node containing the given text and click it.
     */
    private static boolean findAndClickText(AccessibilityNodeInfo rootNode, String targetText) {
        if (rootNode == null) return false;
        
        Queue<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(rootNode);
        boolean found = false;

        try {
            while (!queue.isEmpty()) {
                AccessibilityNodeInfo node = queue.poll();
                if (node == null) continue;

                try {
                    CharSequence text = node.getText();
                    if (text != null && text.toString().contains(targetText)) {
                        if (clickNode(node)) {
                            found = true;
                            break;
                        }
                    }

                    int childCount = node.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        AccessibilityNodeInfo child = node.getChild(i);
                        if (child != null) {
                            queue.add(child);
                        }
                    }
                } finally {
                    if (node != rootNode) {
                        node.recycle();
                    }
                }
            }
        } finally {
            while (!queue.isEmpty()) {
                AccessibilityNodeInfo n = queue.poll();
                if (n != null && n != rootNode) {
                    n.recycle();
                }
            }
        }
        
        return found;
    }

    /**
     * Click a node. Walk up to find a clickable ancestor if needed.
     */
    private static boolean clickNode(AccessibilityNodeInfo node) {
        if (node.isClickable()) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

        AccessibilityNodeInfo parent = node.getParent();
        int depth = 0;
        while (parent != null && depth < 5) {
            if (parent.isClickable()) {
                boolean result = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                parent.recycle();
                return result;
            }
            AccessibilityNodeInfo grandparent = parent.getParent();
            parent.recycle();
            parent = grandparent;
            depth++;
        }
        
        if (parent != null) {
            parent.recycle();
        }
        
        return false;
    }
}