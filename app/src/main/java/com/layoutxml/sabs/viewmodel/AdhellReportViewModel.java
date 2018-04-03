package com.layoutxml.sabs.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.ReportBlockedUrl;

import java.util.Date;
import java.util.List;

import static com.layoutxml.sabs.Global.RecentActivityDays;
import static com.layoutxml.sabs.Global.domainsToExport;

public class AdhellReportViewModel extends AndroidViewModel {
    private LiveData<List<ReportBlockedUrl>> reportBlockedUrls;
    private AppDatabase mDb;

    public AdhellReportViewModel(Application application) {
        super(application);
        mDb = AppDatabase.getAppDatabase(application);
    }

    public LiveData<List<ReportBlockedUrl>> getReportBlockedUrls() {
        reportBlockedUrls = new MutableLiveData<>();
        domainsToExport.clear();
        loadReportBlockedUrls();
        return reportBlockedUrls;
    }

    private void loadReportBlockedUrls() {
        reportBlockedUrls =
                mDb.reportBlockedUrlDao().getReportBlockUrlBetween(
                        new Date(System.currentTimeMillis() - 24 * 3600 * 1000 * RecentActivityDays), new Date());
    }

}
