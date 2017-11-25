package com.komarov.meetings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.komarov.meetings.fragment.AllMeetingsFragment;
import com.komarov.meetings.fragment.MyMeetingsFragment;
import com.komarov.meetings.fragment.SearchFragment;
import com.komarov.meetings.model.Meeting;
import com.komarov.meetings.receiver.BootReceiver;
import com.komarov.meetings.service.MeetingsListService;

import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String MY_MEETINGS_KEY = "myMeetings", RECENT_MEETINGS_KEY = "recentMeetings";

    private static final String TAG = "MainActivity";
    private List<Meeting> myMeetings, recentMeetings;
    private OnUpdateReceiver mOnUpdateReceiver;
    private AlarmManager mAlarmMgr;
    private PendingIntent mAlarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myMeetings = (List<Meeting>) getIntent().getSerializableExtra(MY_MEETINGS_KEY);
        recentMeetings = (List<Meeting>) getIntent().getSerializableExtra(RECENT_MEETINGS_KEY);

        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new MyMeetingsFragment(),
                    new AllMeetingsFragment(),
                    new SearchFragment()
            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_my_meetings),
                    getString(R.string.heading_meetings),
                    getString(R.string.search_Fragment_tab_title)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        findViewById(R.id.fab_new_meeting).setOnClickListener(view -> {
            final Intent intent = new Intent(MainActivity.this, NewMeetingActivity.class);
            startActivity(intent);
        });

        mOnUpdateReceiver = new OnUpdateReceiver();
        IntentFilter intentFilterTime = new IntentFilter(MeetingsListService.ACTION_LOAD_DATA);
        intentFilterTime.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mOnUpdateReceiver, intentFilterTime);

        initializeAlarmManager(this, getIntent());

//        updateMeetings();
    }

    private void initializeAlarmManager(Context context, Intent intent) {
        final String userId = intent.getStringExtra(MeetingsListService.USER_ID);
        final boolean toNotify = intent.getBooleanExtra(MeetingsListService.TO_NOTIFY, false);

        Intent i = new Intent(context, BootReceiver.class);
        i.setAction("com.komarov.meetings.broadcastReceiver.action.STARTED");
        i.putExtra(MeetingsListService.USER_ID, userId);
        i.putExtra(MeetingsListService.TO_NOTIFY, toNotify);

        sendBroadcast(i);
    }

    /*private void initializeAlarmManager(Context context, Intent intent) {
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
    }*/

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mOnUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_update) {
            updateMeetings();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public List<Meeting> getMyMeetings() {
        return myMeetings;
    }

    public List<Meeting> getRecentMeetings() {
        return recentMeetings;
    }

    public void updateMeetings() {
        MeetingsListService.startActionLoad(getApplicationContext(), getUid(), false);
    }

    public class OnUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            myMeetings = (List<Meeting>) intent.getSerializableExtra(MainActivity.MY_MEETINGS_KEY);
            recentMeetings = (List<Meeting>) intent.getSerializableExtra(MainActivity.RECENT_MEETINGS_KEY);
            Toast.makeText(getApplicationContext(), R.string.data_is_loaded_msg, Toast.LENGTH_LONG).show();
        }
    }
}
