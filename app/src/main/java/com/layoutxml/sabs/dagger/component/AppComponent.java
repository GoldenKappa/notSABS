package com.layoutxml.sabs.dagger.component;

import com.layoutxml.sabs.adapter.AdhellPermissionInAppsAdapter;
import com.layoutxml.sabs.blocker.ContentBlocker20;
import com.layoutxml.sabs.blocker.ContentBlocker56;
import com.layoutxml.sabs.blocker.fwInterface;
import com.layoutxml.sabs.dagger.module.AdminModule;
import com.layoutxml.sabs.dagger.module.AppModule;
import com.layoutxml.sabs.dagger.module.EnterpriseModule;
import com.layoutxml.sabs.dagger.module.NetworkModule;
import com.layoutxml.sabs.dagger.scope.AdhellApplicationScope;
import com.layoutxml.sabs.fragments.BlockedUrlSettingFragment;
import com.layoutxml.sabs.fragments.BlockerFragment;
import com.layoutxml.sabs.fragments.PackageDisablerFragment;
import com.layoutxml.sabs.service.BlockedDomainService;
import com.layoutxml.sabs.utils.AdhellAppIntegrity;
import com.layoutxml.sabs.utils.AppsListDBInitializer;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;
import com.layoutxml.sabs.viewmodel.AdhellWhitelistAppsViewModel;
import com.layoutxml.sabs.viewmodel.SharedAppPermissionViewModel;

import dagger.Component;

@AdhellApplicationScope
@Component(modules = {AppModule.class, AdminModule.class, EnterpriseModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(DeviceAdminInteractor deviceAdminInteractor);

    void inject(ContentBlocker56 contentBlocker56);

    void inject(fwInterface fwInterface);

    void inject(ContentBlocker20 contentBlocker20);

    void inject(BlockedDomainService blockedDomainService);

    void inject(BlockedUrlSettingFragment blockedUrlSettingFragment);

    void inject(PackageDisablerFragment packageDisablerFragment);

    void inject(AdhellWhitelistAppsViewModel adhellWhitelistAppsViewModel);

    void inject(SharedAppPermissionViewModel sharedAppPermissionViewModel);

    void inject(AdhellPermissionInAppsAdapter adhellPermissionInAppsAdapter);

    void inject(AppsListDBInitializer appsListDBInitializer);

    void inject(BlockerFragment blockerFragment);

    void inject(AdhellAppIntegrity adhellAppIntegrity);

}
