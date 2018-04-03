package com.layoutxml.sabs.fragments;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.adapter.BlockUrlProviderAdapter;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.BlockUrl;
import com.layoutxml.sabs.db.entity.BlockUrlProvider;
import com.layoutxml.sabs.utils.BlockUrlPatternsMatch;
import com.layoutxml.sabs.utils.BlockUrlUtils;
import com.layoutxml.sabs.viewmodel.BlockUrlProvidersViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.layoutxml.sabs.Global.BlockedUniqueUrls;

public class CustomBlockUrlProviderFragment extends LifecycleFragment {

    private static final String TAG = CustomBlockUrlProviderFragment.class.getCanonicalName();
    private AppDatabase mDb;
    private EditText blockUrlProviderEditText;
    private Button addBlockUrlProviderButton;
    private ListView blockListView;
    private ProgressDialog dialogLoading;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_url_provider, container, false);
        getActivity().setTitle(R.string.subscribe_to_providers);
        addBlockUrlProviderButton = view.findViewById(R.id.addBlockUrlProviderButton);
        blockListView = view.findViewById(R.id.blockUrlProviderListView);
        TextView uniqueTextView = view.findViewById(R.id.uniqueDomains);
        Button updateBlockUrlProvidersButton = view.findViewById(R.id.updateBlockUrlProvidersButton);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Boolean blackTheme = sharedPreferences.getBoolean("blackTheme", false);
        if (BlockedUniqueUrls==0)
            BlockedUniqueUrls = sharedPreferences.getInt("blockedUrls", 0);
        uniqueTextView.setText("Blocked unique domains: "+BlockedUniqueUrls+". Note that you need to reapply blocking for the number to update.");

        ((MainActivity)getActivity()).hideBottomBar();

        updateBlockUrlProvidersButton.setOnClickListener(v -> {
            if (blackTheme) {
                dialogLoading = new ProgressDialog(getActivity(), R.style.BlackAppThemeDialog);
                String message = "Please wait. This may take a couple of minutes. Do not leave SABS.";
                SpannableString message2 = new SpannableString(message);
                dialogLoading.setTitle("Updating");
                message2.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message2.length(), 0);
                dialogLoading.setMessage(message2);
                dialogLoading.setIndeterminate(true);
                dialogLoading.setCancelable(false);
                dialogLoading.show();
            } else
            {
                dialogLoading = new ProgressDialog(getActivity(), R.style.MainAppThemeDialog);
                String message = "Please wait. This may take a couple of minutes. Do not leave SABS.";
                SpannableString message2 =  new SpannableString(message);
                dialogLoading.setTitle("Updating");
                dialogLoading.setMessage(message2);
                dialogLoading.setIndeterminate(true);
                dialogLoading.setCancelable(false);
                dialogLoading.show();
            }
            // TODO: getAll all
            // TODO: then loop and delete and update
            Maybe.fromCallable(() -> {
                List<BlockUrlProvider> blockUrlProviders = mDb.blockUrlProviderDao().getAll2();
                mDb.blockUrlDao().deleteAll();
                for (BlockUrlProvider blockUrlProvider : blockUrlProviders) {
                    try {
                        List<BlockUrl> blockUrls = BlockUrlUtils.loadBlockUrls(blockUrlProvider);
                        blockUrlProvider.count = blockUrls.size();
                        blockUrlProvider.lastUpdated = new Date();
                        mDb.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider);
                        mDb.blockUrlDao().insertAll(blockUrls);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to fetch url from urlProvider", e);
                    }
                }
                if (dialogLoading.isShowing()) {
                    dialogLoading.dismiss();
                }
                return null;
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

        });
        addBlockUrlProviderButton.setOnClickListener(v -> {
            if(blackTheme)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.BlackAppThemeDialog);
                builder.setTitle("Choose a provider");
                builder.setMessage("Add domain (host) list. This can be a URL or file location.");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String urlProvider = input.getText().toString();
                        dialogLoading = new ProgressDialog(getActivity(), R.style.BlackAppThemeDialog);
                        String message = "Please wait. This may take a couple of minutes. Do not leave SABS.";
                        SpannableString message2 =  new SpannableString(message);
                        dialogLoading.setTitle("Adding custom provider");
                        message2.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message2.length(), 0);
                        dialogLoading.setMessage(message2);
                        dialogLoading.setIndeterminate(true);
                        dialogLoading.setCancelable(false);
                        dialogLoading.show();
                        AddCustomProvider(urlProvider);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.MainAppThemeDialog);
                builder.setTitle("Choose a provider");
                builder.setMessage("Add domain (host) list. This can be a URL or file location.");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String urlProvider = input.getText().toString();
                        dialogLoading = new ProgressDialog(getActivity(), R.style.MainAppThemeDialog);
                        String message = "Please wait. This may take a couple of minutes. Do not leave SABS.";
                        SpannableString message2 =  new SpannableString(message);
                        dialogLoading.setTitle("Adding custom provider");
                        dialogLoading.setMessage(message2);
                        dialogLoading.setIndeterminate(true);
                        dialogLoading.setCancelable(false);
                        dialogLoading.show();
                        AddCustomProvider(urlProvider);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        BlockUrlProvidersViewModel model = ViewModelProviders.of(getActivity()).get(BlockUrlProvidersViewModel.class);
        model.getBlockUrlProviders().observe(this, blockUrlProviders -> {
            BlockUrlProviderAdapter adapter = new BlockUrlProviderAdapter(this.getContext(), blockUrlProviders);
            blockListView.setAdapter(adapter);
        });

        return view;
    }

    private void AddCustomProvider(String urlProvider){
        // Check whether the file path is valid
        boolean validFilePath = BlockUrlPatternsMatch.filepathValid(urlProvider);

        // If a value has been entered and matches our local host file regex
        if (!urlProvider.isEmpty() && validFilePath) {

            // Construct the local path
            String localfilePath = urlProvider;

            // Create a new file object for verifying
            File localhostFile = new File(localfilePath);

            // If the file exists
            if(localhostFile.exists())
            {
                Log.d(TAG, "Local host file detected: " + localfilePath);

                Maybe.fromCallable(() -> {
                    BlockUrlProvider blockUrlProvider = new BlockUrlProvider();
                    blockUrlProvider.url = localfilePath;
                    blockUrlProvider.count = 0;
                    blockUrlProvider.deletable = true;
                    blockUrlProvider.lastUpdated = new Date();
                    blockUrlProvider.selected = true;
                    blockUrlProvider.id = mDb.blockUrlProviderDao().insertAll(blockUrlProvider)[0];
                    // Try to download and parse urls
                    try {
                        List<BlockUrl> blockUrls = BlockUrlUtils.loadBlockUrls(blockUrlProvider);
                        blockUrlProvider.count = blockUrls.size();
                        Log.d(TAG, "Number of urls to insert: " + blockUrlProvider.count);
                        // Save url provider
                        mDb.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider);
                        // Save urls from providers
                        mDb.blockUrlDao().insertAll(blockUrls);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to download links from urlproviders", e);
                    }
                    if (dialogLoading.isShowing()) {
                        dialogLoading.dismiss();
                    }
                    return null;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
            else
            {
                Toast.makeText(getContext(), "Host file not found.", Toast.LENGTH_LONG).show();
            }
        }
        // Check if normal url
        else if (!urlProvider.isEmpty() && Patterns.WEB_URL.matcher(urlProvider).matches()) {
            Maybe.fromCallable(() -> {
                BlockUrlProvider blockUrlProvider = new BlockUrlProvider();
                blockUrlProvider.url = (URLUtil.isValidUrl(urlProvider)) ? urlProvider : "http://" + urlProvider;
                blockUrlProvider.count = 0;
                blockUrlProvider.deletable = true;
                blockUrlProvider.lastUpdated = new Date();
                blockUrlProvider.selected = true;
                blockUrlProvider.id = mDb.blockUrlProviderDao().insertAll(blockUrlProvider)[0];
                // Try to download and parse urls
                try {
                    List<BlockUrl> blockUrls = BlockUrlUtils.loadBlockUrls(blockUrlProvider);
                    blockUrlProvider.count = blockUrls.size();
                    Log.d(TAG, "Number of urls to insert: " + blockUrlProvider.count);
                    // Save url provider
                    mDb.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider);
                    // Save urls from providers
                    mDb.blockUrlDao().insertAll(blockUrls);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to download links from urlproviders", e);
                }
                if (dialogLoading.isShowing()) {
                    dialogLoading.dismiss();
                }
                return null;
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        } else {
            Toast.makeText(getContext(), "Url is invalid", Toast.LENGTH_LONG).show();
        }
    }
}
