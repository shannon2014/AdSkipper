package com.adskipper.ui.fragment;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private PreferencesManager prefsManager;

    private List<RadioButton> themeRadioButtons;
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
        themeRadioButtons = new ArrayList<>();
        themeRadioButtons.add(view.findViewById(R.id.radio_theme_default));
        themeRadioButtons.add(view.findViewById(R.id.radio_theme_dark));
        themeRadioButtons.add(view.findViewById(R.id.radio_theme_light));
        themeRadioButtons.add(view.findViewById(R.id.radio_theme_red));
        themeRadioButtons.add(view.findViewById(R.id.radio_theme_green));
        themeRadioButtons.add(view.findViewById(R.id.radio_theme_orange));

        cardClearData = view.findViewById(R.id.card_clear_data);
        tvVersion = view.findViewById(R.id.tv_version);
    }

    private void loadSettings() {
        // Load theme
        String theme = prefsManager.getTheme();
        int checkId;
        switch (theme) {
            case PreferencesManager.THEME_DARK:
                checkId = R.id.radio_theme_dark;
                break;
            case PreferencesManager.THEME_LIGHT:
                checkId = R.id.radio_theme_light;
                break;
            case PreferencesManager.THEME_RED:
                checkId = R.id.radio_theme_red;
                break;
            case PreferencesManager.THEME_GREEN:
                checkId = R.id.radio_theme_green;
                break;
            case PreferencesManager.THEME_ORANGE:
                checkId = R.id.radio_theme_orange;
                break;
            case PreferencesManager.THEME_DEFAULT:
            default:
                checkId = R.id.radio_theme_default;
                break;
        }

        for (RadioButton btn : themeRadioButtons) {
            btn.setChecked(btn.getId() == checkId);
        }

        // Load version
        try {
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            String version = pInfo.versionName;
            if (tvVersion != null) {
                tvVersion.setText(getString(R.string.version, version));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        // Theme selection - Manual implementation since we are not using RadioGroup
        View.OnClickListener themeClickListener = v -> {
            RadioButton clickedBtn = (RadioButton) v;
            
            // Uncheck all others
            for (RadioButton btn : themeRadioButtons) {
                if (btn != clickedBtn) {
                    btn.setChecked(false);
                }
            }
            // Ensure clicked is checked
            clickedBtn.setChecked(true);

            // Apply theme
            int checkedId = clickedBtn.getId();
            String theme;
            if (checkedId == R.id.radio_theme_default) {
                theme = PreferencesManager.THEME_DEFAULT;
            } else if (checkedId == R.id.radio_theme_dark) {
                theme = PreferencesManager.THEME_DARK;
            } else if (checkedId == R.id.radio_theme_light) {
                theme = PreferencesManager.THEME_LIGHT;
            } else if (checkedId == R.id.radio_theme_red) {
                theme = PreferencesManager.THEME_RED;
            } else if (checkedId == R.id.radio_theme_green) {
                theme = PreferencesManager.THEME_GREEN;
            } else if (checkedId == R.id.radio_theme_orange) {
                theme = PreferencesManager.THEME_ORANGE;
            } else {
                theme = PreferencesManager.THEME_DEFAULT;
            }

            ThemeManager.setTheme(requireContext(), theme);
            requireActivity().recreate();
        };

        for (RadioButton btn : themeRadioButtons) {
            btn.setOnClickListener(themeClickListener);
        }

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
                Toast.makeText(requireContext(), R.string.data_cleared, Toast.LENGTH_SHORT).show();
            });
        });
    }
}
