package com.layoutxml.sabs.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.layoutxml.sabs.Global.BlockPort53;
import static com.layoutxml.sabs.Global.BlockPortAll;

public class MiscSettingsFragment extends LifecycleFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle(R.string.misc_settings);
        View view = inflater.inflate(R.layout.fragment_misc_settings, container, false);

        ((MainActivity)getActivity()).hideBottomBar();

        Switch showDialogSwitch = view.findViewById(R.id.showDialogSwitch);
        Switch blackThemeSwitch = view.findViewById(R.id.blackThemeSwitch);
        Switch blockPortSwitch = view.findViewById(R.id.blockPortSwitch);
        Switch blockThemeStore = view.findViewById(R.id.blockThemeSwitch);
        CheckBox blockPortAllBox = view.findViewById(R.id.blockAllBox);
        TextView blockPortAllText = view.findViewById(R.id.textView21);
        View MiscShowWarning = view.findViewById(R.id.MiscShowWarning);
        View MiscBlackTheme = view.findViewById(R.id.MiscBlackTheme);
        View MiscBlockPort = view.findViewById(R.id.MiscBlockPort);
        View MiscBlockAll = view.findViewById(R.id.MiscBlockAll);
        View MiscBlockTheme = view.findViewById(R.id.MiscBlockThemes);
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Boolean showDialog = sharedPreferences.getBoolean("showDialog", false);
        Boolean blackTheme = sharedPreferences.getBoolean("blackTheme", false);
        Boolean blockPort = sharedPreferences.getBoolean("blockPort53", true);
        Boolean blockPortAll = sharedPreferences.getBoolean("blockPortAll", false);
        Boolean blockTheme =  sharedPreferences.getBoolean("blockThemeStore", false);
        showDialogSwitch.setChecked(showDialog);
        blackThemeSwitch.setChecked(blackTheme);
        blockPortSwitch.setChecked(blockPort);
        blockPortAllBox.setChecked(blockPortAll);
        blockPortAllBox.setEnabled(blockPort);
        blockThemeStore.setChecked(blockTheme);
        if (blockPort)
        {
            blockPortAllText.setAlpha(1F);
            blockPortAllBox.setAlpha(1F);
        }
        else
        {
            blockPortAllBox.setAlpha(.5F);
            blockPortAllText.setAlpha(.5F);
        }


        MiscShowWarning.setOnClickListener(v -> {
            boolean isChecked = !showDialogSwitch.isChecked();
            showDialogSwitch.setChecked(isChecked);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("showDialog", isChecked);
            editor.apply();
        });

        MiscBlackTheme.setOnClickListener(v -> {
            boolean isChecked = !blackThemeSwitch.isChecked();
            blackThemeSwitch.setChecked(isChecked);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("blackTheme", isChecked);
            editor.apply();
            Intent mStartActivity = new Intent(getActivity(), MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
            assert mgr != null;
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 600, mPendingIntent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    System.exit(0);
                }
            }, 500);
        });

        MiscBlockPort.setOnClickListener(v -> {
            boolean isChecked = !blockPortSwitch.isChecked();
            blockPortSwitch.setChecked(isChecked);
            blockPortAllBox.setEnabled(isChecked);
            if (isChecked)
            {
                blockPortAllText.setAlpha(1F);
                blockPortAllBox.setAlpha(1F);
            }
            else
            {
                blockPortAllBox.setAlpha(.5F);
                blockPortAllText.setAlpha(.5F);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("blockPort53", isChecked);
            editor.apply();
            BlockPort53=isChecked;
        });

        MiscBlockAll.setOnClickListener(v -> {
            boolean isChecked = !blockPortAllBox.isChecked();
            boolean isAllowed = blockPortSwitch.isChecked();
            if (isAllowed)
            {
                blockPortAllBox.setChecked(isChecked);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("blockPortAll", isChecked);
                editor.apply();
                BlockPortAll=isChecked;
            }
        });

        MiscBlockTheme.setOnClickListener(v -> {
            Log.e("Exception", "1");
            boolean isChecked = !blockThemeStore.isChecked();
            EnterpriseDeviceManager edm = (EnterpriseDeviceManager) getActivity().getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
            assert edm != null;
            ApplicationPolicy appPolicy = edm.getApplicationPolicy();
            List<String> pkgList = appPolicy.getPackagesFromPreventStartBlackList();
            if(!isChecked)
            {
                try {
                    boolean status = appPolicy.clearPreventStartBlackList();
                    if (status){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("blockThemeStore", false);
                        editor.apply();
                        blockThemeStore.setChecked(false);
                    }
                } catch (SecurityException e) {
                    Log.e("Exception", "SecurityException: " + e);
                }
            } else
            {
                List<String> list = new ArrayList<String>();
                list.add("com.samsung.android.themestore");
                list.add("com.samsung.android.themecenter");
                try {
                    List<String> addedList = appPolicy.addPackagesToPreventStartBlackList(list);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("blockThemeStore", true);
                    editor.apply();
                    blockThemeStore.setChecked(true);
                } catch (SecurityException e) {
                    Log.e("Exception", "SecurityException: " + e);
                }
            }
        });

        return view;
    }
}