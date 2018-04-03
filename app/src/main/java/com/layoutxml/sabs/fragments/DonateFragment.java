package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;

import java.util.Objects;

public class DonateFragment extends LifecycleFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle(R.string.donate);
        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        ((MainActivity)getActivity()).hideBottomBar();

        return view;
    }
}