package com.layoutxml.sabs.dagger.module;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

import com.layoutxml.sabs.dagger.scope.AdhellApplicationScope;
import com.layoutxml.sabs.receiver.CustomDeviceAdminReceiver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module(includes = {AppModule.class})
public class AdminModule {
    @Provides
    @AdhellApplicationScope
    DevicePolicyManager providesDevicePolicyManager(Context appContext) {
        return (DevicePolicyManager) appContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Provides
    @AdhellApplicationScope
    ComponentName providesComponentName(Context appContext) {
        return new ComponentName(appContext, CustomDeviceAdminReceiver.class);
    }
}
