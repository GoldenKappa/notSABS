package com.layoutxml.sabs.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.blocker.ContentBlocker56;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.utils.BlockedDomainAlarmHelper;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentBlocker contentBlocker = DeviceAdminInteractor.getInstance().getContentBlocker();
        if (contentBlocker != null && contentBlocker.isEnabled() && (contentBlocker instanceof ContentBlocker56
                || contentBlocker instanceof ContentBlocker57)) {
            BlockedDomainAlarmHelper.scheduleAlarm();
        }
    }
}
