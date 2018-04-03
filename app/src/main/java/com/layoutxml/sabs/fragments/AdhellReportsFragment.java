package com.layoutxml.sabs.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.adapter.ReportBlockedUrlAdapter;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.layoutxml.sabs.db.entity.ReportBlockedUrl;
import com.layoutxml.sabs.viewmodel.AdhellReportViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.layoutxml.sabs.Global.RecentActivityDays;
import static com.layoutxml.sabs.Global.domainsToExport;
import static java.lang.Integer.parseInt;


public class AdhellReportsFragment extends LifecycleFragment {
    private AppCompatActivity parentActivity;
    private TextView lastDayBlockedTextView;
    private TextView lastDayInfoTextView;
    private ListView blockedDomainsListView;
    private ProgressDialog dialogLoading;
    private SwipeRefreshLayout refreshRecentActivity;
    private Handler mHandler = new Handler();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity.setTitle("Recent activity");
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(true);
        }
        View view = inflater.inflate(R.layout.fragment_adhell_reports, container, false);
        setHasOptionsMenu(true);
        ((MainActivity) Objects.requireNonNull(getActivity())).hideBottomBar();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Boolean blackTheme = sharedPreferences.getBoolean("blackTheme", false);

        lastDayBlockedTextView = view.findViewById(R.id.lastDayBlockedTextView);
        blockedDomainsListView = view.findViewById(R.id.blockedDomainsListView);
        lastDayInfoTextView = view.findViewById(R.id.lastDayInfoTextView);
        lastDayInfoTextView.setText("Blocked in the last " + Integer.toString(RecentActivityDays) + " day(s): ");
        refreshRecentActivity = view.findViewById(R.id.refreshRecentActivity);
        refreshRecentActivity.setOnRefreshListener(getSwipeRefreshListener());

        AdhellReportViewModel adhellReportViewModel = ViewModelProviders.of(getActivity()).get(AdhellReportViewModel.class);
        adhellReportViewModel.getReportBlockedUrls().observe(this, reportBlockedUrls -> {
            assert reportBlockedUrls != null;
            ReportBlockedUrlAdapter reportBlockedUrlAdapter = new ReportBlockedUrlAdapter(Objects.requireNonNull(this.getContext()), reportBlockedUrls);
            blockedDomainsListView.setAdapter(reportBlockedUrlAdapter);
            lastDayBlockedTextView.setText(String.valueOf(reportBlockedUrls.size()));
            reportBlockedUrlAdapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recent_activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        Boolean blackTheme = sharedPreferences.getBoolean("blackTheme", false);
        switch (item.getItemId()) {
            case R.id.action_export:
                if (blackTheme) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.BlackAppThemeDialog);
                    builder.setTitle(getString(R.string.action_export_hint));
                    builder.setMessage("Export domains blocked in the last " + Integer.toString(RecentActivityDays) + " days to a file. Choose a file name below. \".txt\" extension is added automatically.");
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogLoading = new ProgressDialog(getActivity(), R.style.BlackAppThemeDialog);
                            String message = "Please wait. This may take a couple of minutes. Do not leave SABS.";
                            SpannableString message2 = new SpannableString(message);
                            dialogLoading.setTitle("Exporting");
                            message2.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message2.length(), 0);
                            dialogLoading.setMessage(message2);
                            dialogLoading.setIndeterminate(true);
                            dialogLoading.setCancelable(false);
                            dialogLoading.show();
                            ExportBlockedDomains(input.getText().toString());
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
                    builder.setTitle(getString(R.string.action_export_hint));
                    builder.setMessage("Export domains blocked in the last " + Integer.toString(RecentActivityDays) + " days to a file. Choose a file name below. \".txt\" extension is added automatically.");
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogLoading = new ProgressDialog(getActivity(), R.style.MainAppThemeDialog);
                            String message = "Please wait. This may take a couple of minutes. Do not leave SABS.";
                            SpannableString message2 = new SpannableString(message);
                            dialogLoading.setTitle("Exporting");
                            message2.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message2.length(), 0);
                            dialogLoading.setMessage(message2);
                            dialogLoading.setIndeterminate(true);
                            dialogLoading.setCancelable(false);
                            dialogLoading.show();
                            ExportBlockedDomains(input.getText().toString());
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
                break;
            case R.id.action_change_days:
                if (blackTheme) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.BlackAppThemeDialog);
                    builder.setTitle(getString(R.string.action_change_days_hint));
                    builder.setMessage("Select the time for recent activity and exporting blocked domains to file in days from 1 to 7.");
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int number = parseInt(input.getText().toString());
                            if (number > 7)
                                number = 7;
                            else if (number < 1)
                                number = 1;
                            RecentActivityDays=number;
                            lastDayInfoTextView.setText("Blocked in the last " + Integer.toString(number) + " day(s): ");
                            AdhellReportViewModel adhellReportViewModel = ViewModelProviders.of(getActivity()).get(AdhellReportViewModel.class);
                            adhellReportViewModel.getReportBlockedUrls().observe(getActivity(), reportBlockedUrls -> {
                                assert reportBlockedUrls != null;
                                ReportBlockedUrlAdapter reportBlockedUrlAdapter = new ReportBlockedUrlAdapter(Objects.requireNonNull(getContext()), reportBlockedUrls);
                                blockedDomainsListView.setAdapter(reportBlockedUrlAdapter);
                                lastDayBlockedTextView.setText(String.valueOf(reportBlockedUrls.size()));
                                reportBlockedUrlAdapter.notifyDataSetChanged();
                            });
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
                    builder.setTitle(getString(R.string.action_change_days_hint));
                    builder.setMessage("Select the time for recent activity and exporting blocked domains to file in days from 1 to 7.");
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int number = parseInt(input.getText().toString());
                            if (number > 7)
                                number = 7;
                            else if (number < 1)
                                number = 1;
                            RecentActivityDays=number;
                            lastDayInfoTextView.setText("Blocked in the last " + Integer.toString(number) + " day(s): ");
                            AdhellReportViewModel adhellReportViewModel = ViewModelProviders.of(getActivity()).get(AdhellReportViewModel.class);
                            adhellReportViewModel.getReportBlockedUrls().observe(getActivity(), reportBlockedUrls -> {
                                assert reportBlockedUrls != null;
                                ReportBlockedUrlAdapter reportBlockedUrlAdapter = new ReportBlockedUrlAdapter(Objects.requireNonNull(getContext()), reportBlockedUrls);
                                blockedDomainsListView.setAdapter(reportBlockedUrlAdapter);
                                lastDayBlockedTextView.setText(String.valueOf(reportBlockedUrls.size()));
                                reportBlockedUrlAdapter.notifyDataSetChanged();
                            });
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void ExportBlockedDomains(String filename) {
        exportDomains(filename);
        if (dialogLoading.isShowing()) {
            dialogLoading.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void exportDomains(String filename) {
        filename = filename.replace(".txt","");
        File file = new File(Environment.getExternalStorageDirectory(), filename+".txt");
        int count=0;
        try {
            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write("");
            for (String item : domainsToExport){
                count++;
                writer.append(item).append("\n");
                }
                writer.close();
            stream.flush();
            stream.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Exported " + count + " domains", Snackbar.LENGTH_LONG).show();
    }

    protected SwipeRefreshLayout.OnRefreshListener getSwipeRefreshListener(){

        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AdhellReportViewModel adhellReportViewModel = ViewModelProviders.of(getActivity()).get(AdhellReportViewModel.class);
                adhellReportViewModel.getReportBlockedUrls().observe(getActivity(), reportBlockedUrls -> {
                    assert reportBlockedUrls != null;
                    ReportBlockedUrlAdapter reportBlockedUrlAdapter = new ReportBlockedUrlAdapter(Objects.requireNonNull(getContext()), reportBlockedUrls);
                    blockedDomainsListView.setAdapter(reportBlockedUrlAdapter);
                    lastDayBlockedTextView.setText(String.valueOf(reportBlockedUrls.size()));
                    reportBlockedUrlAdapter.notifyDataSetChanged();
                });
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (refreshRecentActivity != null) {
                            refreshRecentActivity.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        };
    }

}
