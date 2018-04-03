package com.layoutxml.sabs.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.ReportBlockedUrl;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;
import com.sec.enterprise.firewall.DomainFilterReport;
import com.sec.enterprise.firewall.Firewall;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class BlockedDomainService extends IntentService {
    private static final String TAG = BlockedDomainService.class.getCanonicalName();

    @Nullable
    @Inject
    Firewall firewall;

    @Inject
    AppDatabase appDatabase;

    private DeviceAdminInteractor deviceAdminInteractor;

    public BlockedDomainService() {
        super(TAG);
        App.get().getAppComponent().inject(this);
        deviceAdminInteractor = DeviceAdminInteractor.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!deviceAdminInteractor.isContentBlockerSupported()) {
            return;
        }
        if (!deviceAdminInteractor.isKnoxEnabled()) {
            return;
        }
        ContentBlocker contentBlocker = deviceAdminInteractor.getContentBlocker();
        if (contentBlocker == null || !contentBlocker.isEnabled()) {
            return;
        }

        if (firewall == null) {
            return;
        }

        Log.d(TAG, "Saving domain list");
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 3600 * 1000 * 7);
        appDatabase.reportBlockedUrlDao().deleteBefore(yesterday);

        ReportBlockedUrl lastBlockedUrl = appDatabase.reportBlockedUrlDao().getLastBlockedDomain();
        long lastBlockedTimestamp = 0;
        if (lastBlockedUrl != null) {
            lastBlockedTimestamp = lastBlockedUrl.blockDate.getTime() / 1000;
        }

        List<ReportBlockedUrl> reportBlockedUrls = new ArrayList<>();
        List<DomainFilterReport> reports = firewall.getDomainFilterReport(null);
        if (reports == null) {
            return;
        }
        for (DomainFilterReport b : reports) {
            if (b.getTimeStamp() > lastBlockedTimestamp) {
                ReportBlockedUrl reportBlockedUrl =
                        new ReportBlockedUrl(b.getDomainUrl(), b.getPackageName(), new Date(b.getTimeStamp() * 1000));
                reportBlockedUrls.add(reportBlockedUrl);
            }
        }
        appDatabase.reportBlockedUrlDao().insertAll(reportBlockedUrls);
    }
}
