package com.layoutxml.sabs.utils;

import android.app.enterprise.ApplicationPolicy;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class AppsListDBInitializer {
    private static final AppsListDBInitializer instance = new AppsListDBInitializer();
    @Nullable
    @Inject
    ApplicationPolicy appPolicy;
    @Inject
    AppDatabase appDatabase;

    private AppsListDBInitializer() {
        App.get().getAppComponent().inject(this);
    }


    public static AppsListDBInitializer getInstance() {
        return instance;
    }

    public void fillPackageDb(PackageManager packageManager) {
        List<ApplicationInfo> applicationsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<AppInfo> appsInfo = new ArrayList<>();
        long id = 0;
        for (ApplicationInfo applicationInfo : applicationsInfo) {
            String pckg = App.get().getApplicationContext().getPackageName();
            if (applicationInfo.packageName.equals(pckg)) continue;
            AppInfo appInfo = new AppInfo();
            appInfo.id = id++;
            appInfo.appName = packageManager.getApplicationLabel(applicationInfo).toString();
            appInfo.packageName = applicationInfo.packageName;
            appInfo.disabled = !appPolicy.getApplicationStateEnabled(appInfo.packageName);
            int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            appInfo.system = (applicationInfo.flags & mask) != 0;
            try {
                appInfo.installTime = packageManager.getPackageInfo(applicationInfo.packageName, 0).firstInstallTime;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                appInfo.installTime = 0;
            }
            appsInfo.add(appInfo);
        }
        appDatabase.applicationInfoDao().insertAll(appsInfo);
    }

    public AppInfo generateAppInfo(PackageManager packageManager, String packageName) {
        AppInfo appInfo = new AppInfo();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            appInfo.id = appDatabase.applicationInfoDao().getMaxId() + 1;
            appInfo.packageName = packageName;
            appInfo.appName = packageManager.getApplicationLabel(applicationInfo).toString();
            int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            appInfo.system = (applicationInfo.flags & mask) != 0;
            appInfo.installTime = packageManager.getPackageInfo(packageName, 0).firstInstallTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }
}
