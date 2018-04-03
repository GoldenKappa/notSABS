package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;

public class AdhellNotSupportedFragment extends LifecycleFragment {
    private static final String TAG = AdhellNotSupportedFragment.class.getCanonicalName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).hideBottomBar();
        View view = inflater.inflate(R.layout.fragment_adhell_not_supported, container, false);
        TextView versionname = view.findViewById(R.id.version);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        versionname.setText("Version : " + versionName + " (internal code: " + versionCode + ")");
        return view;
    }

}
