package com.komarov.meetings.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.komarov.meetings.LoginActivity;
import com.komarov.meetings.service.MeetingsListService;

import java.util.Date;

public class BootReceiver extends BroadcastReceiver {

    private AlarmManager mAlarmMgr;
    private PendingIntent mAlarmIntent;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String userId = getUid();
        if (mAlarmMgr == null) {
            mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, MeetingsListService.class);
            i.setAction(MeetingsListService.ACTION_CHECK_DATA);
            i.putExtra(MeetingsListService.USER_ID, userId);

            mAlarmIntent = PendingIntent.getService(context, 0, i, 0);
            mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime(),
                    1000 * 60 * 10, mAlarmIntent);
//        MeetingsListService.startActionCheck(context, userId, toNotify);
        }
    }

    public String getUid() {
        final FirebaseUser currentUser = getCurrentUser();
        if (currentUser != null)
            return currentUser.getUid();
        else {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return null;
        }
    }

    public FirebaseUser getCurrentUser() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            return currentUser;
        else {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return null;
        }
    }
}
