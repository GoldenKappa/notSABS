package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.adapter.AppWhitelistAdapter;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.layoutxml.sabs.viewmodel.AdhellWhitelistAppsViewModel;

import java.util.Objects;


public class AppListFragment extends LifecycleFragment {
    private static final String TAG = AppListFragment.class.getCanonicalName();
    private ListView appListView;
    private AppWhitelistAdapter appWhitelistAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        appListView = view.findViewById(R.id.appList);
        EditText adblockEnabledAppSearchEditText = view.findViewById(R.id.adblockEnabledAppSearchEditText);

        ((MainActivity) Objects.requireNonNull(getActivity())).hideBottomBar();

        adblockEnabledAppSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                charSequence.toString()


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (appWhitelistAdapter != null) {
                    appWhitelistAdapter.getFilter().filter(editable.toString());
                }
            }
        });

        AdhellWhitelistAppsViewModel adhellWhitelistAppsViewModel = ViewModelProviders.of(getActivity()).get(AdhellWhitelistAppsViewModel.class);
        adhellWhitelistAppsViewModel.getSortedAppInfo().observe(this, appInfos -> {
            if (appWhitelistAdapter == null) {
                assert appInfos != null;
                appWhitelistAdapter = new AppWhitelistAdapter(Objects.requireNonNull(this.getContext()), appInfos);
                appListView.setAdapter(appWhitelistAdapter);
            } else {
                appWhitelistAdapter.notifyDataSetChanged();
            }
        });

        appListView.setOnItemClickListener((parent, view1, position, id) -> {
            AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
            Log.d(TAG, "Will toggle app: " + appInfo.packageName);
            adhellWhitelistAppsViewModel.toggleApp(appInfo);
        });
        return view;
    }
}
