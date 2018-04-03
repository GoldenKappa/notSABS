package com.layoutxml.sabs.dagger.module;

import android.app.enterprise.ApplicationPermissionControlPolicy;
import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.FirewallPolicy;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.layoutxml.sabs.dagger.scope.AdhellApplicationScope;
import com.sec.enterprise.firewall.Firewall;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.app.enterprise.EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE;

@Singleton
@Module(includes = {AppModule.class})
public class EnterpriseModule {
    private static final String TAG = EnterpriseModule.class.getCanonicalName();

    @Nullable
    @Provides
    @AdhellApplicationScope
    EnterpriseLicenseManager providesEnterpriseLicenseManager(Context appContext) {
        try {
            Log.i(TAG, "Trying to get EnterpriseLicenseManager");
            return EnterpriseLicenseManager.getInstance(appContext);
        } catch (Throwable e) {
            Log.e(TAG, "Failed to get EnterpriseLicenseManager. So it seems that Knox is not supported on this device", e);
        }
        return null;
    }

    @Nullable
    @Provides
    @AdhellApplicationScope
    EnterpriseDeviceManager providesEnterpriseDeviceManager(Context appContext) {
        try {
            Log.i(TAG, "Trying to get EnterpriseDeviceManager");
            return (EnterpriseDeviceManager) appContext.getSystemService(ENTERPRISE_POLICY_SERVICE);
        } catch (Throwable e) {
            Log.w(TAG, "Failed to get EnterpriseDeviceManager", e);
            return null;
        }
    }

    @Nullable
    @Provides
    @AdhellApplicationScope
    ApplicationPolicy providesApplicationPolicy(@Nullable EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            return null;
        }
        return enterpriseDeviceManager.getApplicationPolicy();
    }

    @Nullable
    @Provides
    @AdhellApplicationScope
    ApplicationPermissionControlPolicy providesApplicationPermissionControlPolicy(@Nullable EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            Log.w(TAG, "enterpriseDeviceManager is null. Can't get ApplicationPermissionControlPolicy");
            return null;
        }
        return enterpriseDeviceManager.getApplicationPermissionControlPolicy();
    }

    @Nullable
    @Provides
    @AdhellApplicationScope
    FirewallPolicy providesFirewallPolicy(@Nullable EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            Log.w(TAG, "enterpriseDeviceManager is null. Can't get FirewallPolicy");
            return null;
        }
        try {
            Log.i(TAG, "Trying to get FirewallPolicy");
            return enterpriseDeviceManager.getFirewallPolicy();
        } catch (Throwable e) {
            Log.e(TAG, "Failed to get firewallPolicy", e);
        }
        return null;
    }

    @Nullable
    @Provides
    @AdhellApplicationScope
    Firewall providesFirewall(@Nullable EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            Log.w(TAG, "enterpriseDeviceManager is null. Can't get firewall");
            return null;
        }
        try {
            Log.i(TAG, "Trying to get Firewall");
            return enterpriseDeviceManager.getFirewall();
        } catch (Throwable throwable) {
            Log.e(TAG, "Failed to get firewall", throwable);
        }
        return null;
    }

}
