package com.layoutxml.sabs.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

import java.io.File;

public class AdhellDownloadBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AdhellDownloadBroadcastReceiver.class.getCanonicalName();
    private Context mContext;

    public AdhellDownloadBroadcastReceiver() {
        super();
        mContext = App.get().getApplicationContext();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive download completed");
        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        SharedPreferences sharedPref =
                App.get().getApplicationContext()
                        .getSharedPreferences(context.getString(R.string.download_manager_sharedPrefs),
                                Context.MODE_PRIVATE);
        long savedReferenceId = sharedPref.getLong(context.getString(R.string.download_manager_reference_id), -2);
        if (referenceId == savedReferenceId) {
            DeviceAdminInteractor deviceAdminInteractor = DeviceAdminInteractor.getInstance();
            if (deviceAdminInteractor.isKnoxEnabled()) {
                Log.i(TAG, "Knox enabled");
                File fileDir = context.getExternalFilesDir(null);
                if (fileDir == null || !fileDir.exists()) {
                    return;
                }
                String downloadDir = fileDir.toString();
                Log.i(TAG, "getAll dit: " + downloadDir);
                String apkFilePath = downloadDir + "/adhell.apk";
                File apkFile = new File(apkFilePath);

                if (!apkFile.exists()) {
                    Log.w(TAG, ".apk file does not exist");
                    return;
                }

                final PackageManager pm = mContext.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(apkFilePath, 0);
                Toast.makeText(mContext, "VersionCode : " + info.versionCode + ", VersionName : " + info.versionName, Toast.LENGTH_LONG).show();
                if (info.versionCode == BuildConfig.VERSION_CODE
                        && info.versionName.equals(BuildConfig.VERSION_NAME)) {
                    Log.w(TAG, "Same version .apk. Aborted");
                    return;
                }

                boolean isInstalled = deviceAdminInteractor.installApk(apkFilePath);
                Log.i(TAG, "Path to: " + apkFilePath);
                if (isInstalled) {
                    Toast.makeText(context, "Adhell app updated!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to update Adhell.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.w(TAG, "Knox is disabled");
            }
        }
    }
}
