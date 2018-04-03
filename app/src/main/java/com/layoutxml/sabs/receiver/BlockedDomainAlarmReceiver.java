package com.layoutxml.sabs.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.layoutxml.sabs.service.BlockedDomainService;

public class BlockedDomainAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = BlockedDomainAlarmReceiver.class.getCanonicalName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received from alarmManager");
        Intent i = new Intent(context, BlockedDomainService.class);
        i.putExtra("launchedFrom", "alarm-receiver");
        //context.startService(i);
    }
}
