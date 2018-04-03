package com.layoutxml.sabs.dialogfragment;


import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.R;

public class AdhellNotSupportedDialogFragment extends DialogFragment {

    public AdhellNotSupportedDialogFragment() {
    }

    public static AdhellNotSupportedDialogFragment newInstance(String title) {
        AdhellNotSupportedDialogFragment adhellNotSupportedDialogFragment = new AdhellNotSupportedDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        adhellNotSupportedDialogFragment.setArguments(args);
        return adhellNotSupportedDialogFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        TextView versionname = getView().findViewById(R.id.version);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        versionname.setText("Version : " + versionName + " (internal code: " + versionCode + ")");
        return inflater.inflate(R.layout.dialog_fragment_adhell_not_supported, container);
    }
}
