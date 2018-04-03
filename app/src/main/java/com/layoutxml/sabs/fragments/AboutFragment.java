package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;

import java.util.Objects;

public class AboutFragment extends LifecycleFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle(R.string.about_title);
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ((MainActivity)getActivity()).hideBottomBar();

        TextView versionname = view.findViewById(R.id.version);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        versionname.setText("Version : " + versionName + " (internal code: " + versionCode + ")");

        TextView packagename = view.findViewById(R.id.packagename);
        packagename.setText(Objects.requireNonNull(getContext()).getPackageName());

        EditText knoxKeyEditText = view.findViewById(R.id.knox_key_editText);
        Button knoxKeyButton = view.findViewById(R.id.submit_knox_key_button);
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String knoxKey = sharedPreferences.getString("knox_key", null);
        if (knoxKey!=null) {
            knoxKeyEditText.setText(knoxKey);
        }
        knoxKeyButton.setOnClickListener(view1 -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("knox_key", knoxKeyEditText.getText().toString());
            editor.apply();
        });

        return view;
    }
}
