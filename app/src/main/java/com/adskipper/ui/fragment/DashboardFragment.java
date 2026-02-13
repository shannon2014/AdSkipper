package com.adskipper.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.adskipper.R;
import com.adskipper.data.PreferencesManager;
import com.adskipper.ui.MonitoredAppsActivity;
import com.adskipper.ui.viewmodel.DashboardViewModel;
import com.adskipper.utils.PermissionHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private PreferencesManager prefsManager;

    private TextView tvTotalSkipped;
    private TextView tvMonitoredCount;
    private SwitchMaterial switchAdSkipping;
    private View layoutPermissionNotice;
    private View layoutBatteryNotice;
    private MaterialButton btnGrantPermission;
    private MaterialButton btnGrantBattery;
    private CardView cardMonitoredApps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsManager = new PreferencesManager(requireContext());
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        initViews(view);
        setupListeners();
        observeData();
        updateServiceStatus();
        updateBatteryIgnoringStatus();
    }

    private void initViews(View view) {
        tvTotalSkipped = view.findViewById(R.id.tv_total_skipped);
        tvMonitoredCount = view.findViewById(R.id.tv_monitored_count);
        switchAdSkipping = view.findViewById(R.id.switch_ad_skipping);
        layoutPermissionNotice = view.findViewById(R.id.layout_permission_notice);
        layoutBatteryNotice = view.findViewById(R.id.layout_battery_notice);
        btnGrantPermission = view.findViewById(R.id.btn_grant_permission);
        btnGrantBattery = view.findViewById(R.id.btn_grant_battrey);
        cardMonitoredApps = view.findViewById(R.id.card_monitored_apps);
    }

    private void setupListeners() {
        
        switchAdSkipping.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Only update preference if the change was initiated by user interaction
            if (buttonView.isPressed()) {
                prefsManager.setAdSkippingEnabled(isChecked);
            }
        });

        btnGrantPermission.setOnClickListener(v -> {
            PermissionHelper.openAccessibilitySettings(requireContext());
        });

        cardMonitoredApps.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MonitoredAppsActivity.class);
            startActivity(intent);
        });

        btnGrantBattery.setOnClickListener(v -> {
            PermissionHelper.openAppSettings(requireContext());
        });
    }

    private void observeData() {
        viewModel.getTotalSkipCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvTotalSkipped.setText(String.format("%,d", count));
            }
        });

        viewModel.getMonitoredAppsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvMonitoredCount.setText(getString(R.string.apps_count, count));
            }
        });
    }

    private void updateBatteryIgnoringStatus() {
        boolean isBatteryIgnoring = PermissionHelper.isBatteryIgnoring(requireContext());
        Log.d("hegui", "updateBatteryIgnoringStatus() isBatteryIgnoring = " + isBatteryIgnoring);
        if (isBatteryIgnoring) {
            prefsManager.setBatterySetupDone(true);
            layoutBatteryNotice.setVisibility(View.GONE);
        } else {
            prefsManager.setBatterySetupDone(false);
            layoutBatteryNotice.setVisibility(View.VISIBLE);
        }
    }

    private void updateServiceStatus() {
        boolean isPermissionGranted = PermissionHelper.isAccessibilityServiceEnabled(requireContext());

        if (isPermissionGranted) {
            prefsManager.setServiceEnabled(true);
            layoutPermissionNotice.setVisibility(View.GONE);
            
            // Ad Skipping Switch Logic
            boolean isAdSkippingEnabled = prefsManager.isAdSkippingEnabled();
            switchAdSkipping.setEnabled(true);
            switchAdSkipping.setChecked(isAdSkippingEnabled);
            
        } else {
            prefsManager.setServiceEnabled(false);
            layoutPermissionNotice.setVisibility(View.VISIBLE);
            
            // If permission is OFF, Ad Skipping Switch must be OFF and Disabled
            switchAdSkipping.setEnabled(false);
            switchAdSkipping.setChecked(false); 
            // Note: We are NOT updating the preference here, only the visual state.
            // When permission is re-enabled, it will restore from preference in the 'if' block above.
        }
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return getString(R.string.time_format_hours, hours, minutes);
        } else {
            return getString(R.string.time_format_minutes, minutes);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateServiceStatus();
        updateBatteryIgnoringStatus();
        // 垃圾小米手机，大概率不准，这里再试一下
        if (!prefsManager.isServiceEnabled()) {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                updateServiceStatus();
                updateBatteryIgnoringStatus();
            }, 500);
        }
    }
}
