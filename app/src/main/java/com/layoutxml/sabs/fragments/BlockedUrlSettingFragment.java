package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

public class BlockedUrlSettingFragment extends LifecycleFragment {
    private ContentBlocker contentBlocker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentBlocker = DeviceAdminInteractor.getInstance().getContentBlocker();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocked_url_settings, container, false);
        getActivity().setTitle(R.string.settings);
        TextView showCustomUrlProvidersFragmentButton = view.findViewById(R.id.showCustomUrlProvidersFragmentButton);
        if (contentBlocker instanceof ContentBlocker57) {} else {
            showCustomUrlProvidersFragmentButton.setVisibility(View.GONE);
        }

        ((MainActivity)getActivity()).hideBottomBar();

        return view;
    }
}
