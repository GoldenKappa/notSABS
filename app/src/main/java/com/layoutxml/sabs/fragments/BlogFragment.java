package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layoutxml.sabs.R;

public class BlogFragment extends LifecycleFragment {
    private TextView entryTextView;
    AppCompatActivity parentActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        parentActivity.setTitle(getString(R.string.blog));
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(true);
        }

        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        entryTextView = view.findViewById(R.id.entryTextView);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();
    }
}
