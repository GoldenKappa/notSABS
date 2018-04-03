package com.layoutxml.sabs.dialogfragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.layoutxml.sabs.R;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

import java.util.Objects;

public class DnsChangeDialogFragment extends DialogFragment {

    private EditText mDns1EditText;
    private EditText mDns2EditText;

    public DnsChangeDialogFragment() {
    }

    public static DnsChangeDialogFragment newInstance(String title) {
        DnsChangeDialogFragment dnsChangeDialogFragment = new DnsChangeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        dnsChangeDialogFragment.setArguments(args);
        return dnsChangeDialogFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_dns, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDns1EditText = view.findViewById(R.id.dns_address_1);
        mDns2EditText = view.findViewById(R.id.dns_address_2);
        final SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("dnsAddresses", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("dns1") && sharedPreferences.contains("dns2")) {
            Toast.makeText(view.getContext(), getString(R.string.loading_saved_dns), Toast.LENGTH_SHORT).show();
            mDns1EditText.setText(sharedPreferences.getString("dns1", "0.0.0.0"));
            mDns2EditText.setText(sharedPreferences.getString("dns2", "0.0.0.0"));
        }

        Button mSetDnsButton = view.findViewById(R.id.changeDnsOkButton);
        Button mCancelButton = view.findViewById(R.id.changeDnsCancelButton);
        Button restoreDefaultDnsButton = view.findViewById(R.id.restoreDefaultDnsButton);

        mDns1EditText.requestFocus();
        Objects.requireNonNull(getDialog().getWindow()).setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mSetDnsButton.setOnClickListener(v ->
        {
            String dns1 = mDns1EditText.getText().toString();
            String dns2 = mDns2EditText.getText().toString();
            if (!Patterns.IP_ADDRESS.matcher(dns1).matches() || !Patterns.IP_ADDRESS.matcher(dns2).matches()) {
                Toast.makeText(v.getContext(), getString(R.string.check_input_dns), Toast.LENGTH_LONG).show();
                return;
            }
            ContentBlocker57 contentBlocker57 = (ContentBlocker57) DeviceAdminInteractor.getInstance().getContentBlocker();
            if (contentBlocker57 != null && !contentBlocker57.isEnabled()) {
                Toast.makeText(v.getContext(), getString(R.string.enable_app_for_dns), Toast.LENGTH_LONG).show();
            }
            assert contentBlocker57 != null;
            contentBlocker57.setDns(dns1, dns2);
            Toast.makeText(v.getContext(), getString(R.string.changed_dns), Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("dns1", dns1);
            editor.putString("dns2", dns2);
            editor.apply();
            dismiss();
        });

        mCancelButton.setOnClickListener(v -> dismiss());
        restoreDefaultDnsButton.setOnClickListener(v ->
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("dns1");
            editor.remove("dns2");
            editor.apply();
            ContentBlocker57 contentBlocker57 = (ContentBlocker57) DeviceAdminInteractor.getInstance().getContentBlocker();
            contentBlocker57.disableBlocker();
            Toast.makeText(v.getContext(), getString(R.string.restored_dns), Toast.LENGTH_LONG).show();
            dismiss();
        });

    }
}
