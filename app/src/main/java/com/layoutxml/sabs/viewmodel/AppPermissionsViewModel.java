package com.layoutxml.sabs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.layoutxml.sabs.db.AppDatabase;

import java.util.List;

public class AppPermissionsViewModel extends AndroidViewModel {
    private AppDatabase mAppDatabase;
    private List<String> permissions;

    public AppPermissionsViewModel(Application application) {
        super(application);
        mAppDatabase = AppDatabase.getAppDatabase(application);
    }
}
