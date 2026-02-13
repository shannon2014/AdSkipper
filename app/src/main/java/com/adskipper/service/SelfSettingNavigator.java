package com.adskipper.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Opens the current app's battery / power management settings page.
 * Covers stock Android, Xiaomi/MIUI, Huawei/EMUI, OPPO/ColorOS,
 * Vivo/FuntouchOS, Samsung OneUI, etc.
 * <p>
 * If the OEM-specific intent fails, falls back to stock Android
 * battery optimization settings.
 */
public class SelfSettingNavigator {

    private static final String TAG = "SelfSettingNavigator";

    /**
     * Open the battery/power management settings page for the current app.
     */
    public static void openBatterySettings(Context context) {
        String packageName = context.getPackageName();
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        Log.d(TAG, "Opening battery settings for " + packageName
                + ", manufacturer=" + manufacturer);

        boolean launched = false;

        try {
            if (manufacturer.contains("xiaomi") || manufacturer.contains("redmi")) {
                launched = openXiaomiBatterySetting(context, packageName);
            } else if (manufacturer.contains("huawei") || manufacturer.contains("honor")) {
                launched = openHuaweiBatterySetting(context, packageName);
            } else if (manufacturer.contains("oppo") || manufacturer.contains("realme")
                    || manufacturer.contains("oneplus")) {
                launched = openOppoBatterySetting(context, packageName);
            } else if (manufacturer.contains("vivo")) {
                launched = openVivoBatterySetting(context, packageName);
            } else if (manufacturer.contains("samsung")) {
                launched = openSamsungBatterySetting(context, packageName);
            }
        } catch (Exception e) {
            Log.w(TAG, "OEM-specific intent failed: " + e.getMessage());
        }

        // Fallback: stock Android app detail settings → user can find Battery section
        if (!launched) {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Log.d(TAG, "Opened app detail settings (fallback)");
            } catch (Exception e) {
                Log.e(TAG, "Failed to open any settings page", e);
            }
        }
    }

    // ---- Xiaomi / MIUI ----
    private static boolean openXiaomiBatterySetting(Context context, String packageName) {
        // MIUI: Hidden power manager
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened Xiaomi power manager");
            return true;
        } catch (Exception ignored) {
        }

        // Fallback: app detail page on MIUI
        intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened MIUI app perm editor");
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    // ---- Huawei / EMUI ----
    private static boolean openHuaweiBatterySetting(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened Huawei startup manager");
            return true;
        } catch (Exception ignored) {
        }

        // Fallback: power management
        intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened Huawei power management");
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    // ---- OPPO / ColorOS / OnePlus / Realme ----
    private static boolean openOppoBatterySetting(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened OPPO startup manager");
            return true;
        } catch (Exception ignored) {
        }

        // Fallback: battery optimization
        intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.coloros.oppoguardelf",
                "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened OPPO power consumption");
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    // ---- Vivo / FuntouchOS ----
    private static boolean openVivoBatterySetting(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened Vivo background startup manager");
            return true;
        } catch (Exception ignored) {
        }

        // Fallback
        intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.iqoo.powersaving",
                "com.iqoo.powersaving.PowerSavingManagerActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened Vivo power saving");
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    // ---- Samsung / OneUI ----
    private static boolean openSamsungBatterySetting(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.samsung.android.lool",
                "com.samsung.android.sm.battery.ui.BatteryActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened Samsung battery manager");
            return true;
        } catch (Exception ignored) {
        }

        // Fallback: Device care
        intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.samsung.android.lool",
                "com.samsung.android.sm.ui.battery.BatteryActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            Log.d(TAG, "Opened Samsung device care battery");
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }
}
