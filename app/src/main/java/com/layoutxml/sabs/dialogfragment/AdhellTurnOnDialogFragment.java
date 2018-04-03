package com.layoutxml.sabs.dialogfragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.sabs.R;
import com.layoutxml.sabs.fragments.BlockerFragment;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

import org.w3c.dom.Text;

import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class AdhellTurnOnDialogFragment extends DialogFragment {
    private static final String TAG = AdhellTurnOnDialogFragment.class.getCanonicalName();
    BroadcastReceiver receiver;
    IntentFilter filter;
    private DeviceAdminInteractor deviceAdminInteractor;
    private Single<String> knoxKeyObservable;
    private Button turnOnAdminButton;
    private Button activateKnoxButton;
    private CompositeDisposable disposable;
    private FragmentManager fragmentManager;
    private TextView textView;

    public AdhellTurnOnDialogFragment() {
        deviceAdminInteractor = DeviceAdminInteractor.getInstance();
        knoxKeyObservable = Single.create(emmiter -> {
            String knoxKey;
            try {
                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                knoxKey = deviceAdminInteractor.getKnoxKey(sharedPreferences);
                emmiter.onSuccess(knoxKey);
            } catch (Throwable e) {
                emmiter.onError(e);
                Log.e(TAG, "Failed to getAll knox key", e);
            }
        });
    }

    public static AdhellTurnOnDialogFragment newInstance(String title) {
        AdhellTurnOnDialogFragment adhellTurnOnDialogFragment = new AdhellTurnOnDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        adhellTurnOnDialogFragment.setArguments(args);
        return adhellTurnOnDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_turn_on_adhell, container);
        turnOnAdminButton = view.findViewById(R.id.turnOnAdminButton);
        activateKnoxButton = view.findViewById(R.id.activateKnoxButton);

        fragmentManager = getActivity().getSupportFragmentManager();
        turnOnAdminButton.setOnClickListener(v -> {
            deviceAdminInteractor.forceEnableAdmin(this.getActivity());
        });

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
            String firstthree = knoxKeyEditText.getText().toString().substring(0, 3);
            if (Objects.equals(firstthree, "KLM"))
            {
                textView = view.findViewById(R.id.textView);
                textView.setText(R.string.steps_to_enable_reminder_error_KLM);
                textView.setTextColor(Color.RED);
            }
            editor.commit();
            Toast.makeText(getContext(), "Key Submitted", Toast.LENGTH_SHORT).show();
            knoxKeyButton.setText("Key Submitted");
        });

        // TODO: Implement on error
        activateKnoxButton.setOnClickListener(v -> {
            Log.d(TAG, "Activate Knox button clicked");
            activateKnoxButton.setEnabled(false);
            activateKnoxButton.setText(R.string.activating_knox_license);
            Disposable subscribe = knoxKeyObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<String>() {

                        @Override
                        public void onSuccess(@NonNull String knoxKey) {
                            if (knoxKey == null) {
                                activateKnoxButton.setEnabled(true);
                                activateKnoxButton.setText(R.string.activate_knox);
                                Log.w(TAG, "Failed to activate knox");
                            }
                            try {
                                deviceAdminInteractor.forceActivateKnox(knoxKey);
                            } catch (Exception e) {
                                activateKnoxButton.setEnabled(true);
                                activateKnoxButton.setText(R.string.activate_knox);
                                Log.e(TAG, "Failed to activate knox", e);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            activateKnoxButton.setEnabled(true);
                            activateKnoxButton.setText(R.string.activate_knox);
                            Log.e(TAG, "Failed to activate knox", e);
                        }
                    });
            disposable.add(subscribe);
            Log.d(TAG, "Exiting button click");
        });

        filter = new IntentFilter();
        filter.addAction("edm.intent.action.license.status");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DeviceAdminInteractor deviceAdminInteractor = DeviceAdminInteractor.getInstance();
                if (deviceAdminInteractor.isKnoxEnabled()) {
                    Toast.makeText(context, "License activated", Toast.LENGTH_LONG).show();
                    dismiss();
                    allowActivateKnox(false);
                    activateKnoxButton.setText("License Activated");
                    Log.d(TAG, "License activated");
                } else {
                    Toast.makeText(context, "License activation failed. Try again", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "License activation failed");
                    activateKnoxButton.setEnabled(true); // Allow the user to try again
                    activateKnoxButton.setText("Activate License"); // Allow the user to try again
                    allowActivateKnox(true);  // Allow the user to try again
                }
            }
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getActivity().registerReceiver(receiver, filter);
        disposable = new CompositeDisposable();
        Log.i(TAG, "AdhellTurnOnDialogFragment on Resume");
        if (deviceAdminInteractor.isActiveAdmin()) {
            allowTurnOnAdmin(false);
            turnOnAdminButton.setText("Admin Enabled");
        } else {
            allowTurnOnAdmin(true);
            turnOnAdminButton.setText("Enable Admin");
        }
        if (deviceAdminInteractor.isKnoxEnabled()) {
            activateKnoxButton.setText("License activated");
            allowActivateKnox(false);
        } else {
            if (!deviceAdminInteractor.isActiveAdmin()) {
                activateKnoxButton.setText("Activate License");
                allowActivateKnox(false);
            } else {
                activateKnoxButton.setText("Activate License");
                allowActivateKnox(true);
            }
        }
        if (deviceAdminInteractor.isActiveAdmin() && deviceAdminInteractor.isKnoxEnabled()) {
            dismiss();
        }
    }


    private void allowActivateKnox(boolean isAllowed) {
        Log.i(TAG, "allowActivateKnox");
        activateKnoxButton.setEnabled(isAllowed);
        activateKnoxButton.setClickable(isAllowed);
    }

    private void allowTurnOnAdmin(boolean isAllowed) {
        turnOnAdminButton.setClickable(isAllowed);
        turnOnAdminButton.setEnabled(isAllowed);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog){
        super.onDismiss(dialog);
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, new BlockerFragment(), BlockerFragment.class.getCanonicalName())
                .commit();
    }
}
