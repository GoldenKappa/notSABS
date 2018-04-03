package com.layoutxml.sabs.utils;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.blocker.ContentBlocker20;
import com.layoutxml.sabs.blocker.ContentBlocker56;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.google.gson.Gson;

import java.io.File;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

public class DeviceAdminInteractor {
    private static final int RESULT_ENABLE = 42;
    private static final String TAG = DeviceAdminInteractor.class.getCanonicalName();
    private static DeviceAdminInteractor mInstance = null;

    @Nullable
    @Inject
    EnterpriseLicenseManager enterpriseLicenseManager;

    @Nullable
    @Inject
    EnterpriseDeviceManager enterpriseDeviceManager;

    @Nullable
    @Inject
    DevicePolicyManager devicePolicyManager;

    @Nullable
    @Inject
    ApplicationPolicy mApplicationPolicy;

    @Inject
    Context mContext;

    @Inject
    ComponentName componentName;

    @Inject
    Gson gson;

    @Inject
    OkHttpClient okHttpClient;

    private DeviceAdminInteractor() {
        App.get().getAppComponent().inject(this);
    }


    public static DeviceAdminInteractor getInstance() {
        if (mInstance == null) {
            mInstance = getSync();
        }
        return mInstance;
    }

    private static synchronized DeviceAdminInteractor getSync() {
        if (mInstance == null) {
            mInstance = new DeviceAdminInteractor();
        }
        return mInstance;
    }

    public static boolean isSamsung() {
        Log.i(TAG, "Device manufacturer: " + Build.MANUFACTURER);
        return Build.MANUFACTURER.equals("samsung");
    }

    /**
     * Check if admin enabled
     *
     * @return void
     */
    public boolean isActiveAdmin() {
        return devicePolicyManager.isAdminActive(componentName);
    }

    /**
     * Force user to enable administrator
     */
    public void forceEnableAdmin(Context context) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Policy provider");
        ((Activity) context).startActivityForResult(intent, RESULT_ENABLE);
    }

    /**
     * Force to activate Samsung KNOX Standard SDK
     */
    public void forceActivateKnox(String knoxKey) throws Exception {
        try {
            EnterpriseLicenseManager.getInstance(mContext)
                    .activateLicense(knoxKey);
        } catch (Exception e) {
            Log.e(TAG, "Failed to activate license", e);
            throw new Exception("Failed to activate license");
        }

    }

    /**
     * Check if KNOX enabled
     */
    public boolean isKnoxEnabled() {
        return (mContext.checkCallingOrSelfPermission("android.permission.sec.MDM_FIREWALL")
                == PackageManager.PERMISSION_GRANTED)
                && (mContext.checkCallingOrSelfPermission("android.permission.sec.MDM_APP_MGMT")
                == PackageManager.PERMISSION_GRANTED);
    }

    public String getKnoxKey(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("knox_key", null);
    }

    public boolean installApk(String pathToApk) {
        if (mApplicationPolicy == null) {
            Log.i(TAG, "mApplicationPolicy variable is null");
            return false;
        }
        try {
            File file = new File(pathToApk);
            if (!file.exists()) {
                Log.i(TAG, "apk fail does not exist: " + pathToApk);
                return false;
            }

            boolean result = mApplicationPolicy.installApplication(pathToApk, false);
            Log.i(TAG, "Is Application installed: " + result);
            return result;
        } catch (Throwable e) {
            Log.e(TAG, "Failed to install application", e);
            return false;
        }
    }

    public ContentBlocker getContentBlocker() {
        Log.d(TAG, "Entering contentBlocker() method");
        try {
            switch (enterpriseDeviceManager.getEnterpriseSdkVer()) {
                case ENTERPRISE_SDK_VERSION_NONE:
                    return ContentBlocker57.getInstance();
                case ENTERPRISE_SDK_VERSION_2:
                case ENTERPRISE_SDK_VERSION_2_1:
                case ENTERPRISE_SDK_VERSION_2_2:
                case ENTERPRISE_SDK_VERSION_3:
                case ENTERPRISE_SDK_VERSION_4:
                case ENTERPRISE_SDK_VERSION_4_0_1:
                case ENTERPRISE_SDK_VERSION_4_1:
                case ENTERPRISE_SDK_VERSION_5:
                case ENTERPRISE_SDK_VERSION_5_1:
                case ENTERPRISE_SDK_VERSION_5_2:
                case ENTERPRISE_SDK_VERSION_5_3:
                case ENTERPRISE_SDK_VERSION_5_4:
                    ContentBlocker20.getInstance().setUrlBlockLimit(625);
                    return ContentBlocker20.getInstance();
                case ENTERPRISE_SDK_VERSION_5_4_1:
                case ENTERPRISE_SDK_VERSION_5_5:
                case ENTERPRISE_SDK_VERSION_5_5_1:
                    ContentBlocker20.getInstance().setUrlBlockLimit(625);
                    return ContentBlocker20.getInstance();
                case ENTERPRISE_SDK_VERSION_5_6:
                    return ContentBlocker56.getInstance();
                case ENTERPRISE_SDK_VERSION_5_7:
                    return ContentBlocker57.getInstance();
                case ENTERPRISE_SDK_VERSION_5_7_1:
                    return ContentBlocker57.getInstance();
                case ENTERPRISE_SDK_VERSION_5_8:
                    return ContentBlocker57.getInstance();
                case ENTERPRISE_SDK_VERSION_5_9:
                    return  ContentBlocker57.getInstance();
                default:
                    return ContentBlocker57.getInstance();
            }
        } catch (Throwable t) {
            Log.e(TAG, "Failed to getAll ContentBlocker", t);
            return null;
        }
    }

    public boolean isContentBlockerSupported() {
        return (isSamsung() && isKnoxSupported() && isKnoxVersionSupported());
    }

    private boolean isKnoxVersionSupported() {
        Log.d(TAG, "Entering isKnoxVersionSupported() method");
        if (enterpriseDeviceManager == null) {
            Log.w(TAG, "Knox not supported since enterpriseDeviceManager = null");
            return false;
        }
        switch (enterpriseDeviceManager.getEnterpriseSdkVer()) {
            case ENTERPRISE_SDK_VERSION_NONE:
                return false;
            case ENTERPRISE_SDK_VERSION_2:
            case ENTERPRISE_SDK_VERSION_2_1:
            case ENTERPRISE_SDK_VERSION_2_2:
            case ENTERPRISE_SDK_VERSION_3:
            case ENTERPRISE_SDK_VERSION_4:
            case ENTERPRISE_SDK_VERSION_4_0_1:
            case ENTERPRISE_SDK_VERSION_4_1:
            case ENTERPRISE_SDK_VERSION_5:
            case ENTERPRISE_SDK_VERSION_5_1:
            case ENTERPRISE_SDK_VERSION_5_2:
            case ENTERPRISE_SDK_VERSION_5_3:
            case ENTERPRISE_SDK_VERSION_5_4:
            case ENTERPRISE_SDK_VERSION_5_4_1:
            case ENTERPRISE_SDK_VERSION_5_5:
            case ENTERPRISE_SDK_VERSION_5_5_1:
            case ENTERPRISE_SDK_VERSION_5_6:
            case ENTERPRISE_SDK_VERSION_5_7:
            case ENTERPRISE_SDK_VERSION_5_7_1:
            case ENTERPRISE_SDK_VERSION_5_8:
            case ENTERPRISE_SDK_VERSION_5_9:
                return true;
            default:
                return true;
        }
    }

    public boolean isKnoxSupported() {
        if (enterpriseLicenseManager == null) {
            Log.w(TAG, "Knox is not supported");
            return false;
        }
        Log.i(TAG, "Knox is supported");
        return true;
    }
}
