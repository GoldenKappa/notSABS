package com.layoutxml.sabs.fragments;


import android.arch.lifecycle.LifecycleFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.layoutxml.sabs.R;

public class AppSupportFragment extends LifecycleFragment {
    private static final String TAG = AppSupportFragment.class.getCanonicalName();
    private TextView supportDevelopmentTextView;
    private AppCompatActivity parentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.app_support_fragment_title));
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(false);
        }
        View view = inflater.inflate(R.layout.fragment_app_support, container, false);

        supportDevelopmentTextView = view.findViewById(R.id.supportDevelopmentTextView);

        Button donateButton = view.findViewById(R.id.paypal_donate_button);
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent donateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.com"));
                startActivity(donateIntent);
            }
        });

        return view;
    }
}
