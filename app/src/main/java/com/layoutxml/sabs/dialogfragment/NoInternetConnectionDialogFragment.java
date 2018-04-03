package com.layoutxml.sabs.dialogfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layoutxml.sabs.R;

public class NoInternetConnectionDialogFragment extends DialogFragment {

    public NoInternetConnectionDialogFragment() {
    }

    public static NoInternetConnectionDialogFragment newInstance(String title) {
        NoInternetConnectionDialogFragment noInternetConnectionDialogFragment = new NoInternetConnectionDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        noInternetConnectionDialogFragment.setArguments(args);
        return noInternetConnectionDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_no_internet_connection, container);
    }
}
