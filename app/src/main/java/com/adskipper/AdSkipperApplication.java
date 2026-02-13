package com.adskipper;

import android.app.Application;

// import com.adskipper.utils.OcrHelper;
import com.adskipper.utils.ThemeManager;

public class AdSkipperApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Apply saved theme
        ThemeManager.applyTheme(this);
        
        // Initialize OCR
        // OcrHelper.initialize();
    }
}
