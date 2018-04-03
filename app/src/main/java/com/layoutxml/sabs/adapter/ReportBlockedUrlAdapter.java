package com.layoutxml.sabs.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.entity.ReportBlockedUrl;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.layoutxml.sabs.Global.domainsToExport;

public class ReportBlockedUrlAdapter extends ArrayAdapter<ReportBlockedUrl> {
    private static final String TAG = ReportBlockedUrlAdapter.class.getCanonicalName();
    private PackageManager packageManager;

    public ReportBlockedUrlAdapter(@NonNull Context context, @NonNull List<ReportBlockedUrl> objects) {
        super(context, 0, objects);
        packageManager = getContext().getPackageManager();

        for (ReportBlockedUrl item : objects)
        {
            domainsToExport.add(item.url);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_blocked_url_info, parent, false);
        }
        ReportBlockedUrl reportBlockedUrl = getItem(position);
        if (reportBlockedUrl == null) {
            return convertView;
        }

        ImageView blockedDomainIconImageView = convertView.findViewById(R.id.blockedDomainIconImageView);
        TextView blockedDomainAppNameTextView = convertView.findViewById(R.id.blockedDomainAppNameTextView);
        TextView blockedDomainTimeTextView = convertView.findViewById(R.id.blockedDomainTimeTextView);
        TextView blockedDomainUrlTextView = convertView.findViewById(R.id.blockedDomainUrlTextView);


        String packageName = reportBlockedUrl.packageName;
        Drawable icon = null;
        try {
            icon = packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to getAll application icon.", e);
        }


        ApplicationInfo ai;
        try {
            ai = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        String applicationName = (String) (ai != null ? packageManager.getApplicationLabel(ai) : "(unknown)");


        if (icon != null) {
            blockedDomainIconImageView.setImageDrawable(icon);
        }
        blockedDomainAppNameTextView.setText(position+1 + ". " + applicationName);
        blockedDomainTimeTextView.setText(new SimpleDateFormat("HH:mm:ss").format(reportBlockedUrl.blockDate));
        blockedDomainUrlTextView.setText(reportBlockedUrl.url);

        return convertView;
    }
}
