package com.layoutxml.sabs.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AppWhitelistAdapter extends ArrayAdapter<AppInfo> {
    private static final String TAG = AppWhitelistAdapter.class.getCanonicalName();
    private List<AppInfo> appInfos;
    private List<AppInfo> mAppInfoOriginal;
    private PackageManager mPackageManager;
    // field variable
    private Picasso mPicasso;

    public AppWhitelistAdapter(@NonNull Context context, @NonNull List<AppInfo> appInfos) {
        super(context, 0, appInfos);
        mPackageManager = getContext().getPackageManager();
        this.appInfos = appInfos;
        mAppInfoOriginal = appInfos;
        // in constructor
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.addRequestHandler(new AppIconRequestHandler(context));
        mPicasso = builder.build();
    }

    @Override
    public int getCount() {
        if (appInfos == null) {
            return 0;
        }
        return appInfos.size();
    }

    @Nullable
    @Override
    public AppInfo getItem(int position) {
        return appInfos.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_app_list_view, parent, false);
        }
        AppInfo appInfo = getItem(position);
        if (appInfo != null) {
            ImageView appIconImageView = convertView.findViewById(R.id.appIconImageView);
            TextView appNameTextView = convertView.findViewById(R.id.appNameTextView);
            Switch adhellWhitelistAppSwitch = convertView.findViewById(R.id.adhellWhitelistAppSwitch);
            appNameTextView.setText(appInfo.appName);
            mPicasso.load(AppIconRequestHandler.getUri(appInfo.packageName)).into(appIconImageView);
            adhellWhitelistAppSwitch.setChecked(!appInfo.adhellWhitelisted);
        }
        return convertView;
    }

    static class AppIconRequestHandler extends RequestHandler {
        /** Uri scheme for app icons */
        public static final String SCHEME_APP_ICON = "app-icon";
        private PackageManager mPackageManager;
        public AppIconRequestHandler(Context context) {
            mPackageManager = context.getPackageManager();
        }
        /**
         * Create an Uri that can be handled by this RequestHandler based on the package name
         */
        public static Uri getUri(String packageName) {
            return Uri.fromParts(SCHEME_APP_ICON, packageName, null);
        }
        @Override
        public boolean canHandleRequest(Request data) {
            // only handle Uris matching our scheme
            return (SCHEME_APP_ICON.equals(data.uri.getScheme()));
        }
        @Override
        public Result load(Request request, int networkPolicy) throws IOException {
            String packageName = request.uri.getSchemeSpecificPart();
            Drawable drawable;
            try {
                drawable = mPackageManager.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException ignored) {
                return null;
            }
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return new Result(bitmap, Picasso.LoadedFrom.DISK);
        }
    }


    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    appInfos = (List<AppInfo>) results.values;
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d(TAG, "Performing filtering");
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    ArrayList<AppInfo> list = new ArrayList<>(mAppInfoOriginal);
                    results.values = list;
                    results.count = list.size();
                } else {
                    List<AppInfo> filteredAppInfos = new ArrayList<>();
                    for (AppInfo info : mAppInfoOriginal) {
                        if (info.packageName.contains(constraint.toString().toLowerCase())
                                || info.appName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredAppInfos.add(info);
                            Log.d(TAG, "appInfo: " + info.appName + " " + info.packageName);
                        }
                    }
                    Log.d(TAG, "Number of filtered apps: " + filteredAppInfos.size() + " filter: " + constraint);
                    results.values = filteredAppInfos;
                    results.count = filteredAppInfos.size();
                }
                return results;
            }
        };
    }
}
