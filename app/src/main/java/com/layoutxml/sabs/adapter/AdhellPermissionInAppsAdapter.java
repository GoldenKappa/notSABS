package com.layoutxml.sabs.adapter;

import android.app.enterprise.AppPermissionControlInfo;
import android.app.enterprise.ApplicationPermissionControlPolicy;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class AdhellPermissionInAppsAdapter extends RecyclerView.Adapter<AdhellPermissionInAppsAdapter.ViewHolder> {
    private static final String TAG = AdhellPermissionInAppsAdapter.class.getCanonicalName();
    public String currentPermissionName;
    @Inject
    PackageManager mPackageManager;

    @Nullable
    @Inject
    ApplicationPermissionControlPolicy mAppControlPolicy;
    private Set<String> restrictedPackageNames;
    private List<AppInfo> appInfos;


    public AdhellPermissionInAppsAdapter(Context context, List<AppInfo> packageInfos) {
        App.get().getAppComponent().inject(this);
        this.appInfos = packageInfos;
        updateRestrictedPackages();
    }

    public void updateRestrictedPackages() {
        assert mAppControlPolicy != null;
        List<AppPermissionControlInfo> appPermissionControlInfos = mAppControlPolicy.getPackagesFromPermissionBlackList();
        if (appPermissionControlInfos == null
                || appPermissionControlInfos.size() == 0
                ) {
            restrictedPackageNames = null;
            return;
        }
        Log.w(TAG, appPermissionControlInfos.toString());
        Log.w(TAG, "Size of appPermissionControlInfos: " + appPermissionControlInfos.size());
        for (AppPermissionControlInfo appPermissionControlInfo : appPermissionControlInfos) {
            if (appPermissionControlInfo == null) {
                continue;
            }
            if (appPermissionControlInfo.mapEntries == null || appPermissionControlInfo.mapEntries.size() == 0) {
                continue;
            }
            Log.w(TAG, appPermissionControlInfo.mapEntries.toString());
            restrictedPackageNames = appPermissionControlInfo.mapEntries.get(currentPermissionName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View permissionInApp = inflater.inflate(R.layout.item_permission_in_app, parent, false);
        return new AdhellPermissionInAppsAdapter.ViewHolder(permissionInApp);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo appInfo = appInfos.get(position);
        holder.appNameTextView.setText(appInfo.appName);
        Drawable icon = null;
        try {
            icon = mPackageManager.getApplicationIcon(appInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Application icon not found");
        }
        holder.appIconImageView.setImageDrawable(icon);
        holder.appPermissionSwitch.setChecked(true);
        if (restrictedPackageNames == null) {
            return;
        }
        if (!restrictedPackageNames.contains(appInfo.packageName)) {
            return;
        }

        holder.appPermissionSwitch.setChecked(false);
    }

    @Override
    public int getItemCount() {
        if (appInfos == null) {
            return 0;
        }
        return appInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView appIconImageView;
        TextView appNameTextView;
        Switch appPermissionSwitch;

        ViewHolder(View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            appPermissionSwitch = itemView.findViewById(R.id.appPermissionSwitch);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d(TAG, "Position clicked: " + position);
            AppInfo appInfo = appInfos.get(position);
            Log.d(TAG, appInfo.packageName);
            List<String> list = new ArrayList<>();
            list.add(appInfo.packageName);
            if (restrictedPackageNames != null && restrictedPackageNames.contains(appInfo.packageName)) {
                boolean isBlacklisted = mAppControlPolicy.removePackagesFromPermissionBlackList(currentPermissionName, list);
                Log.d(TAG, "Is removed: " + isBlacklisted);
                if (isBlacklisted) {
                    appPermissionSwitch.setChecked(true);
                }
            } else {
                boolean success = mAppControlPolicy.addPackagesToPermissionBlackList(currentPermissionName, list);
                Log.d(TAG, "Is added to blacklist: " + success);
                if (success) {
                    appPermissionSwitch.setChecked(false);
                }
            }
            updateRestrictedPackages();
        }
    }
}
