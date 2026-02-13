package com.adskipper.ui.adapter;

import android.content.Context;
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
    private Context context;

    public void setRecords(List<SkipRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_skip_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SkipRecord record = records.get(position);

        holder.tvAppName.setText(record.getAppName());

        String time = timeFormat.format(new Date(record.getTimestamp()));
        
        String adTypeKey = record.getAdType();
        String adTypeDisplay;
        if ("startup".equals(adTypeKey)) {
            adTypeDisplay = context.getString(R.string.startup_ad);
        } else if ("interstitial".equals(adTypeKey)) {
            adTypeDisplay = context.getString(R.string.interstitial_ad);
        } else if ("video".equals(adTypeKey)) {
            adTypeDisplay = context.getString(R.string.video_ad);
        } else if ("bumper".equals(adTypeKey)) {
             adTypeDisplay = context.getString(R.string.bumper_ad);
        } else {
            // Fallback to whatever is stored if it doesn't match keys
            adTypeDisplay = adTypeKey;
        }
        
        holder.tvSkipInfo.setText(time + " • " + adTypeDisplay);

        holder.tvTimeSavedBadge.setText(context.getString(R.string.saved_time, record.getTimeSavedSeconds()));
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
