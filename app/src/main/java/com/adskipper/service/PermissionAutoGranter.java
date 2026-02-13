package com.adskipper.service;

import android.accessibilityservice.AccessibilityService;

import java.util.List;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Auto-grants permissions (file access, camera, background) by detecting
 * permission dialogs and clicking "Allow" buttons via the accessibility
 * service.
 * <p>
 * Works on Android 10+ by detecting the system permission dialog UI and
 * automatically clicking the appropriate grant buttons.
 */
public class PermissionAutoGranter {

    private static final String TAG = "PermissionAutoGranter";

    // System packages that show permission dialogs
    private static final Set<String> PERMISSION_DIALOG_PACKAGES = new HashSet<>(Arrays.asList(
            "com.android.permissioncontroller",
            "com.android.packageinstaller",
            "com.google.android.permissioncontroller",
            "com.google.android.packageinstaller",
            "com.samsung.android.permissioncontroller" // Samsung
    ));

    // "Allow" button text patterns in multiple languages
    private static final Set<String> ALLOW_PATTERNS = new HashSet<>(Arrays.asList(
            // English
            "allow", "allow all the time", "while using the app",
            "allow only while using the app",
            // Chinese
            "允许", "始终允许", "仅在使用时允许", "使用时允许",
            "仅在使用该应用时允许"));

    // "Allow" button resource ID patterns (varies by OEM)
    private static final Set<String> ALLOW_BUTTON_IDS = new HashSet<>(Arrays.asList(
            "permission_allow_button",
            "permission_allow_foreground_only_button",
            "permission_allow_always_button",
            "permission_allow_one_time_button"));

    /**
     * Check if the current package is a permission dialog and attempt to
     * auto-grant.
     *
     * @param rootNode    the root node of the current window
     * @param packageName the package name of the current foreground app
     * @param service     the accessibility service instance (for gestures)
     * @return true if a permission dialog was detected and an action was taken
     */
    public static boolean tryAutoGrant(AccessibilityNodeInfo rootNode,
            String packageName,
            AccessibilityService service) {
        if (!PERMISSION_DIALOG_PACKAGES.contains(packageName)) {
            return false;
        }

        Log.d(TAG, "Permission dialog detected from: " + packageName);

        // Strategy 1: Search by resource ID
        for (String idSuffix : ALLOW_BUTTON_IDS) {
            for (String pkg : PERMISSION_DIALOG_PACKAGES) {
                String fullId = pkg + ":id/" + idSuffix;
                List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(fullId);
                if (nodes != null) {
                    try {
                        if (!nodes.isEmpty()) {
                            for (AccessibilityNodeInfo node : nodes) {
                                if (node.isClickable() && node.isVisibleToUser()) {
                                    boolean clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    Log.d(TAG, "Clicked allow button by ID '" + fullId + "': " + clicked);
                                    if (clicked)
                                        return true;
                                }
                            }
                        }
                    } finally {
                        for (AccessibilityNodeInfo node : nodes) {
                            if (node != null) node.recycle();
                        }
                    }
                }
            }
        }

        // Strategy 2: BFS to find "Allow" text buttons
        Queue<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(rootNode);

        boolean found = false;
        
        try {
            while (!queue.isEmpty()) {
                AccessibilityNodeInfo node = queue.poll();
                if (node == null)
                    continue;

                try {
                    CharSequence text = node.getText();
                    if (text != null) {
                        String lowerText = text.toString().trim().toLowerCase();
                        if (ALLOW_PATTERNS.contains(lowerText)) {
                            // Found a matching button
                            if (clickNode(node)) {
                                Log.d(TAG, "Clicked allow button by text: " + text);
                                found = true;
                                break;
                            }
                        }
                    }

                    // Also check content description
                    if (!found) {
                        CharSequence desc = node.getContentDescription();
                        if (desc != null) {
                            String lowerDesc = desc.toString().trim().toLowerCase();
                            if (ALLOW_PATTERNS.contains(lowerDesc)) {
                                if (clickNode(node)) {
                                    Log.d(TAG, "Clicked allow button by content-description: " + desc);
                                    found = true;
                                    break;
                                }
                            }
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

        if (found) return true;

        Log.d(TAG, "No allow button found in permission dialog");
        return false;
    }

    /**
     * Click a node. If the node itself is not clickable, try its parent.
     */
    private static boolean clickNode(AccessibilityNodeInfo node) {
        if (node.isClickable()) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

        // Walk up to find a clickable parent
        AccessibilityNodeInfo parent = node.getParent();
        int depth = 0;
        final int MAX_DEPTH = 5;
        
        while (parent != null && depth < MAX_DEPTH) {
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