package com.komarov.meetings.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.komarov.meetings.service.MeetingsListService;

import java.util.Date;

public class BootReceiver extends BroadcastReceiver {

    private AlarmManager mAlarmMgr;
    private PendingIntent mAlarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String userId = intent.getStringExtra(MeetingsListService.USER_ID);
        final boolean toNotify = intent.getBooleanExtra(MeetingsListService.TO_NOTIFY, false);

        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MeetingsListService.class);
        i.setAction(MeetingsListService.ACTION_CHECK_DATA);
        i.putExtra(MeetingsListService.USER_ID, userId);
        i.putExtra(MeetingsListService.TO_NOTIFY, toNotify);

        mAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime(),
                1000 * 60 * 10, mAlarmIntent);
        MeetingsListService.startActionCheck(context, userId, toNotify);
    }
}
