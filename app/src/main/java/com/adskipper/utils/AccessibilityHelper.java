package com.adskipper.utils;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.Log;

/**
 * Description:
 * Created by hegui on 2026/2/13. 11:00
 */
public class AccessibilityHelper {
    private static final String TAG = "SkipAdsHelper";

    /**
     * 1. 模拟点击返回键
     * 常用场景：关闭无法通过点击节点关闭的全屏广告
     */
    public static boolean performBack(AccessibilityService service) {
        return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 2. 模拟点击 Home 键
     */
    public static boolean performHome(AccessibilityService service) {
        return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /**
     * 3. 模拟点击“最近任务”键
     */
    public static boolean performRecents(AccessibilityService service) {
        return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    /**
     * 4. 下拉打开通知栏
     */
    public static boolean openNotifications(AccessibilityService service) {
        return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    /**
     * 5. 锁定屏幕
     * 需 Android 9.0 (API 28) +
     */
    public static boolean lockScreen(AccessibilityService service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
        }
        Log.e(TAG, "锁定屏幕失败：需 Android 9.0+");
        return false;
    }

    /**
     * 6. 系统截屏
     * 需 Android 9.0 (API 28) +
     */
    public static boolean takeScreenshot(AccessibilityService service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
        }
        return false;
    }

    /**
     * 7. 收起通知栏 / 快速设置
     * 需 Android 12.0 (API 31) +
     */
    public static boolean dismissNotificationShade(AccessibilityService service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE);
        }
        // 低版本兼容方案：发送返回键通常能收起通知栏
        return performBack(service);
    }

    /**
     * 8. 切换分屏
     * 需 Android 7.0 (API 24) +
     */
    public static boolean toggleSplitScreen(AccessibilityService service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
        }
        return false;
    }

    /**
     * 9. 通用安全执行方法
     * 适用于所有全局操作常量
     */
    public static boolean performActionSafe(AccessibilityService service, int action) {
        return service.performGlobalAction(action);
    }
}
