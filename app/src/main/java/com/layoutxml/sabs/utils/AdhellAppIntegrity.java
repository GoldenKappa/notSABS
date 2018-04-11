package com.layoutxml.sabs.utils;

import android.app.enterprise.AppPermissionControlInfo;
import android.app.enterprise.ApplicationPermissionControlPolicy;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.layoutxml.sabs.db.entity.AppPermission;
import com.layoutxml.sabs.db.entity.BlockUrl;
import com.layoutxml.sabs.db.entity.BlockUrlProvider;
import com.layoutxml.sabs.db.entity.DisabledPackage;
import com.layoutxml.sabs.db.entity.FirewallWhitelistedPackage;
import com.layoutxml.sabs.db.entity.PolicyPackage;
import com.sec.enterprise.AppIdentity;
import com.sec.enterprise.firewall.DomainFilterRule;
import com.sec.enterprise.firewall.Firewall;
import com.sec.enterprise.firewall.FirewallResponse;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class AdhellAppIntegrity {
    private final static String DEFAULT_POLICY_ID = "default-policy";
    private static final String TAG = AdhellAppIntegrity.class.getCanonicalName();
    private static final String DEFAULT_POLICY_CHECKED = "adhell_default_policy_created";
    private static final String DISABLED_PACKAGES_MOVED = "adhell_disabled_packages_moved";
    private static final String FIREWALL_WHITELISTED_PACKAGES_MOVED = "adhell_firewall_whitelisted_packages_moved";
    private static final String MOVE_APP_PERMISSIONS = "adhell_app_permissions_moved";
    private static final String DEFAULT_PACKAGES_FIREWALL_WHITELISTED = "default_packages_firewall_whitelisted";
    private static final String CHECK_ADHELL_STANDARD_PACKAGE = "sabs_standard_package";
    private static final String CHECK_PACKAGE_DB = "adhell_packages_filled_db";

    // Create a list of our mandatory packages
    private List<String> sabsstandardPackages = new ArrayList<>(Arrays.asList(MainActivity.PACKAGE, MainActivity.SABS_MMOTTI_PACKAGE));

    @Nullable
    @Inject
    Firewall firewall;

    @Inject
    AppDatabase appDatabase;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    PackageManager packageManager;

    @Nullable
    @Inject
    ApplicationPermissionControlPolicy applicationPermissionControlPolicy;

    public AdhellAppIntegrity() {
        App.get().getAppComponent().inject(this);
    }

    public void check() {
        boolean defaultPolicyChecked = sharedPreferences.getBoolean(DEFAULT_POLICY_CHECKED, false);
        if (!defaultPolicyChecked) {
            checkDefaultPolicyExists();
            sharedPreferences.edit().putBoolean(DEFAULT_POLICY_CHECKED, true).apply();
        }
        boolean disabledPackagesMoved = sharedPreferences.getBoolean(DISABLED_PACKAGES_MOVED, false);
        if (!disabledPackagesMoved) {
            copyDataFromAppInfoToDisabledPackage();
            sharedPreferences.edit().putBoolean(DISABLED_PACKAGES_MOVED, true).apply();
        }
        boolean firewallWhitelistedPackagesMoved
                = sharedPreferences.getBoolean(FIREWALL_WHITELISTED_PACKAGES_MOVED, false);
        if (!firewallWhitelistedPackagesMoved) {
            copyDataFromAppInfoToFirewallWhitelistedPackage();
            sharedPreferences.edit().putBoolean(FIREWALL_WHITELISTED_PACKAGES_MOVED, true).apply();
        }
        boolean appPermissionsMoved = sharedPreferences.getBoolean(MOVE_APP_PERMISSIONS, false);
        if (!appPermissionsMoved) {
            moveAppPermissionsToAppPermissionTable();
            sharedPreferences.edit().putBoolean(MOVE_APP_PERMISSIONS, true).apply();
        }
        boolean defaultPackagesFirewallWhitelisted
                = sharedPreferences.getBoolean(DEFAULT_PACKAGES_FIREWALL_WHITELISTED, false);
        if (!defaultPackagesFirewallWhitelisted) {
            addDefaultAdblockWhitelist();
            sharedPreferences.edit().putBoolean(DEFAULT_PACKAGES_FIREWALL_WHITELISTED, true).apply();
        }
        //boolean adhellStandardPackageChecked = sharedPreferences.getBoolean(CHECK_ADHELL_STANDARD_PACKAGE, false);
        //if (!adhellStandardPackageChecked) {
            checkAdhellStandardPackage();
            //sharedPreferences.edit().putBoolean(CHECK_ADHELL_STANDARD_PACKAGE, false).apply();
        //}
        boolean packageDbFilled = sharedPreferences.getBoolean(CHECK_PACKAGE_DB, false);
        if (!packageDbFilled) {
            fillPackageDb();
            sharedPreferences.edit().putBoolean(CHECK_PACKAGE_DB, true).apply();
        }
    }

    public void checkDefaultPolicyExists() {
        PolicyPackage policyPackage = appDatabase.policyPackageDao().getPolicyById(DEFAULT_POLICY_ID);
        if (policyPackage != null) {
            Log.d(TAG, "Default PolicyPackage exists");
            return;
        }
        Log.d(TAG, "Default PolicyPackage does not exist. Creating default policy.");
        policyPackage = new PolicyPackage();
        policyPackage.id = DEFAULT_POLICY_ID;
        policyPackage.name = "Default Policy";
        policyPackage.description = "Automatically generated policy from current Adhell app settings";
        policyPackage.active = true;
        policyPackage.createdAt = policyPackage.updatedAt = new Date();
        appDatabase.policyPackageDao().insert(policyPackage);
        Log.d(TAG, "Default PolicyPackage has been added");
    }

    private void copyDataFromAppInfoToDisabledPackage() {
        List<DisabledPackage> disabledPackages = appDatabase.disabledPackageDao().getAll();
        if (disabledPackages.size() > 0) {
            Log.d(TAG, "DisabledPackages is not empty. No need to move data from AppInfo table");
            return;
        }
        List<AppInfo> disabledApps = appDatabase.applicationInfoDao().getDisabledApps();
        if (disabledApps.size() == 0) {
            Log.d(TAG, "No disabledgetDisabledApps apps in AppInfo table");
            return;
        }
        Log.d(TAG, "There is " + disabledApps.size() + " to move to DisabledPackage table");
        disabledPackages = new ArrayList<>();
        for (AppInfo appInfo : disabledApps) {
            DisabledPackage disabledPackage = new DisabledPackage();
            disabledPackage.packageName = appInfo.packageName;
            disabledPackage.policyPackageId = DEFAULT_POLICY_ID;
            disabledPackages.add(disabledPackage);
        }
        appDatabase.disabledPackageDao().insertAll(disabledPackages);
    }

    private void copyDataFromAppInfoToFirewallWhitelistedPackage() {
        List<FirewallWhitelistedPackage> firewallWhitelistedPackages
                = appDatabase.firewallWhitelistedPackageDao().getAll();
        if (firewallWhitelistedPackages.size() > 0) {
            Log.d(TAG, "FirewallWhitelist package size is: " + firewallWhitelistedPackages.size() + ". No need to move data");
            return;
        }
        List<AppInfo> whitelistedApps = appDatabase.applicationInfoDao().getWhitelistedApps();
        if (whitelistedApps.size() == 0) {
            Log.d(TAG, "No whitelisted apps in AppInfo table");
            return;
        }
        Log.d(TAG, "There is " + whitelistedApps.size() + " to move");
        firewallWhitelistedPackages = new ArrayList<>();
        for (AppInfo appInfo : whitelistedApps) {
            FirewallWhitelistedPackage whitelistedPackage = new FirewallWhitelistedPackage();
            whitelistedPackage.packageName = appInfo.packageName;
            whitelistedPackage.policyPackageId = DEFAULT_POLICY_ID;
            firewallWhitelistedPackages.add(whitelistedPackage);
        }
        appDatabase.firewallWhitelistedPackageDao().insertAll(firewallWhitelistedPackages);
    }

    private void moveAppPermissionsToAppPermissionTable() {
        if (applicationPermissionControlPolicy == null) {
            Log.w(TAG, "applicationPermissionControlPolicy is null");
            return;
        }
        List<AppPermission> appPermissions = appDatabase.appPermissionDao().getAll();
        if (appPermissions.size() > 0) {
            Log.d(TAG, "AppPermission size is " + appPermissions.size() + ". No need to move data");
        }

        List<AppPermissionControlInfo> appPermissionControlInfos
                = applicationPermissionControlPolicy.getPackagesFromPermissionBlackList();
        if (appPermissionControlInfos == null || appPermissionControlInfos.size() == 0) {
            Log.d(TAG, "No blacklisted packages in applicationPermissionControlPolicy");
            return;
        }
        appPermissions = new ArrayList<>();
        AppPermissionControlInfo appPermissionControlInfo = appPermissionControlInfos.get(0);
        for (String permissionName : appPermissionControlInfo.mapEntries.keySet()) {
            Set<String> packageNames = appPermissionControlInfo.mapEntries.get(permissionName);
            if (packageNames == null) {
                continue;
            }
            for (String packageName : packageNames) {
                AppPermission appPermission = new AppPermission();
                appPermission.packageName = packageName;
                appPermission.permissionName = permissionName;
                appPermission.permissionStatus = AppPermission.STATUS_DISALLOW;
                appPermissions.add(appPermission);
            }
        }
        appDatabase.appPermissionDao().insertAll(appPermissions);
    }

    private void addDefaultAdblockWhitelist() {
        List<String> firewallWhitelistedPackages = new ArrayList<>(Arrays.asList("com.google.android.music", "com.google.android.apps.fireball", "com.google.android.apps.tachyon"));
        //Google Music, Google Allo, Google Duo
        for (String app : firewallWhitelistedPackages) {
            List<DomainFilterRule> rules = new ArrayList<>();
            List<String> superAllow = new ArrayList<>();
            superAllow.add("*");
            rules.add(new DomainFilterRule(new AppIdentity(app, null), new ArrayList<>(), superAllow));
            try {
                if (firewall!=null && firewall.isFirewallEnabled()) {
                    FirewallResponse[] response = null;
                    response = firewall.addDomainFilterRules(rules);
                    if (response[0].getResult() == FirewallResponse.Result.SUCCESS) {
                        Log.d(TAG, "Firewall app rules whitelist updated successfully");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to remove filter rule");
            }
        }
    }

    private void constructStandardPackages(String sabspackage, Boolean selected)
    {
        BlockUrlProvider blockUrlProvider = appDatabase.blockUrlProviderDao().getByUrl(sabspackage);

        if (blockUrlProvider == null) {
            blockUrlProvider = new BlockUrlProvider();
            blockUrlProvider.url = sabspackage;
            blockUrlProvider.lastUpdated = new Date();
            blockUrlProvider.deletable = true;
            blockUrlProvider.selected = selected;
            blockUrlProvider.policyPackageId = DEFAULT_POLICY_ID;
            blockUrlProvider.id = appDatabase.blockUrlProviderDao().insertAll(blockUrlProvider)[0];

            // Only fetch the domains if the package is selected
            if(selected) {
                List<BlockUrl> blockUrls;
                try {
                    blockUrls = BlockUrlUtils.loadBlockUrls(blockUrlProvider);
                    blockUrlProvider.count = blockUrls.size();
                    Log.d(TAG, "Number of urls to insert: " + blockUrlProvider.count);
                    appDatabase.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider);
                    appDatabase.blockUrlDao().insertAll(blockUrls);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to download urls", e);
                }
            }

        }
    }

    public void checkAdhellStandardPackage() {

        /* Called by Main Activity */

        // Get all packages
        List<BlockUrlProvider> allProviders = appDatabase.blockUrlProviderDao().getAll2();
        // Get all standard packages
        List<BlockUrlProvider> standardLists = appDatabase.blockUrlProviderDao().getStandardListsNew();
        // Get all un-deletable packages
        List<BlockUrlProvider> ud_standardLists = appDatabase.blockUrlProviderDao().getStandardLists();
        // Get selected un-deletable packages
        List<BlockUrlProvider> ud_selectedStandardLists = appDatabase.blockUrlProviderDao().getStandardListsBySelectFlag(1);
        // Create a list to hold the URL of un-deletable packages
        List<String> ud_selectedactiveStandardListsURLS = new ArrayList<>();

        /*
            User has NO packages
            Let's add the standard packages
        */

        if(allProviders.isEmpty())
        {
            // Add each standard package and select it
            for(String standardPackage : sabsstandardPackages)
            {
                constructStandardPackages(standardPackage, true);
            }

            return;
        }

        /*
            User has UN-DELETABLE packages
            Let's re-add them as deletable.
        */

        if(!ud_standardLists.isEmpty())
        {
            // Delete them
            appDatabase.blockUrlProviderDao().deleteStandardLists();

            // If there were selected standard lists
            if(!ud_selectedStandardLists.isEmpty())
            {
                // For each one, add the URL to our list
                for(BlockUrlProvider selectedStandard : ud_selectedStandardLists)
                {
                    ud_selectedactiveStandardListsURLS.add(selectedStandard.url);
                }
            }

            // For each standard package
           for(String standardPackage : sabsstandardPackages)
           {
               if(ud_selectedactiveStandardListsURLS.contains(standardPackage))
               {
                   constructStandardPackages(standardPackage, true);
               }
               else
               {
                   constructStandardPackages(standardPackage, false);
               }
           }
        }

        /*
            User has an old default package
            Let's delete it
        */

        // If the user has standard lists
        if(!standardLists.isEmpty())
        {
            // For each standard list
            for(BlockUrlProvider standardList : standardLists)
            {
                String URL = standardList.url;

                if(!sabsstandardPackages.contains(URL))
                {
                    appDatabase.blockUrlProviderDao().delete(standardList);
                }
            }
        }
    }


    public void fillPackageDb() {
        if (appDatabase.applicationInfoDao().getAll().size() > 0) {
            return;
        }
        AppsListDBInitializer.getInstance().fillPackageDb(packageManager);
    }
}