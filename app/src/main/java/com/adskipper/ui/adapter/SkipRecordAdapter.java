package com.adskipper.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adskipper.R;
import com.adskipper.data.SkipRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SkipRecordAdapter extends RecyclerView.Adapter<SkipRecordAdapter.ViewHolder> {

    private List<SkipRecord> records = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public void setRecords(List<SkipRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_skip_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SkipRecord record = records.get(position);

        holder.tvAppName.setText(record.getAppName());

        String time = timeFormat.format(new Date(record.getTimestamp()));
        String adType = record.getAdType();
        holder.tvSkipInfo.setText(time + " • " + adType);

        holder.tvTimeSavedBadge.setText("Saved " + record.getTimeSavedSeconds() + "s");
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvSkipInfo;
        TextView tvTimeSavedBadge;

        ViewHolder(View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.iv_app_icon);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            tvSkipInfo = itemView.findViewById(R.id.tv_skip_info);
            tvTimeSavedBadge = itemView.findViewById(R.id.tv_time_saved_badge);
        }
    }
}
