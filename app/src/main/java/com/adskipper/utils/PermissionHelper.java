package com.adskipper.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import com.adskipper.service.AdSkipperAccessibilityService;

import java.util.List;

public class PermissionHelper {
    
    /**
     * Check if accessibility service is enabled
     */
    public static boolean isAccessibilityServiceEnabled(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am == null) return false;
        
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        if (enabledServices != null) {
            for (AccessibilityServiceInfo enabledService : enabledServices) {
                ServiceInfo serviceInfo = enabledService.getResolveInfo().serviceInfo;
                if (serviceInfo.packageName.equals(context.getPackageName()) && 
                    serviceInfo.name.equals(AdSkipperAccessibilityService.class.getName())) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public static boolean isBatteryIgnoring(Context context){
        // Auto-setup: open battery settings if not done yet
        // Check actual system state first
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isIgnoring = false;
        if (pm != null) {
            isIgnoring = pm.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }
    
    /**
     * Open accessibility settings page
     */
    public static void openAccessibilitySettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    /**
     * Request file access permission
     */
    public static void requestFilePermission(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }
    
    /**
     * Open app settings page
     */
    public static void openAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
