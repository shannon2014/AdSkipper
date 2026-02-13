package com.adskipper.ui.fragment;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.adskipper.R;
import com.adskipper.data.PreferencesManager;
import com.adskipper.data.SkipDatabase;
import com.adskipper.utils.ThemeManager;

import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private PreferencesManager prefsManager;

    private RadioGroup radioGroupTheme;
    private CardView cardClearData;
    private TextView tvVersion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsManager = new PreferencesManager(requireContext());

        initViews(view);
        loadSettings();
        setupListeners();
    }

    private void initViews(View view) {
        radioGroupTheme = view.findViewById(R.id.radio_group_theme);
        cardClearData = view.findViewById(R.id.card_clear_data);
        tvVersion = view.findViewById(R.id.tv_version);
    }

    private void loadSettings() {
        // Load theme
        String theme = prefsManager.getTheme();
        switch (theme) {
            case PreferencesManager.THEME_DEFAULT:
                radioGroupTheme.check(R.id.radio_theme_default);
                break;
            case PreferencesManager.THEME_DARK:
                radioGroupTheme.check(R.id.radio_theme_dark);
                break;
            case PreferencesManager.THEME_LIGHT:
                radioGroupTheme.check(R.id.radio_theme_light);
                break;
        }

        // Load version
        try {
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            String version = pInfo.versionName;
            if (tvVersion != null) {
                tvVersion.setText("Version " + version);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        // Theme selection
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            String theme;
            if (checkedId == R.id.radio_theme_default) {
                theme = PreferencesManager.THEME_DEFAULT;
            } else if (checkedId == R.id.radio_theme_dark) {
                theme = PreferencesManager.THEME_DARK;
            } else {
                theme = PreferencesManager.THEME_LIGHT;
            }

            ThemeManager.setTheme(requireContext(), theme);
            requireActivity().recreate();
        });

        // Clear data
        cardClearData.setOnClickListener(v -> showClearDataDialog());
    }

    private void showClearDataDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.clear_data)
                .setMessage(R.string.clear_data_confirm)
                .setPositiveButton(R.string.confirm, (dialog, which) -> clearAllData())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void clearAllData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            SkipDatabase database = SkipDatabase.getInstance(requireContext());
            database.skipRecordDao().deleteAll();
            database.appPreferenceDao().deleteAll();

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "All data cleared", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
