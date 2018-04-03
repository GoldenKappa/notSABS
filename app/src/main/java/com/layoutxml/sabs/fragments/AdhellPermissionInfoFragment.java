package com.layoutxml.sabs.fragments;

import android.app.enterprise.AppPermissionControlInfo;
import android.app.enterprise.ApplicationPermissionControlPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.adapter.AdhellPermissionInfoAdapter;
import com.layoutxml.sabs.adapter.ItemClickSupport;
import com.layoutxml.sabs.dialogfragment.AdhellTurnOnDialogFragment;
import com.layoutxml.sabs.model.AdhellPermissionInfo;
import com.layoutxml.sabs.viewmodel.SharedAppPermissionViewModel;

import java.util.List;
import java.util.Objects;

public class AdhellPermissionInfoFragment extends LifecycleFragment {
    private static final String TAG = AdhellPermissionInfoFragment.class.getCanonicalName();
    private List<AdhellPermissionInfo> adhellPermissionInfos;
    private AppCompatActivity parentActivity;
    private SharedAppPermissionViewModel sharedAppPermissionViewModel;
    private FragmentManager fragmentManager;
    private AdhellTurnOnDialogFragment adhellTurnOnDialogFragment;
    private static SharedPreferences sharedPreferences;
    private Boolean blackTheme;
    ApplicationPermissionControlPolicy mAppControlPolicy;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();
        boolean isPermissionGranted = (this.getContext()
                .checkCallingOrSelfPermission("android.permission.sec.MDM_APP_PERMISSION_MGMT")
                == PackageManager.PERMISSION_GRANTED);
        if (isPermissionGranted) {
            Log.i(TAG, "Permission granted");
        } else {
            Log.w(TAG, "Permission for application permission policy is not granted");
            Toast.makeText(this.getContext(), "You need to re-enable admin to make this work", Toast.LENGTH_LONG).show();
            adhellTurnOnDialogFragment = AdhellTurnOnDialogFragment.newInstance("Adhell Turn On");
            adhellTurnOnDialogFragment.setCancelable(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("App Permissions");
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(false);
        }

        ((MainActivity)getActivity()).showBottomBar();

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        blackTheme = sharedPreferences.getBoolean("blackTheme", false);

        EnterpriseDeviceManager edm = (EnterpriseDeviceManager) Objects.requireNonNull(getContext()).getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        assert edm != null;
        mAppControlPolicy = edm.getApplicationPermissionControlPolicy();

        sharedAppPermissionViewModel = ViewModelProviders.of(getActivity()).get(SharedAppPermissionViewModel.class);
        fragmentManager = getActivity().getSupportFragmentManager();
        adhellPermissionInfos = AdhellPermissionInfo.loadPermissions();
        View view = inflater.inflate(R.layout.fragment_adhell_permission_info, container, false);
        RecyclerView permissionInfoRecyclerView = view.findViewById(R.id.permissionInfoRecyclerView);
        AdhellPermissionInfoAdapter adhellPermissionInfoAdapter = new AdhellPermissionInfoAdapter(this.getContext(), adhellPermissionInfos);
        permissionInfoRecyclerView.setAdapter(adhellPermissionInfoAdapter);
        permissionInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);

        sharedAppPermissionViewModel.loadInstalledAppsLiveData().observe(this, installedApps -> {
            sharedAppPermissionViewModel.installedApps = installedApps;
        });


        permissionInfoRecyclerView.addItemDecoration(itemDecoration);
        ItemClickSupport.addTo(permissionInfoRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    sharedAppPermissionViewModel.select(adhellPermissionInfos.get(position));
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, new AdhellPermissionInAppsFragment());
                    fragmentTransaction.addToBackStack("permissionsInfo_permissionsInApp");
                    fragmentTransaction.commit();
                }
        );
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.permissions_manager_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_restore_perms:
                if (blackTheme) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.BlackAppThemeDialog).create();
                    alertDialog.setTitle(getString(R.string.restore_permissions));
                    alertDialog.setMessage(getString(R.string.restore_permissions_message));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog, which) -> {
                                assert mAppControlPolicy != null;
                                boolean result = mAppControlPolicy.clearPackagesFromPermissionBlackList();
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                } else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.MainAppThemeDialog).create();
                    alertDialog.setTitle(getString(R.string.restore_permissions));
                    alertDialog.setMessage(getString(R.string.restore_permissions_message));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog, which) -> {
                                assert mAppControlPolicy != null;
                                boolean result = mAppControlPolicy.clearPackagesFromPermissionBlackList();
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
                break;
            case R.id.action_app_settings:
                Log.d(TAG, "App setting action clicked");
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new AppSettingsFragment(), AppSettingsFragment.class.getCanonicalName())
                        .addToBackStack(AppSettingsFragment.class.getCanonicalName())
                        .commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
