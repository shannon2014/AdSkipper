package com.adskipper.ui.adapter;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adskipper.R;
import com.adskipper.data.AppPreference;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private List<AppPreference> apps = new ArrayList<>();
    private PackageManager packageManager;
    private OnAppToggleListener listener;

    public interface OnAppToggleListener {
        void onAppToggled(AppPreference app, boolean isMonitored);
    }

    public AppAdapter(PackageManager packageManager, OnAppToggleListener listener) {
        this.packageManager = packageManager;
        this.listener = listener;
    }

    public void setApps(List<AppPreference> apps) {
        this.apps = apps != null ? apps : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<AppPreference> getApps() {
        return apps;
    }

    public void selectAll(boolean selected) {
        for (AppPreference app : apps) {
            app.setMonitored(selected);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppPreference app = apps.get(position);

        holder.tvAppName.setText(app.getAppName());

        // Load app icon
        try {
            Drawable icon = packageManager.getApplicationIcon(app.getPackageName());
            holder.ivAppIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            holder.ivAppIcon.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        // Format last used time
        long lastUsed = app.getLastUsedTimestamp();
        if (lastUsed > 0) {
            holder.tvAppInfo.setText(formatLastUsed(lastUsed));
        } else {
            holder.tvAppInfo.setText(R.string.not_monitored);
        }

        // Set toggle state without triggering listener
        holder.switchMonitored.setOnCheckedChangeListener(null);
        holder.switchMonitored.setChecked(app.isMonitored());
        holder.switchMonitored.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setMonitored(isChecked);
            if (listener != null) {
                listener.onAppToggled(app, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    private String formatLastUsed(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 60) {
            return "Last used: " + minutes + "m ago";
        } else if (hours < 24) {
            return "Last used: " + hours + "h ago";
        } else if (days == 1) {
            return "Last used: Yesterday";
        } else if (days < 7) {
            return "Last used: " + days + "d ago";
        } else {
            long weeks = days / 7;
            return "Last used: " + weeks + "w ago";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppInfo;
        SwitchMaterial switchMonitored;

        ViewHolder(View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.iv_app_icon);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            tvAppInfo = itemView.findViewById(R.id.tv_app_info);
            switchMonitored = itemView.findViewById(R.id.switch_monitored);
        }
    }
}
