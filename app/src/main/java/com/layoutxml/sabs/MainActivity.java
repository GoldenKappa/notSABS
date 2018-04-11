package com.layoutxml.sabs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.blocker.ContentBlocker56;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.dialogfragment.AdhellNotSupportedDialogFragment;
import com.layoutxml.sabs.dialogfragment.AdhellTurnOnDialogFragment;
import com.layoutxml.sabs.dialogfragment.DnsChangeDialogFragment;
import com.layoutxml.sabs.dialogfragment.NoInternetConnectionDialogFragment;
import com.layoutxml.sabs.fragments.AdhellNotSupportedFragment;
import com.layoutxml.sabs.fragments.AdhellPermissionInfoFragment;
import com.layoutxml.sabs.fragments.AppListFragment;
import com.layoutxml.sabs.fragments.BlockCustomUrlFragment;
import com.layoutxml.sabs.fragments.BlockedUrlSettingFragment;
import com.layoutxml.sabs.fragments.BlockerFragment;
import com.layoutxml.sabs.fragments.CustomBlockUrlProviderFragment;
import com.layoutxml.sabs.fragments.AboutFragment;
import com.layoutxml.sabs.fragments.DonateFragment;
import com.layoutxml.sabs.fragments.MiscSettingsFragment;
import com.layoutxml.sabs.fragments.PackageDisablerFragment;
import com.layoutxml.sabs.fragments.WhitelistFragment;
import com.layoutxml.sabs.service.BlockedDomainService;
import com.layoutxml.sabs.utils.AdhellAppIntegrity;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.util.Objects;

import static com.layoutxml.sabs.Global.BlockPort53;
import static com.layoutxml.sabs.Global.BlockPortAll;
import static com.layoutxml.sabs.Global.BlockedUniqueUrls;


public class MainActivity extends AppCompatActivity {
    public static final String PACKAGE = "https://raw.githubusercontent.com/LayoutXML/SABS-Package/master/package.txt";
    public static final String SABS_MMOTTI_PACKAGE = "https://raw.githubusercontent.com/mmotti/mmotti-host-file/master/wildcard_standard_hosts.txt";
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String BACK_STACK_TAB_TAG = "tab_fragment";
    protected DeviceAdminInteractor mAdminInteractor;
    private FragmentManager fragmentManager;
    private AdhellNotSupportedDialogFragment adhellNotSupportedDialogFragment;
    private AdhellTurnOnDialogFragment adhellTurnOnDialogFragment;
    private NoInternetConnectionDialogFragment noInternetConnectionDialogFragment;
    BottomNavigationView bottomNavigationView;
    private static SharedPreferences sharedPreferences;



    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count <= 1) {
            finish();
        } else {
            fragmentManager.popBackStack();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Boolean blackTheme = sharedPreferences.getBoolean("blackTheme", false);
        Boolean askedDonate = sharedPreferences.getBoolean("donate", false);
        BlockPort53 = sharedPreferences.getBoolean("blockPort53", true);
        BlockPortAll = sharedPreferences.getBoolean("blockPortAll", false);
        if (BlockedUniqueUrls==-1)
            BlockedUniqueUrls = sharedPreferences.getInt("blockedUrls", 0);
        if (blackTheme)
            setTheme(R.style.BlackAppTheme);
        else
            setTheme(R.style.MainAppTheme);

        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        mAdminInteractor = DeviceAdminInteractor.getInstance();
        adhellNotSupportedDialogFragment = AdhellNotSupportedDialogFragment.newInstance("App not supported");
        if (!mAdminInteractor.isContentBlockerSupported()) {
            Log.i(TAG, "Device not supported");
            return;
        }

        adhellTurnOnDialogFragment = AdhellTurnOnDialogFragment.newInstance("Adhell Turn On");
        noInternetConnectionDialogFragment = NoInternetConnectionDialogFragment.newInstance("No Internet connection");
        adhellNotSupportedDialogFragment.setCancelable(false);
        adhellTurnOnDialogFragment.setCancelable(false);
        noInternetConnectionDialogFragment.setCancelable(false);

        if (!mAdminInteractor.isContentBlockerSupported()) {
            return;
        }

        Boolean showDialog = sharedPreferences.getBoolean("showDialog", false);
        if (showDialog)
        {
            if (blackTheme) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.BlackAppThemeDialog).create();
                alertDialog.setTitle(R.string.lock_dialog_Title);
                alertDialog.setMessage(getString(R.string.lock_dialog_body));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "EXIT",
                        (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            } else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.MainAppThemeDialog).create();
                alertDialog.setTitle(R.string.lock_dialog_Title);
                alertDialog.setMessage(getString(R.string.lock_dialog_body));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "EXIT",
                        (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }

        if (!askedDonate)
        {
            sharedPreferences.edit().putBoolean("donate", true).apply();
            if (blackTheme) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.BlackAppThemeDialog).create();
                alertDialog.setTitle(getString(R.string.donate_dialog_title));
                alertDialog.setMessage(getString(R.string.donate_dialog_message));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Paypal",
                        (dialog, which) -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/RJankunas"));
                            startActivity(browserIntent);
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "GPlay",
                        (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.layoutxml.support"));
                            startActivity(intent);
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            } else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.MainAppThemeDialog).create();
                alertDialog.setTitle(R.string.donate_dialog_title);
                alertDialog.setMessage(getString(R.string.donate_dialog_message));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Paypal",
                        (dialog, which) -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/RJankunas"));
                            startActivity(browserIntent);
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "GPlay",
                        (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.layoutxml.support"));
                            startActivity(intent);
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.blockerTab);
        Fragment defaultFragment = new BlockerFragment();
        Fragment second = new PackageDisablerFragment();

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, second)
                .hide(second)
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, defaultFragment)
                .addToBackStack(BACK_STACK_TAB_TAG)
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    Fragment replacing;
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.blockerTab:
                                replacing = new BlockerFragment();
                                break;
                            case R.id.packageDisablerTab:
                                replacing = new PackageDisablerFragment();
                                break;
                            case R.id.appPermissionsTab:
                                replacing = new AdhellPermissionInfoFragment();
                                break;
                        }
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, replacing)
                                .commit();
                        return true;
                    }
                });

        if (blackTheme)
        {
            new AppUpdater(this)
                    .setTitleOnUpdateAvailable("Update available!")
                    .setUpdateFrom(UpdateFrom.GITHUB)
                    .setGitHubUserAndRepo("LayoutXML", "SABS")
                    .setDisplay(Display.SNACKBAR)
                    .start();
        } else
        {
            new AppUpdater(this)
                    .setTitleOnUpdateAvailable("Update available!")
                    .setUpdateFrom(UpdateFrom.GITHUB)
                    .setGitHubUserAndRepo("LayoutXML", "SABS")
                    .start();
        }

        AsyncTask.execute(() -> {
            AdhellAppIntegrity adhellAppIntegrity = new AdhellAppIntegrity();
            adhellAppIntegrity.check();
            adhellAppIntegrity.checkDefaultPolicyExists();
            adhellAppIntegrity.checkAdhellStandardPackage();
            adhellAppIntegrity.fillPackageDb();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                fragmentManager.popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume1");
        if (!mAdminInteractor.isContentBlockerSupported()) {
            Log.i(TAG, "Device not supported");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new AdhellNotSupportedFragment());
            fragmentTransaction.commit();
            return;
        }
        showDialog();
        if (!mAdminInteractor.isActiveAdmin()) {
            Log.d(TAG, "Admin not active");
            return;
        }

        if (!mAdminInteractor.isKnoxEnabled()) {
            Log.d(TAG, "Knox disabled");
            return;
        }
        Log.d(TAG, "Everything is okay");


        ContentBlocker contentBlocker = mAdminInteractor.getContentBlocker();
        if (contentBlocker != null && contentBlocker.isEnabled() && (contentBlocker instanceof ContentBlocker56
                || contentBlocker instanceof ContentBlocker57)) {
            Intent i = new Intent(App.get().getApplicationContext(), BlockedDomainService.class);
            i.putExtra("launchedFrom", "main-activity");
            App.get().getApplicationContext().startService(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying activity");
    }

    public static void updateBlockCount() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("blockedUrls", BlockedUniqueUrls);
        editor.apply();
    }

    public void showDialog() {
        if (!(DeviceAdminInteractor.isSamsung() && mAdminInteractor.isKnoxSupported())) {
            Log.i(TAG, "Device not supported");
            if (!adhellNotSupportedDialogFragment.isVisible()) {
                adhellNotSupportedDialogFragment.show(fragmentManager, "dialog_fragment_adhell_not_supported");
            }
            return;
        }
        if (!mAdminInteractor.isActiveAdmin()) {
            Log.d(TAG, "Admin is not active. Request enabling");
            if (!adhellTurnOnDialogFragment.isVisible()) {
                adhellTurnOnDialogFragment.show(fragmentManager, "dialog_fragment_turn_on_adhell");
            }
            return;
        }

        if (!mAdminInteractor.isKnoxEnabled()) {
            Log.d(TAG, "Knox disabled");
            Log.d(TAG, "Checking if internet connection exists");
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(TAG, "Is internet connection exists: " + isConnected);
            if (!isConnected) {
                if (!noInternetConnectionDialogFragment.isVisible()) {
                    noInternetConnectionDialogFragment.show(fragmentManager, "dialog_fragment_no_internet_connection");
                }
            } else {
                if (!adhellTurnOnDialogFragment.isVisible()) {
                    adhellTurnOnDialogFragment.show(fragmentManager, "dialog_fragment_turn_on_adhell");
                }
            }
        }
    }

    public void editLayoutClick(View view) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            fragmentTransaction.replace(R.id.fragmentContainer, new BlockedUrlSettingFragment());
            fragmentTransaction.addToBackStack("main_to_editUrl");
            fragmentTransaction.commit();
    }

    public void dnsLayoutClick(View view) {
        DnsChangeDialogFragment dnsChangeDialogFragment = DnsChangeDialogFragment.newInstance("Some title");
                dnsChangeDialogFragment.show(fragmentManager, "dialog_fragment_dns");
    }

    public void alloAppsLayout(View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragmentContainer, new AppListFragment());
        fragmentTransaction.addToBackStack("main_to_editApp");
        fragmentTransaction.commit();
        Objects.requireNonNull(MainActivity.this).setTitle(R.string.edit_blocked_apps_list);
    }

    public void aboutLayoutClick(View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragmentContainer, new AboutFragment());
        fragmentTransaction.addToBackStack("main_to_about");
        fragmentTransaction.commit();
    }

    public void redditURL(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/user/LayoutXML"));
        startActivity(browserIntent);
    }

    public void githubURL(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LayoutXML/SABS"));
        startActivity(browserIntent);
    }

    public void copyknoxkey(View view) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String knoxKey = sharedPreferences.getString("knox_key", "key not inserted");
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("key", knoxKey);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content), "Copied "+knoxKey, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public void whitelistLayout(View view) {
       Log.d(TAG, "Edit button click in Fragment1");
       FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
       fragmentTransaction.replace(R.id.fragmentContainer, new WhitelistFragment());
       fragmentTransaction.addToBackStack("manage_url_to_manage_standard");
       fragmentTransaction.commit();
    }

    public void blacklistLayout(View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragmentContainer, new BlockCustomUrlFragment());
        fragmentTransaction.addToBackStack("manage_url_to_add_custom");
        fragmentTransaction.commit();
    }

    public void subscribeLayout(View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragmentContainer, new CustomBlockUrlProviderFragment());
        fragmentTransaction.addToBackStack("manage_custom_url_providers");
        fragmentTransaction.commit();
    }

    public void forceCheckVersion(View view) {
        new AppUpdater(this)
                .setTitleOnUpdateAvailable("Update available!")
                .showAppUpdated(true)
                .setDisplay(Display.SNACKBAR)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("LayoutXML", "SABS")
                .start();
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Checking for updates...", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void viewLicenses(View view) {
        Intent intent = new Intent(this, OssLicensesMenuActivity.class);
        startActivity(intent);
    }

    public void hideBottomBar() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomBar() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void copyPackageID(View view) {
        String packageName = getPackageName();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("packagename", packageName);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content), "Copied "+packageName, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public void miscSettings(View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragmentContainer, new MiscSettingsFragment());
        fragmentTransaction.addToBackStack("misc_settings");
        fragmentTransaction.commit();
    }

    public void donateSettings(View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragmentContainer, new DonateFragment());
        fragmentTransaction.addToBackStack("donate_settings");
        fragmentTransaction.commit();
    }

    public void donateGP(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.layoutxml.support"));
        startActivity(intent);
    }

    public void donatePP(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/RJankunas"));
        startActivity(browserIntent);
    }
}
