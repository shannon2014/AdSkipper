package com.adskipper.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adskipper.R;
import com.adskipper.ui.adapter.SkipRecordAdapter;
import com.adskipper.ui.viewmodel.HistoryViewModel;

public class HistoryFragment extends Fragment {

    private HistoryViewModel viewModel;
    private SkipRecordAdapter adapter;

    private TextView tvTotalTime;
    private TextView tvAcrossApps;
    private TextView tvTodayCount;
    private RecyclerView recyclerHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeData();
    }

    private void initViews(View view) {
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        tvAcrossApps = view.findViewById(R.id.tv_across_apps);
        tvTodayCount = view.findViewById(R.id.tv_today_count);
        recyclerHistory = view.findViewById(R.id.recycler_history);
    }

    private void setupRecyclerView() {
        adapter = new SkipRecordAdapter();
        recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerHistory.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getTotalTimeSaved().observe(getViewLifecycleOwner(), seconds -> {
            if (seconds != null) {
                tvTotalTime.setText(formatTime(seconds));
            }
        });

        viewModel.getTodaySkipCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvTodayCount.setText(getString(R.string.skips_count, count));
            }
        });

        viewModel.getAllRecords().observe(getViewLifecycleOwner(), records -> {
            if (records != null) {
                adapter.setRecords(records);
            }
        });
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
}
