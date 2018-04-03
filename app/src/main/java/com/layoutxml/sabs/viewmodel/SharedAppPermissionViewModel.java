package com.layoutxml.sabs.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.layoutxml.sabs.model.AdhellPermissionInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SharedAppPermissionViewModel extends ViewModel {
    private static final String TAG = SharedAppPermissionViewModel.class.getCanonicalName();
    private final MutableLiveData<AdhellPermissionInfo> selected = new MutableLiveData<AdhellPermissionInfo>();
    public List<AppInfo> installedApps;
    private LiveData<List<AppInfo>> installedAppsLiveData;

    @Inject
    AppDatabase appDatabase;

    @Inject
    PackageManager packageManager;

    public SharedAppPermissionViewModel() {
        App.get().getAppComponent().inject(this);
    }

    public LiveData<List<AppInfo>> loadInstalledAppsLiveData() {
        if (installedAppsLiveData == null) {
            installedAppsLiveData = appDatabase.applicationInfoDao().getAllNonSystemLiveData();
        }
        return installedAppsLiveData;
    }

    public List<AppInfo> loadPermissionsApps(List<AppInfo> appInfos, String permissionName) {
        List<AppInfo> permssionsApps = new ArrayList<>();
        if (appInfos == null || appInfos.size() == 0) {
            Log.w(TAG, "appInfos is empty");
            return permssionsApps;
        }
        Log.i(TAG, "Number of installed apps " + appInfos.size());
        Log.i(TAG, "Permission name: " + permissionName);

        for (AppInfo appInfo : appInfos) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
                if (packageInfo == null) {
                    Log.w(TAG, "Package info with package name " + appInfo.packageName + " is null");
                    continue;
                }
                if (packageInfo.requestedPermissions == null || packageInfo.requestedPermissions.length == 0) {
                    Log.w(TAG, "Package info requestedPermissions with package name " + appInfo.packageName + " is null or 0");
                    continue;
                }
                for (String appPermissionName : packageInfo.requestedPermissions) {
                    if (appPermissionName.equals(permissionName)) {
                        permssionsApps.add(appInfo);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to get packageInfo", e);
            }
        }
        Log.i(TAG, "permssionsApps number size is: " + permssionsApps.size());
        return permssionsApps;

    }

    public void select(AdhellPermissionInfo adhellPermissionInfo) {
        selected.setValue(adhellPermissionInfo);
    }

    public LiveData<AdhellPermissionInfo> getSelected() {
        return selected;
    }

}
